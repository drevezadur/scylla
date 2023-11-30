/*
 * Copyright (c)  2023-2023.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.drevezadur.scylla.teacher.restserver.service

import io.drevezadur.scylla.teacher.client.command.CommandConsole
import io.drevezadur.scylla.teacher.restserver.lang.BattleStatus
import io.drevezadur.scylla.teacher.restserver.lang.FleetStatus
import io.drevezadur.scylla.teacher.restserver.lang.ShipStructuralStatus
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.persist.BattleRepository
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.service.model.FleetPojo
import io.drevezadur.scylla.teacher.restserver.service.model.LocationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.ShipPojo
import io.drevezadur.scylla.teacher.restserver.testutils.IntegrationContext
import io.drevezadur.scylla.teacher.restserver.testutils.IntegrationContextFactory
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.CREATE_PLAYERS_AND_BATTLE_SCRIPT
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.DEPLOY_JANE_FLEET_SCRIPT
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.DEPLOY_JOHN_FLEET_SCRIPT
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.FIGHT_UNTIL_JANE_VICTORY
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.ApplicationContext
import java.util.*

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BattleGameIntegTest {

    @Value(value = "\${local.server.port}")
    var port: Int = 0

    @Autowired
    lateinit var applicationContext: ApplicationContext


    @Autowired
    lateinit var playerRepository: PlayerRepository

    @Autowired
    lateinit var battleRepository: BattleRepository

    lateinit var integrationContext: IntegrationContext

    lateinit var console: CommandConsole

    var johnId: UUID = UUID.randomUUID()
    var janeId: UUID = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        if (!this::integrationContext.isInitialized) {
            integrationContext = IntegrationContextFactory.createContext(applicationContext, port)
        }

        console = integrationContext.console
    }


    @AfterEach
    fun tearDown() {
        integrationContext.resetAllData()
    }


    @Test
    fun `getAll shall return empty list when no battle`() {
        assertThat(console.getAllBattles())
            .isEmpty()
    }


    @Test
    fun `A fleet shall be deployed when all ships are deployed`() {
        assertThat(console.getAllBattles().isEmpty())
            .isTrue()
        integrationContext.runScripts(
            CREATE_PLAYERS_AND_BATTLE_SCRIPT,
            DEPLOY_JOHN_FLEET_SCRIPT
        )


        assertThat(fleetOf("John"))
            .extracting("status")
            .isSameAs(FleetStatus.UNHARMED)
    }

    private fun fleetOf(playerName: String): FleetPojo {
        return integrationContext.getFleetOf(playerName)
    }


    @Test
    fun `Game shall be started when all fleets are deployed`() {
        assertThat(console.getAllBattles().isEmpty())
            .isTrue()
        integrationContext.runScripts(
            CREATE_PLAYERS_AND_BATTLE_SCRIPT,
            DEPLOY_JOHN_FLEET_SCRIPT,
            DEPLOY_JANE_FLEET_SCRIPT
        )


        assertThat(fleetOf("John"))
            .extracting("status")
            .isSameAs(FleetStatus.UNHARMED)

        assertThat(fleetOf("Jane"))
            .extracting("status")
            .isSameAs(FleetStatus.UNHARMED)

        val battle = integrationContext.getCurrentBattle()
        assertThat(battle.status)
            .isSameAs(BattleStatus.DEPLOYED)
    }

    @Test
    fun `Game shall able firing of John when all fleets are deployed`() {
        assertThat(console.getAllBattles().isEmpty())
            .isTrue()
        integrationContext.runScripts(
            CREATE_PLAYERS_AND_BATTLE_SCRIPT,
        )

        integrationContext.runScripts(
            DEPLOY_JOHN_FLEET_SCRIPT,
            DEPLOY_JANE_FLEET_SCRIPT,
            """
                shot 00
            """.trimIndent()
        )

        assertThat(fleetOf("John"))
            .extracting("status")
            .isSameAs(FleetStatus.UNHARMED)

        assertThat(fleetOf("Jane"))
            .extracting("status")
            .isSameAs(FleetStatus.DAMAGED)

        assertThat(shipOf("Jane", ShipType.CARRIER))
            .extracting("status", "hits")
            .contains(ShipStructuralStatus.DAMAGED, listOf(LocationPojo(0, 0)))


        val battle = integrationContext.getCurrentBattle()
        assertThat(battle.status)
            .isSameAs(BattleStatus.RUNNING)
    }

    private fun shipOf(playerName: String, shipType: ShipType): ShipPojo {
        return integrationContext.getShipOf(playerName, shipType)
    }


    @Test
    fun `Game shall able firing of Jane after John`() {
        assertThat(console.getAllBattles().isEmpty())
            .isTrue()
        integrationContext.runScripts(
            CREATE_PLAYERS_AND_BATTLE_SCRIPT,
        )

        println("battleId=" + integrationContext.getCurrentBattleId())
        println("John playerId=" + integrationContext.getPlayerId("John"))
        println("Jane playerId=" + integrationContext.getPlayerId("Jane"))

        integrationContext.runScripts(
            DEPLOY_JOHN_FLEET_SCRIPT,
            DEPLOY_JANE_FLEET_SCRIPT,
            """
                shot 00
                shot 01
            """.trimIndent()
        )

        assertThat(fleetOf("John"))
            .extracting("status")
            .isSameAs(FleetStatus.DAMAGED)

        assertThat(fleetOf("Jane"))
            .extracting("status")
            .isSameAs(FleetStatus.DAMAGED)

        assertThat(shipOf("Jane", ShipType.CARRIER))
            .extracting("status", "hits")
            .contains(ShipStructuralStatus.DAMAGED, listOf(LocationPojo(0, 0)))

        assertThat(shipOf("John", ShipType.BATTLESHIP))
            .extracting("status", "hits")
            .contains(ShipStructuralStatus.DAMAGED, listOf(LocationPojo(0, 1)))


        val battle = integrationContext.getCurrentBattle()
        assertThat(battle.status)
            .isSameAs(BattleStatus.RUNNING)
    }


    @Test
    fun `Game shall end`() {
        assertThat(console.getAllBattles().isEmpty())
            .isTrue()
        integrationContext.runScripts(
            CREATE_PLAYERS_AND_BATTLE_SCRIPT,
        )

        integrationContext.runScripts(
            DEPLOY_JOHN_FLEET_SCRIPT,
            DEPLOY_JANE_FLEET_SCRIPT,
            FIGHT_UNTIL_JANE_VICTORY
        )

        val johnId = integrationContext.getPlayerId("John")
        val janeId = integrationContext.getPlayerId("Jane")

        assertThat(integrationContext.getCurrentBattle())
            .extracting("status", "winner", "shootingPlayer")
            .containsExactly(BattleStatus.FINISHED, janeId, johnId)
    }
}