package io.drevezerezh.scylla.advanced.webserver.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.drevezerezh.scylla.advanced.domain.api.player.*
import io.drevezerezh.scylla.advanced.domain.api.usecase.PlayerUseCaseManager
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerJson
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(PlayerController::class)
class PlayerControllerTest {

    @MockkBean
    lateinit var playerManagerUseCase: PlayerUseCaseManager

    @Autowired
    lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()


    @Test
    fun `getAllPlayers shall return empty list when no player`() {
        every { playerManagerUseCase.getAll() } returns emptyList()

        val response = mockMvc.perform(MockMvcRequestBuilders.get("/players"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        val content = response.response.contentAsString
        val players: List<String> = objectMapper.readValue(content, object : TypeReference<List<String>>() {})
        assertThat(players)
            .isEmpty()

        verify { playerManagerUseCase.getAll() }
    }


    @Test
    fun `getAllPlayers shall return complete player id list when contains several players`() {
        every { playerManagerUseCase.getAll() } returns listOf(
            Player("id1", "John"),
            Player("id2", "Jane"),
            Player("id3", "Walter"),
            Player("id4", "Laury")
        )

        val response = mockMvc.perform(MockMvcRequestBuilders.get("/players"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()

        val content = response.response.contentAsString
        val players: List<String> = objectMapper.readValue(content, object : TypeReference<List<String>>() {})
        assertThat(players)
            .containsOnly("id1", "id2", "id3", "id4")

        verify { playerManagerUseCase.getAll() }
    }


    @Test
    fun `getPlayerById shall return not found when player does not exist`() {
        every { playerManagerUseCase.getById("unknown") } throws PlayerNotFoundException("unknown")

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

        verify { playerManagerUseCase.getById("unknown") }
    }


    @Test
    fun `getPlayerById shall return the player`() {
        every { playerManagerUseCase.getById(JOHN.id) } returns JOHN

        val response = mockMvc.perform(MockMvcRequestBuilders.get("/players/${JOHN.id}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
        val content = response.response.contentAsString

        val actualPlayer = objectMapper.readValue(content, PlayerJson::class.java)
        assertThat(actualPlayer)
            .isEqualTo(PlayerMapper.toJson(JOHN))

        verify { playerManagerUseCase.getById(JOHN.id) }
    }


    @Test
    fun `createPlayer shall fail when name is invalid`() {
        val playerCreation = PlayerCreation(name = "")
        every { playerManagerUseCase.create(playerCreation) } throws InvalidPlayerAttributeException(
            "in-creation",
            setOf("name")
        )

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

        verify { playerManagerUseCase.create(playerCreation) }
    }


    @Test
    fun `createPlayer shall fail when name already exist`() {
        val playerCreation = PlayerCreation(name = JOHN.name)
        every { playerManagerUseCase.create(playerCreation) } throws PlayerAlreadyExistException(
            "in-creation",
            setOf("name")
        )

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
            .contains(400, "Player with id 'in-creation' already exists")

        assertThat(error.attributes)
            .containsOnly("name")

        verify { playerManagerUseCase.create(playerCreation) }
    }


    @Test
    fun `createPlayer shall create player when name does not exist`() {
        val playerCreation = PlayerCreation(name = JOHN.name)
        every { playerManagerUseCase.create(playerCreation) } returns JOHN

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

        verify { playerManagerUseCase.create(playerCreation) }
    }


    @Test
    fun `updatePlayer shall fail when player does not exist`() {
        val playerUpdate = PlayerUpdate(name = "Walter")
        every { playerManagerUseCase.update("unknown", playerUpdate) } throws PlayerNotFoundException("unknown")

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

        verify { playerManagerUseCase.update("unknown", playerUpdate) }
    }


    @Test
    fun `updatePlayer shall fail when player name is invalid`() {
        val playerUpdate = PlayerUpdate(name = "")
        every { playerManagerUseCase.update(JOHN.id, playerUpdate) } throws InvalidPlayerAttributeException(
            JOHN.id,
            setOf("name")
        )

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

        verify { playerManagerUseCase.update(JOHN.id, playerUpdate) }
    }


    @Test
    fun `updatePlayer shall update the name of the player`() {
        val expectedPlayer = Player(JOHN.id, "Walter")
        val update = PlayerUpdate(name = "Walter")
        every { playerManagerUseCase.update(JOHN.id, update) } returns expectedPlayer

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

        verify { playerManagerUseCase.update(JOHN.id, update) }
    }


    @Test
    fun `deletePlayer shall return not found when player does not exist`() {
        every { playerManagerUseCase.delete("unknown") } throws PlayerNotFoundException("unknown")

        val response = mockMvc.perform(MockMvcRequestBuilders.delete("/players/unknown"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn()
        val content = response.response.contentAsString

        val error: ErrorResponse = objectMapper.readValue(content, ErrorResponse::class.java)
        assertThat(error)
            .extracting("status", "title")
            .contains(404, "Cannot found player with id 'unknown'")

        verify { playerManagerUseCase.delete("unknown") }
    }


    @Test
    fun `deletePlayer shall delete the player when it exists`() {
        every { playerManagerUseCase.delete(JOHN.id) } returns true

        val response = mockMvc.perform(MockMvcRequestBuilders.delete("/players/${JOHN.id}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andReturn()
        val content = response.response.contentAsString

        assertThat(content)
            .isEmpty()

        verify { playerManagerUseCase.delete(JOHN.id) }
    }

    companion object {
        private val JOHN = Player("id-1", "John")
    }
}