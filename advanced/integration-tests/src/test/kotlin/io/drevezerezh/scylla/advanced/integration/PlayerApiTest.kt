package io.drevezerezh.scylla.advanced.integration

import io.drevezerezh.scylla.advanced.integration.player.*
import io.drevezerezh.scylla.advanced.scenario.dsl.ScenarioParserFactory
import okhttp3.OkHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class PlayerApiTest {

    private val httpClient = OkHttpClient()
    private val context = ServerContext(httpClient, "http://localhost:11000/battleship")
    private val playerApi: PlayerApi = PlayerApiBean(context)

    private val scenarioParser = ScenarioParserFactory().create()


    @BeforeEach
    fun setUp() {
        playerApi.deleteAll()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `getAllIds shall return empty list at the beginning`() {
        assertThat(playerApi.getAllIds())
            .isEmpty()
    }

    @Test
    fun `create player John shall success`() {
        val johnId = playerApi.create(PlayerCreation("John"))
        assertThat(johnId)
            .isNotEmpty()
    }

    @Test
    fun `update player John shall rename to Walter`() {
        val johnId = playerApi.create(PlayerCreation("John"))

        val walter = playerApi.update(johnId, PlayerUpdate(name = "Walter"))
        assertThat(walter)
            .isEqualTo(PlayerJson(johnId, "Walter"))

    }

    @Test
    fun `delete player John shall delete player`() {
        val johnId = playerApi.create(PlayerCreation("John"))
        playerApi.delete(johnId)

        assertThat(playerApi.findById(johnId))
            .isNull()
    }

    @Test
    fun `get all shall return all players ids`() {
        val johnId = playerApi.create(PlayerCreation("John"))
        val janeId = playerApi.create(PlayerCreation("Jane"))

        assertThat(playerApi.getAllIds())
            .containsOnly(johnId, janeId)
    }

    @Test
    fun `get all shall return all players`() {
        val johnId = playerApi.create(PlayerCreation("John"))
        val janeId = playerApi.create(PlayerCreation("Jane"))

        assertThat(playerApi.getAll())
            .hasSize(2)
            .containsOnly(
                PlayerJson(johnId, "John"),
                PlayerJson(janeId, "Jane")
            )
    }
}