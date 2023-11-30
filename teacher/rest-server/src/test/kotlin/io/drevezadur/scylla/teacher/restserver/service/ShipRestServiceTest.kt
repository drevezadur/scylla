package io.drevezadur.scylla.teacher.restserver.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.drevezadur.scylla.teacher.client.command.CommandConsole
import io.drevezadur.scylla.teacher.restserver.lang.*
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.service.model.LocationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.ShipDeploymentBody
import io.drevezadur.scylla.teacher.restserver.service.model.ShipPojo
import io.drevezadur.scylla.teacher.restserver.service.util.Attribute
import io.drevezadur.scylla.teacher.restserver.service.util.HttpErrorContent
import io.drevezadur.scylla.teacher.restserver.testutils.IntegrationContext
import io.drevezadur.scylla.teacher.restserver.testutils.IntegrationContextFactory
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.CREATE_PLAYERS_AND_BATTLE_SCRIPT
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper.createWriteHttpEntity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContext
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShipRestServiceTest {

    @Value(value = "\${local.server.port}")
    var port: Int = 0

    @Autowired
    lateinit var applicationContext: ApplicationContext


    @Autowired
    lateinit var playerRepository: PlayerRepository

    lateinit var integrationContext: IntegrationContext

    lateinit var console: CommandConsole

    lateinit var restTemplate: TestRestTemplate

    lateinit var battleId: UUID
    lateinit var johnId: UUID
    lateinit var janeId: UUID

    @BeforeEach
    fun setUp() {
        if (!this::integrationContext.isInitialized) {
            integrationContext = IntegrationContextFactory.createContext(applicationContext, port)
        }

        restTemplate = applicationContext.getBean(TestRestTemplate::class.java)

        console = integrationContext.console
        integrationContext.runScript(CREATE_PLAYERS_AND_BATTLE_SCRIPT)

        battleId = integrationContext.getCurrentBattleId()
        johnId = integrationContext.getPlayerId("John")
        janeId = integrationContext.getPlayerId("Jane")
    }

    @AfterEach
    fun tearDown() {
        integrationContext.resetAllData()
    }

    @Test
    fun `deployShip shall deploy a ship already deployed`() {
        deployJohnShip(ShipType.SUBMARINE, 3, 0, GridOrientation.COLUMN)

        val response = deployJohnShip(ShipType.SUBMARINE, 0, 3, GridOrientation.ROW)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.CREATED)

        assertThat(response.headers["Location"]!![0])
            .isEqualTo("/battles/$battleId/players/$johnId/fleet/ships/SUBMARINE")

        assertOnlyShipDeployed(johnId, Ship(ShipType.SUBMARINE, 0, 3, GridOrientation.ROW))
    }

    private data class Ship(
        val shipType: ShipType,
        val x: Int,
        val y: Int,
        val orientation: GridOrientation,
        val status: ShipStructuralStatus = ShipStructuralStatus.UNHARMED,
        val hits: List<LocationPojo> = emptyList()
    ) {
        fun toPojo(battleId: BattleUUID, playerId: PlayerUUID): ShipPojo {
            return ShipPojo(battleId, playerId, shipType, status, LocationPojo(x, y), orientation, hits)
        }
    }

    private fun assertOnlyShipDeployed(playerId: PlayerUUID, vararg ships: Ship) {
        ShipAssertion(battleId, playerId, console)
            .assertOnlyDeployed(ships.toList())
    }

    private class ShipAssertion(
        private val battleId: BattleUUID,
        private val playerId: PlayerUUID,
        console: CommandConsole
    ) {
        private val storedShipList: List<ShipPojo> = console.getAllShipsInFleet(battleId, playerId)
        fun assertOnlyDeployed(ships: List<Ship>) {
            val shipList: List<ShipPojo> = ships.map { it.toPojo(battleId, playerId) }
            assertThat(shipList)
                .hasSize(storedShipList.size)
                .containsAll(storedShipList)
        }
    }

    private fun deployJohnShip(type: ShipType, x: Int, y: Int, orientation: GridOrientation): ResponseEntity<String> {
        val shipDeploymentBody = ShipDeploymentBody(type, x, y, orientation)
        val header = createWriteHttpEntity(shipDeploymentBody)
        val url = "http://localhost:$port/battles/$battleId/players/$johnId/fleet/ships"
        return restTemplate.exchange(url, HttpMethod.POST, header, String::class.java)
    }


    @Test
    fun `deployShip shall not deploy a ship on same location of a previous and distinct ship`() {
        deployJohnShip(ShipType.SUBMARINE, 0, 0, GridOrientation.COLUMN)

        val response = deployJohnShip(ShipType.DESTROYER, 0, 0, GridOrientation.ROW)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.BAD_REQUEST)

        val errorContent = jacksonObjectMapper().readValue<HttpErrorContent>(response.body!!)

        assertThat(errorContent.errorCode)
            .isEqualTo("SHIP_DEPLOYMENT_COLLISION")

        assertThat(errorContent.attributes)
            .containsOnly(
                Attribute("battleId", battleId.toString()),
                Attribute("playerId", johnId.toString()),
                Attribute("shipType", ShipType.DESTROYER.name),
                Attribute("location", "00")
            )
    }


    @Test
    fun `deployShip shall deploy a ship not previously deployed`() {
        val response = deployJohnShip(ShipType.SUBMARINE, 0, 3, GridOrientation.ROW)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.CREATED)

        assertThat(response.headers["Location"]!![0])
            .isEqualTo("/battles/$battleId/players/$johnId/fleet/ships/SUBMARINE")

        assertOnlyShipDeployed(johnId, Ship(ShipType.SUBMARINE, 0, 3, GridOrientation.ROW))
    }


    @Test
    fun `getAllShipsInFleet shall return empty ship list when player and battle exist`() {
        val header = HttpJsonHelper.createReadHttpEntity()
        val url = "/battles/$battleId/players/$johnId/fleet/ships"

        val paramTypeReference: ParameterizedTypeReference<List<ShipPojo>> =
            object : ParameterizedTypeReference<List<ShipPojo>>() {}
        val response = restTemplate.exchange(url, HttpMethod.GET, header, paramTypeReference)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)
        assertThat(response.body)
            .isEmpty()
    }


    @Test
    fun `getAllShipsInFleet shall return all ships in a specific fleet`() {
        console.deployShip(battleId, johnId, ShipDeploymentBody(ShipType.CRUISER, 0, 0, GridOrientation.COLUMN))
        console.deployShip(battleId, johnId, ShipDeploymentBody(ShipType.DESTROYER, 1, 0, GridOrientation.COLUMN))

        console.deployShip(battleId, janeId, ShipDeploymentBody(ShipType.BATTLESHIP, 0, 0, GridOrientation.ROW))
        console.deployShip(battleId, janeId, ShipDeploymentBody(ShipType.SUBMARINE, 0, 1, GridOrientation.ROW))


        val header = HttpJsonHelper.createReadHttpEntity()
        val url = "/battles/$battleId/players/$johnId/fleet/ships"

        val response = restTemplate.exchange(url, HttpMethod.GET, header, String::class.java)


        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        val typeReference: TypeReference<List<ShipPojo>> = object : TypeReference<List<ShipPojo>>() {}
        val shipList = jacksonObjectMapper().readValue(response.body, typeReference)
        assertThat(shipList)
            .hasSize(2)
            .extracting("battleId", "playerId", "type")
            .containsOnly(
                Tuple(battleId, johnId, ShipType.CRUISER),
                Tuple(battleId, johnId, ShipType.DESTROYER)
            )
    }

    @Test
    fun `getShip shall thrown an error code when it does not exist`() {
        console.deployShip(battleId, johnId, ShipDeploymentBody(ShipType.CRUISER, 0, 0, GridOrientation.COLUMN))
        console.deployShip(battleId, johnId, ShipDeploymentBody(ShipType.DESTROYER, 1, 0, GridOrientation.COLUMN))

        console.deployShip(battleId, janeId, ShipDeploymentBody(ShipType.BATTLESHIP, 0, 0, GridOrientation.ROW))
        console.deployShip(battleId, janeId, ShipDeploymentBody(ShipType.SUBMARINE, 0, 1, GridOrientation.ROW))


        val header = HttpJsonHelper.createReadHttpEntity()
        val url = "/battles/$battleId/players/$johnId/fleet/ships/CARRIER"

        val response = restTemplate.exchange(url, HttpMethod.GET, header, String::class.java)


        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.NOT_FOUND)
    }


    @Test
    fun `getShip shall provide the ship when it exists`() {
        console.deployShip(battleId, johnId, ShipDeploymentBody(ShipType.CRUISER, 0, 0, GridOrientation.COLUMN))
        console.deployShip(battleId, johnId, ShipDeploymentBody(ShipType.DESTROYER, 1, 0, GridOrientation.COLUMN))

        console.deployShip(battleId, janeId, ShipDeploymentBody(ShipType.BATTLESHIP, 0, 0, GridOrientation.ROW))
        console.deployShip(battleId, janeId, ShipDeploymentBody(ShipType.SUBMARINE, 0, 1, GridOrientation.ROW))


        val header = HttpJsonHelper.createReadHttpEntity()
        val url = "/battles/$battleId/players/$johnId/fleet/ships/CRUISER"

        val response = restTemplate.exchange(url, HttpMethod.GET, header, String::class.java)


        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        val ship = jacksonObjectMapper().readValue(response.body, ShipPojo::class.java)

        assertThat(ship)
            .extracting("battleId", "playerId", "type")
            .containsExactly(battleId, johnId, ShipType.CRUISER)
    }
}