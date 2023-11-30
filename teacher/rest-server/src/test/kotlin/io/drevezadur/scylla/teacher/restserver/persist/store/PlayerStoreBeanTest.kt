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

package io.drevezadur.scylla.teacher.restserver.persist.store

import io.drevezadur.scylla.teacher.restserver.common.UuidProvider
import io.drevezadur.scylla.teacher.restserver.domain.PlayerNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.store.PlayerStore
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.PlayerPEntity
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(MockKExtension::class)
@ExtendWith(SpringExtension::class)
@DataJpaTest
class PlayerStoreBeanTest {

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var playerRepository: PlayerRepository

    lateinit var playerStore: PlayerStore

    @MockK
    lateinit var uuidProvider: UuidProvider

    @BeforeEach
    fun setUp() {
        playerStore = PlayerStoreBean(playerRepository, uuidProvider)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `create() shall store a new user`() {
        every { uuidProvider.create() }.returns(playerId1)

        assertThat(playerStore.create("Jane"))
            .extracting("id", "name")
            .containsExactly(playerId1, "Jane")
    }


    @Test
    fun `findById shall return null when user not exists`() {
        assertThat(playerStore.findById(unknownId)).isNull()
    }

    @Test
    fun `findById shall return the expected player when it exists`() {
        createPlayer(playerId1, "John")
        createPlayer(playerId2, "Jane")

        assertThat(playerStore.findById(playerId2))
            .extracting("id", "name")
            .containsExactly(playerId2, "Jane")
    }

    private fun createPlayer(id: UUID, name: String) {
        playerRepository.save(PlayerPEntity(id, name))
        entityManager.flush()
    }


    @Test
    fun `getById shall fail when the player does not exist`() {
        assertThatThrownBy {
            playerStore.getById(playerId1)
        }.isInstanceOf(PlayerNotFoundException::class.java)
    }

    @Test
    fun `getById shall return the expected player when it exists`() {
        createPlayer(playerId2, "Jane")

        assertThat(playerStore.getById(playerId2))
            .extracting("id", "name")
            .containsExactly(playerId2, "Jane")
    }

    @Test
    fun `getAll() shall return empty list when no user`() {
        assertThat(playerStore.getAll()).isEmpty()
    }

    @Test
    fun getAll() {
        createPlayer(playerId1, "John")
        createPlayer(playerId2, "Jane")

        assertThat(playerStore.getAll())
            .hasSize(2)
            .extracting("id", "name")
            .containsOnly(
                Tuple(playerId1, "John"),
                Tuple(playerId2, "Jane")
            )
    }

    @Test
    fun `delete() shall do nothing when no user`() {
        createPlayer(playerId1, "John")
        createPlayer(playerId2, "Jane")

        playerStore.delete(unknownId)
    }

    @Test
    fun `delete() shall delete existing user`() {
        createPlayer(playerId1, "John")
        createPlayer(playerId2, "Jane")

        playerStore.delete(playerId1)
        entityManager.flush()

        assertThat(playerRepository.count()).isEqualTo(1)
    }

    companion object {
        val playerId1: UUID = UUID.randomUUID()
        val playerId2: UUID = UUID.randomUUID()
        val unknownId: UUID = UUID.randomUUID()
    }
}