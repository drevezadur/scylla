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
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.PlayerPEntity
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
class PlayerRestService2IntegTest {

    @Value(value = "\${local.server.port}")
    var port: Int = 0

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var playerRepository: PlayerRepository

    lateinit var integrationContext: IntegrationContext

    lateinit var console: CommandConsole

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
    fun `getAll shall return empty list when no users`() {
        assertThat(console.getAllPlayers())
            .isEmpty()
    }


    @Test
    fun `getAll shall return the list of users`() {
        createPlayers()

        assertThat(console.getAllPlayers())
            .extracting("uuid", "name")
            .contains(
                Tuple(uuid1, "John"),
                Tuple(uuid2, "Jane")
            )
    }

    private fun createPlayers() {
        playerRepository.save(PlayerPEntity(uuid1, "John"))
        playerRepository.save(PlayerPEntity(uuid2, "Jane"))
    }


    @Test
    fun `getById shall return not found when user does not exist`() {
        assertThat(console.findPlayerById(unknown))
            .isNull()
    }

    @Test
    fun `getById shall return the user when it exists`() {
        createPlayers()

        assertThat(console.findPlayerById(uuid1))
            .extracting("uuid", "name")
            .containsExactly(uuid1, "John")
    }

    @Test
    fun `create shall create a new user`() {
        console.createPlayer("Walter")
    }

    @Test
    fun `delete shall do nothing when user not exists`() {
        console.deletePlayer(unknown)
    }

    @Test
    fun `delete shall delete user when user exists`() {
        createPlayers()

        console.deletePlayer(uuid2)

        assertThat(playerRepository.count())
            .isEqualTo(1)
    }


    companion object {
        val uuid1: UUID = UUID.randomUUID()
        val uuid2: UUID = UUID.randomUUID()
        val unknown: UUID = UUID.randomUUID()
    }
}