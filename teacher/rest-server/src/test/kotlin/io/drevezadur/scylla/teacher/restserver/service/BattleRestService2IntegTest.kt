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
import io.drevezadur.scylla.teacher.restserver.persist.BattleRepository
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.BattlePEntity
import io.drevezadur.scylla.teacher.restserver.service.model.BattleCreationPojo
import io.drevezadur.scylla.teacher.restserver.testutils.IntegrationContext
import io.drevezadur.scylla.teacher.restserver.testutils.IntegrationContextFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
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
class BattleRestService2IntegTest {

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


    @BeforeEach
    fun setUp() {
        if (!this::integrationContext.isInitialized) {
            integrationContext = IntegrationContextFactory.createContext(applicationContext, port)
        }

        console = integrationContext.console

        createPlayers()
    }

    private fun createPlayers() {
        integrationContext.createPlayer(JOHN_ID, "John")
        integrationContext.createPlayer(JANE_ID, "Jane")
        integrationContext.createPlayer(JANICE_ID, "Janice")
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
    fun `getAll shall return the list of users`() {
        createBattles()

        assertThat(console.getAllBattles())
            .extracting("id", "player1Id", "player2Id", "shootingPlayer", "status", "winner")
            .contains(
                Tuple(UUID_1, JOHN_ID, JANE_ID, JOHN_ID, BattleStatus.RUNNING, null),
                Tuple(UUID_2, JANE_ID, JANICE_ID, JANE_ID, BattleStatus.DEPLOYMENT, null)
            )
    }

    private fun createBattles() {
        battleRepository.save(BattlePEntity(UUID_1, JOHN_ID, JANE_ID, BattleStatus.RUNNING, JOHN_ID, null))
        battleRepository.save(BattlePEntity(UUID_2, JANE_ID, JANICE_ID, BattleStatus.DEPLOYMENT, JANE_ID, null))
    }


    @Test
    fun `create shall create a new battle`() {
        val battleId = console.createBattle(
            BattleCreationPojo(
                JOHN_ID, JANICE_ID
            )
        )

        assertThat(console.findBattleById(battleId))
            .isNotNull
    }

    @Test
    fun `delete shall do nothing when battle does not exist`() {
        createBattles()

        console.deleteBattle(UNKNOWN_UUID)

        assertThat(console.getAllBattles())
            .hasSize(2)
    }

    @Test
    fun `delete shall delete an existing battle`() {
        createBattles()

        console.deleteBattle(UUID_2)

        assertThat(console.getAllBattles())
            .hasSize(1)
    }


    @Test
    fun `getById shall return not found when battle does not exist`() {
        assertThat(console.findBattleById(UNKNOWN_UUID))
            .isNull()
    }

    @Test
    fun `getById shall return the battle when it exists`() {
        createBattles()

        assertThat(console.findBattleById(UUID_2))
            .extracting("id", "player1Id", "player2Id")
            .containsExactly(UUID_2, JANE_ID, JANICE_ID)
    }


    companion object {
        val JOHN_ID: UUID = UUID.randomUUID()
        val JANE_ID: UUID = UUID.randomUUID()
        val JANICE_ID: UUID = UUID.randomUUID()

        val UUID_1: UUID = UUID.randomUUID()
        val UUID_2: UUID = UUID.randomUUID()
        val UNKNOWN_UUID: UUID = UUID.randomUUID()
    }
}