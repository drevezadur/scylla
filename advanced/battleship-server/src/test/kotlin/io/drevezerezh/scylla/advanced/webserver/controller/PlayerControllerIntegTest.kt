package io.drevezerezh.scylla.advanced.webserver.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.drevezerezh.scylla.advanced.domain.api.Player
import io.drevezerezh.scylla.advanced.domain.api.PlayerManager
import io.drevezerezh.scylla.advanced.domain.api.PlayerStore
import io.drevezerezh.scylla.advanced.domain.impl.IdProvider
import io.drevezerezh.scylla.advanced.domain.impl.PlayerManagerBean
import io.drevezerezh.scylla.advanced.persistance.MemoryPlayerStoreBean
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerJson
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(PlayerController::class)
class PlayerControllerIntegTest {

    @Autowired
    lateinit var playerStore: PlayerStore

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var idProvider: IdProvider

    private val objectMapper = ObjectMapper()

    @TestConfiguration
    internal class AdditionalConfig {
        @Bean
        fun playerStore() : PlayerStore {
            return MemoryPlayerStoreBean()
        }

        @Bean
        fun playerManager(idProvider: IdProvider,playerStore: PlayerStore): PlayerManager {
            return PlayerManagerBean(idProvider, playerStore)
        }
    }

    @BeforeEach
    fun setUp() {
        playerStore.deleteAll()
    }

    @Test
    fun `getAllPlayers shall return empty list when no player`() {
        val response = mockMvc.perform(MockMvcRequestBuilders.get("/players"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        val content = response.response.contentAsString
        val players: List<String> = objectMapper.readValue(content, object : TypeReference<List<String>>() {})
        assertThat(players)
            .isEmpty()
    }


    @Test
    fun `getAllPlayers shall return complete player id list when contains several players`() {
        playerStore.saveAll(Player("id1", "John"), Player("id2", "Jane"),
            Player("id3", "Walter"),
            Player("id4", "Laury"))


        val response = mockMvc.perform(MockMvcRequestBuilders.get("/players"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        val content = response.response.contentAsString
        val players: List<String> = objectMapper.readValue(content, object : TypeReference<List<String>>() {})
        assertThat(players)
            .containsOnly("id1", "id2", "id3", "id4")
    }


    @Test
    fun `getPlayerById shall return not found when player does not exist`() {
        val response = mockMvc.perform(MockMvcRequestBuilders.get("/players/unknown"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn()
        val content = response.response.contentAsString

        val error: ErrorResponse = objectMapper.readValue(content, ErrorResponse::class.java)
        assertThat(error)
            .extracting("status", "title")
            .contains(404, "Cannot found player with id 'unknown'")
    }


    @Test
    fun `getPlayerById shall return the player`() {
        playerStore.save(JOHN)

        val response = mockMvc.perform(MockMvcRequestBuilders.get("/players/${JOHN.id}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
        val content = response.response.contentAsString

        val actualPlayer = objectMapper.readValue(content, PlayerJson::class.java)
        assertThat(actualPlayer)
            .isEqualTo(PlayerMapper.toJson(JOHN))
    }


    @Test
    fun `createPlayer shall fail when name is invalid`() {
        every { idProvider.createId() } returns  "in-creation"

        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/players")
                .content(
                    """
                { "name": "" }
            """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn()

        val content = response.response.contentAsString
        val error: ErrorResponse = objectMapper.readValue(content, ErrorResponse::class.java)
        assertThat(error)
            .extracting("status", "title")
            .contains(400, "Illegal attributes for player with id 'in-creation'")

        assertThat(error.attributes)
            .contains("name")
    }


    @Test
    fun `createPlayer shall fail when name already exist`() {
        every { idProvider.createId() } returns  "in-creation"
        playerStore.save(JOHN)

        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/players")
                .content(
                    """
                { "name": "${JOHN.name}" }
            """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn()

        val content = response.response.contentAsString
        val error: ErrorResponse = objectMapper.readValue(content, ErrorResponse::class.java)
        assertThat(error)
            .extracting("status", "title")
            .contains(400, "Player with id '${JOHN.id}' already exists")

        assertThat(error.attributes)
            .containsOnly("name")

        verify { idProvider.createId() }
    }


    @Test
    fun `createPlayer shall create player when name does not exist`() {
        every { idProvider.createId() } returns  JOHN.id

        val expectedLocation = "http://localhost/players/${JOHN.id}"

        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/players")
                .content(
                    """
                { "name": "${JOHN.name}" }
            """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("location", expectedLocation))
            .andReturn()

        val content = response.response.contentAsString
        assertThat(content)
            .isEmpty()
    }


    @Test
    fun `updatePlayer shall fail when player does not exist`() {
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/players/unknown")
                .content(
                    """
                { "name": "Walter" }
            """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn()
        val content = response.response.contentAsString

        val error: ErrorResponse = objectMapper.readValue(content, ErrorResponse::class.java)
        assertThat(error)
            .extracting("status", "title")
            .contains(404, "Cannot found player with id 'unknown'")
    }


    @Test
    fun `updatePlayer shall fail when player name is invalid`() {
        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/players/${JOHN.id}")
                .content(
                    """
                { "name": "" }
            """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andReturn()
        val content = response.response.contentAsString

        val error: ErrorResponse = objectMapper.readValue(content, ErrorResponse::class.java)
        assertThat(error)
            .extracting("status", "title")
            .contains(400, "Illegal attributes for player with id '${JOHN.id}'")
        assertThat(error.attributes)
            .contains("name")
    }


    @Test
    fun `updatePlayer shall update the name of the player`() {
        playerStore.save(JOHN)

        val expectedPlayer = Player(JOHN.id, "Walter")

        val response = mockMvc.perform(
            MockMvcRequestBuilders.put("/players/${JOHN.id}")
                .content(
                    """
                { "name": "Walter" }
            """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
        val content = response.response.contentAsString
        val actualPlayer = objectMapper.readValue(content, PlayerJson::class.java)

        assertThat(actualPlayer)
            .isEqualTo(PlayerMapper.toJson(expectedPlayer))
    }


    @Test
    fun `deletePlayer shall return not found when player does not exist`() {
        val response = mockMvc.perform(MockMvcRequestBuilders.delete("/players/unknown"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn()
        val content = response.response.contentAsString

        val error: ErrorResponse = objectMapper.readValue(content, ErrorResponse::class.java)
        assertThat(error)
            .extracting("status", "title")
            .contains(404, "Cannot found player with id 'unknown'")
    }


    @Test
    fun `deletePlayer shall delete the player when it exists`() {
        playerStore.save(JOHN)

        val response = mockMvc.perform(MockMvcRequestBuilders.delete("/players/${JOHN.id}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andReturn()
        val content = response.response.contentAsString

        assertThat(content)
            .isEmpty()
    }

    companion object {
        private val JOHN = Player("id-1", "John")
    }
}