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

package io.drevezadur.scylla.teacher.restserver.domain.internal.usecase

import io.drevezadur.scylla.teacher.client.command.CommandConsole
import io.drevezadur.scylla.teacher.client.command.CommandConsoleFactory
import io.drevezadur.scylla.teacher.restserver.lang.BattleStatus
import io.drevezadur.scylla.teacher.restserver.lang.FleetStatus
import io.drevezadur.scylla.teacher.restserver.persist.BattleRepository
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.testutils.IntegrationContext
import io.drevezadur.scylla.teacher.restserver.testutils.IntegrationContextFactory
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.CREATE_PLAYERS_AND_BATTLE_SCRIPT
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.DEPLOY_JANE_FLEET_SCRIPT
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.DEPLOY_JOHN_FLEET_SCRIPT
import io.drevezadur.scylla.teacher.restserver.testutils.PlayScripts.FIGHT_UNTIL_JANE_VICTORY
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*


@ExtendWith(SpringExtension::class)
@DataJpaTest
@Import(UseCaseConfiguration::class)
class UseCaseIntegrationTest {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var battleRepository: BattleRepository

    @Autowired
    lateinit var playerRepository: PlayerRepository

    @Autowired
    lateinit var entityManager: EntityManager


    lateinit var integrationContext: IntegrationContext

    lateinit var console: CommandConsole

    var battleId: UUID = UUID.randomUUID()
    var johnId: UUID = UUID.randomUUID()
    var janeId: UUID = UUID.randomUUID()


    @BeforeEach
    fun setUp() {
        if (!this::integrationContext.isInitialized) {
            integrationContext = IntegrationContextFactory.createContext(applicationContext)
        }

        CommandConsoleFactory.createUseCaseCommandConsole(applicationContext)
        console = integrationContext.console
    }


    @AfterEach
    fun tearDown() {
        integrationContext.resetAllData()
    }

    @Test
    fun createPlayers() {
        integrationContext.runScript(
            """
                createPlayer John
                createPlayer Jane
        """.trimIndent()
        )

        assertThat(console.getAllPlayers())
            .hasSize(2)
            .extracting("name")
            .containsOnly("John", "Jane")
    }

    @Test
    fun createBattle() {
        integrationContext.runScript(CREATE_PLAYERS_AND_BATTLE_SCRIPT)
        initializePlayerAndBattleIds()

        assertThat(console.getAllBattles())
            .hasSize(1)

        assertThat(console.getAllBattles().first())
            .extracting("player1Id", "player2Id")
            .containsExactly(johnId, janeId)
    }


    private fun initializePlayerAndBattleIds() {
        battleId = integrationContext.getCurrentBattleId()
        johnId = integrationContext.getPlayerId("John")
        janeId = integrationContext.getPlayerId("Jane")
    }


    @Test
    fun deploySingleFleet() {
        integrationContext.runScripts(CREATE_PLAYERS_AND_BATTLE_SCRIPT, DEPLOY_JOHN_FLEET_SCRIPT)
        initializePlayerAndBattleIds()

        val johnFleet = console.findFleetById(battleId, johnId)!!

        assertThat(johnFleet.status)
            .isSameAs(FleetStatus.UNHARMED)

        val battle = console.findBattleById(battleId)!!
        assertThat(battle.status)
            .isSameAs(BattleStatus.DEPLOYMENT)
    }

    @Test
    fun deployAllFleets() {
        integrationContext.runScripts(
            CREATE_PLAYERS_AND_BATTLE_SCRIPT,
            DEPLOY_JOHN_FLEET_SCRIPT,
            DEPLOY_JANE_FLEET_SCRIPT
        )
        initializePlayerAndBattleIds()

        val johnFleet = console.findFleetById(battleId, johnId)!!
        val janeFleet = console.findFleetById(battleId, janeId)!!

        assertThat(johnFleet.status)
            .isSameAs(FleetStatus.UNHARMED)
        assertThat(janeFleet.status)
            .isSameAs(FleetStatus.UNHARMED)

        val battle = console.findBattleById(battleId)!!
        assertThat(battle.status)
            .isSameAs(BattleStatus.DEPLOYED)
    }


    @Test
    fun playFullBattle() {
        integrationContext.runScripts(
            CREATE_PLAYERS_AND_BATTLE_SCRIPT,
            DEPLOY_JOHN_FLEET_SCRIPT,
            DEPLOY_JANE_FLEET_SCRIPT,
            FIGHT_UNTIL_JANE_VICTORY
        )
        initializePlayerAndBattleIds()

        val battle = console.findBattleById(battleId)!!
        assertThat(battle.status)
            .isSameAs(BattleStatus.FINISHED)
    }
}