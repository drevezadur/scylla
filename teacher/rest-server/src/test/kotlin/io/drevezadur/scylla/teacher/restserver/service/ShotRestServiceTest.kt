package io.drevezadur.scylla.teacher.restserver.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.drevezadur.scylla.teacher.client.command.CommandConsole
import io.drevezadur.scylla.teacher.restserver.domain.model.ShotReport
import io.drevezadur.scylla.teacher.restserver.lang.BattleStatus
import io.drevezadur.scylla.teacher.restserver.lang.ShotResult
import io.drevezadur.scylla.teacher.restserver.persist.BattleRepository
import io.drevezadur.scylla.teacher.restserver.persist.FleetRepository
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.persist.ShipRepository
import io.drevezadur.scylla.teacher.restserver.service.model.LocationPojo
import io.drevezadur.scylla.teacher.restserver.service.util.Attribute
import io.drevezadur.scylla.teacher.restserver.service.util.HttpErrorContent
import io.drevezadur.scylla.teacher.restserver.testutils.IntegrationContext
import io.drevezadur.scylla.teacher.restserver.testutils.IntegrationContextFactory
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.CREATE_PLAYERS_AND_BATTLE_SCRIPT
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.DEPLOY_JANE_FLEET_SCRIPT
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.DEPLOY_JOHN_FLEET_SCRIPT
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShotRestServiceTest {

    @Value(value = "\${local.server.port}")
    var port: Int = 0

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var playerRepository: PlayerRepository

    @Autowired
    lateinit var battleRepository: BattleRepository

    @Autowired
    lateinit var fleetRepository: FleetRepository

    @Autowired
    lateinit var shipRepository: ShipRepository

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
    fun `fireAtFleet shall fail when battle does not exist`() {
        val response = fireAt(UNKNOWN_ID, johnId, 2, 4)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.NOT_FOUND)

        val errorContent = toErrorContent(response)
        assertThat(errorContent.errorCode)
            .isEqualTo("BATTLE_NOT_FOUND")
        assertThat(errorContent.attributes)
            .containsOnly(Attribute("battleId", "$UNKNOWN_ID"))
    }

    private fun fireAt(battleId: UUID, fromPlayerId: UUID, x: Int, y: Int): ResponseEntity<String> {
        val target = LocationPojo(x, y)
        val header = HttpJsonHelper.createWriteHttpEntity(target)
        val url = "/battles/$battleId/players/$fromPlayerId/fleet/shot"
        return restTemplate.exchange(url, HttpMethod.POST, header, String::class.java)
    }

    private fun toErrorContent(response: ResponseEntity<String>): HttpErrorContent {
        return jacksonObjectMapper().readValue(response.body!!)
    }


    @Test
    fun `fireAtFleet shall fail when player does not exist`() {
        val response = fireAt(battleId, UNKNOWN_ID, 2, 4)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.NOT_FOUND)

        val errorContent = toErrorContent(response)
        assertThat(errorContent.errorCode)
            .isEqualTo("PLAYER_NOT_FOUND")
        assertThat(errorContent.attributes)
            .containsOnly(Attribute("playerId", "$UNKNOWN_ID"))
    }


    @Test
    fun `fireAtFleet shall fail when player is not in battle`() {
        integrationContext.runScript("createPlayer Janine")
        val janineId = integrationContext.getPlayerId("Janine")


        val response = fireAt(battleId, janineId, 2, 4)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.NOT_FOUND)

        val errorContent = toErrorContent(response)
        assertThat(errorContent.errorCode)
            .isEqualTo("PLAYER_NOT_IN_BATTLE")
        assertThat(errorContent.attributes)
            .containsOnly(
                Attribute("battleId", "$battleId"),
                Attribute("playerId", "$janineId")
            )
    }


    @Test
    fun `fireAtFleet shall fail when player is not deployed`() {
        integrationContext.runScript(DEPLOY_JOHN_FLEET_SCRIPT)

        val response = fireAt(battleId, janeId, 2, 4)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.BAD_REQUEST)

        val errorContent = toErrorContent(response)
        assertThat(errorContent.errorCode)
            .isEqualTo("UNEXPECTED_BATTLE_STATUS")
        assertThat(errorContent.attributes)
            .containsOnly(
                Attribute("battleId", "$battleId"),
                Attribute("currentStatus", "${BattleStatus.DEPLOYMENT}"),
                Attribute("expectedStatus", "${BattleStatus.RUNNING}")
            )
    }


    @Test
    fun `fireAtFleet shall fail when player is deployed but not the opponent`() {
        integrationContext.runScript(DEPLOY_JOHN_FLEET_SCRIPT)

        val response = fireAt(battleId, johnId, 2, 4)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.BAD_REQUEST)

        val errorContent = toErrorContent(response)
        assertThat(errorContent.errorCode)
            .isEqualTo("UNEXPECTED_BATTLE_STATUS")
        assertThat(errorContent.attributes)
            .containsOnly(
                Attribute("battleId", "$battleId"),
                Attribute("currentStatus", "${BattleStatus.DEPLOYMENT}"),
                Attribute("expectedStatus", "${BattleStatus.RUNNING}")
            )
    }


    @Test
    fun `fireAtFleet shall support missed shot`() {
        integrationContext.runScripts(DEPLOY_JOHN_FLEET_SCRIPT, DEPLOY_JANE_FLEET_SCRIPT)

        val response = fireAt(battleId, johnId, 9, 9)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        val shotReport = toShotReport(response)
        assertThat(shotReport)
            .extracting("shotResult", "winner")
            .containsOnly(ShotResult.MISSED, false)
    }

    private fun toShotReport(response: ResponseEntity<String>): ShotReport {
        return jacksonObjectMapper().readValue(response.body!!)
    }

    @Test
    fun `fireAtFleet shall support first hit shot`() {
        integrationContext.runScripts(DEPLOY_JOHN_FLEET_SCRIPT, DEPLOY_JANE_FLEET_SCRIPT)

        val response = fireAt(battleId, johnId, 0, 0)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        val shotReport = toShotReport(response)
        assertThat(shotReport)
            .extracting("shotResult", "winner")
            .containsOnly(ShotResult.HIT, false)
    }

    @Test
    fun `fireAtFleet shall support second hit shot`() {
        integrationContext.runScripts(
            DEPLOY_JOHN_FLEET_SCRIPT, DEPLOY_JANE_FLEET_SCRIPT,
            """
                shot 00 # Jane Carrier 1/5
                shot 00 # John Carrier 1/5
            """.trimIndent()
        )

        val response = fireAt(battleId, johnId, 0, 1)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        val shotReport = toShotReport(response)
        assertThat(shotReport)
            .extracting("shotResult", "winner")
            .containsOnly(ShotResult.HIT, false)
    }

    @Test
    fun `fireAtFleet shall support sunk hit shot`() {
        integrationContext.runScripts(
            DEPLOY_JOHN_FLEET_SCRIPT, DEPLOY_JANE_FLEET_SCRIPT,
            """
                shot 00 # Jane Carrier 1/5
                shot 00 # John Carrier 1/5
                
                shot 01 # Jane Carrier 2/5
                shot 10 # John Carrier 2/5

                shot 02 # Jane Carrier 3/5
                shot 20 # John Carrier 3/5

                shot 03 # Jane Carrier 4/5 
                shot 30 # John Carrier 4/5
            """.trimIndent()
        )

        val response = fireAt(battleId, johnId, 0, 4)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        val shotReport = toShotReport(response)
        assertThat(shotReport)
            .extracting("shotResult", "winner")
            .containsOnly(ShotResult.SUNK, false)
    }


    @Test
    fun `fireAtFleet shall support sunk hit shot and win the battle`() {
        integrationContext.runScripts(
            DEPLOY_JOHN_FLEET_SCRIPT, DEPLOY_JANE_FLEET_SCRIPT,
            """
                shot 00 # Jane Carrier 1/5
                shot 00 # John Carrier 1/5
                
                shot 01 # Jane Carrier 2/5
                shot 10 # John Carrier 2/5

                shot 02 # Jane Carrier 3/5
                shot 20 # John Carrier 3/5

                shot 03 # Jane Carrier 4/5 
                shot 30 # John Carrier 4/5

                shot 04 # Jane Carrier 5/5 
                shot 40 # John Carrier 5/5
                
                shot 06 # Jane water
                shot 01 # John Battleship 1/4
                
                shot 16 # Jane water
                shot 11 # John Battleship 2/4
                
                shot 26 # Jane water
                shot 21 # John Battleship 3/4
                
                shot 36 # Jane water
                shot 31 # John Battleship 4/4

                shot 46 # Jane water
                shot 02 # John Cruiser 1/3
                
                shot 56 # Jane water
                shot 12 # John Cruiser 2/3
                
                shot 66 # Jane water
                shot 22 # John Cruiser 3/3
                
                shot 76 # Jane water
                shot 03 # John Submarine 1/3
                
                shot 86 # Jane water
                shot 13 # John Submarine 2/3
                
                shot 96 # Jane water
                shot 23 # John Submarine 3/3
                
                shot 07 # Jane water
                shot 04 # John Destroyer 1/2
                
                shot 17 # Jane water
            """.trimIndent()
        )

        val response = fireAt(battleId, janeId, 1, 4)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        val shotReport = toShotReport(response)
        assertThat(shotReport)
            .extracting("shotResult", "winner")
            .containsOnly(ShotResult.SUNK, true)
    }

    @Test
    fun `fireAtFleet shall support already hit shot`() {
        integrationContext.runScripts(DEPLOY_JOHN_FLEET_SCRIPT, DEPLOY_JANE_FLEET_SCRIPT)

        fireAt(battleId, johnId, 0, 0)
        fireAt(battleId, janeId, 0, 0)

        val response = fireAt(battleId, johnId, 0, 0)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)

        val shotReport = toShotReport(response)
        assertThat(shotReport)
            .extracting("shotResult", "winner")
            .containsOnly(ShotResult.ALREADY_SHOT, false)
    }

    companion object {
        val UNKNOWN_ID: UUID = UUID.randomUUID()!!
    }
}