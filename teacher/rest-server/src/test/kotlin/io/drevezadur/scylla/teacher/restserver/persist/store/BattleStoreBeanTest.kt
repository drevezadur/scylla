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
import io.drevezadur.scylla.teacher.restserver.domain.BattleNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleDEntity
import io.drevezadur.scylla.teacher.restserver.lang.BattleStatus
import io.drevezadur.scylla.teacher.restserver.persist.BattleRepository
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.BattlePEntity
import io.drevezadur.scylla.teacher.restserver.persist.model.PlayerPEntity
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.hibernate.exception.ConstraintViolationException
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
class BattleStoreBeanTest {

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var battleRepository: BattleRepository

    @Autowired
    lateinit var playerRepository: PlayerRepository

    @MockK
    lateinit var uuidProvider: UuidProvider

    lateinit var battleStore: BattleStoreBean

    @BeforeEach
    fun setUp() {
        createPlayers()
        deleteAllBattles()
        createBattleStore()
    }

    private fun createPlayers() {
        playerRepository.save(PlayerPEntity(player1Id, "John"))
        playerRepository.save(PlayerPEntity(player2Id, "Jane"))
        playerRepository.save(PlayerPEntity(player1Id2, "Sacha"))
        playerRepository.save(PlayerPEntity(player2Id2, "Sarah"))
        entityManager.flush()
    }

    private fun deleteAllBattles() {
        battleRepository.deleteAll()
        entityManager.flush()
    }

    private fun createBattleStore() {
        battleStore = BattleStoreBean(battleRepository, uuidProvider)
    }

    @Test
    fun `create() shall not create the battle when first player does not exist`() {
        val creation = BattleCreation(unknownId, player2Id)

        every { uuidProvider.create() }.returns(battleId)


        assertThatThrownBy {
            battleStore.create(creation)
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)

        verify(exactly = 1) { uuidProvider.create() }
    }


    @Test
    fun `create() shall not create the battle when second player does not exist`() {
        val creation = BattleCreation(player1Id, unknownId)

        every { uuidProvider.create() }.returns(battleId)

        assertThatThrownBy {
            battleStore.create(creation)
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)

        verify(exactly = 1) { uuidProvider.create() }
    }


    @Test
    fun `create() shall create a battle and persist it`() {
        val creation = BattleCreation(player1Id, player2Id)

        every { uuidProvider.create() }.returns(battleId)


        assertThat(battleStore.create(creation))
            .extracting("id", "player1Id", "player2Id", "status", "shooterPlayerId", "winnerId")
            .containsExactly(
                battleId,
                player1Id,
                player2Id,
                BattleStatus.DEPLOYMENT,
                player1Id,
                null
            )
        entityManager.flush()

        assertThat(battleRepository.findById(battleId).get())
            .extracting("id", "player1Id", "player2Id", "status", "shooterPlayer", "winner")
            .containsExactly(battleId, player1Id, player2Id, BattleStatus.DEPLOYMENT, player1Id, null)

        verify(exactly = 1) { uuidProvider.create() }
    }


    @Test
    fun `findById shall return null when battle does not exist`() {
        assertThat(battleStore.findById(battleId))
            .isNull()
    }

    @Test
    fun `findById shall return the battle when battle exists`() {
        val battlePEntity = BattlePEntity(
            battleId,
            player1Id,
            player2Id,
            BattleStatus.RUNNING,
            player2Id,
            null
        )
        battleRepository.save(battlePEntity);
        entityManager.flush()


        assertThat(battleStore.findById(battleId))
            .extracting("id", "player1Id", "player2Id", "status", "shooterPlayerId", "winnerId")
            .containsExactly(battleId, player1Id, player2Id, BattleStatus.RUNNING, player2Id, null)
    }


    @Test
    fun `getById shall throw an exception when battle does not exist`() {
        assertThatThrownBy {
            battleStore.getById(battleId)
        }.isInstanceOf(BattleNotFoundException::class.java)
    }


    @Test
    fun `getById shall return the matching battle`() {
        val battlePEntity = BattlePEntity(
            battleId,
            player1Id,
            player2Id,
            BattleStatus.RUNNING,
            player2Id,
            null
        )
        saveBattles(battlePEntity)


        assertThat(battleStore.getById(battleId))
            .extracting("id", "player1Id", "player2Id", "status", "shooterPlayerId", "winnerId")
            .containsExactly(battleId, player1Id, player2Id, BattleStatus.RUNNING, player2Id, null)
    }

    private fun saveBattles(vararg battles: BattlePEntity) {
        battles.forEach {
            battleRepository.save(it)
        }
        entityManager.flush()
    }


    @Test
    fun `save() shall fail when player1 does not exist`() {
        val battleDEntity = BattleDEntity(
            battleId,
            unknownId,
            player2Id,
            BattleStatus.RUNNING,
            player2Id,
            null
        )

        assertThatThrownBy {
            battleStore.save(battleDEntity)
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }

    @Test
    fun `save() shall fail when player2 does not exist`() {
        val battleDEntity = BattleDEntity(
            battleId,
            player1Id,
            unknownId,
            BattleStatus.RUNNING,
            player1Id,
            null
        )

        assertThatThrownBy {
            battleStore.save(battleDEntity)
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }


    @Test
    fun `save() shall fail when shooterPlayer does not exist`() {
        val battleDEntity = BattleDEntity(
            battleId,
            player1Id,
            player2Id,
            BattleStatus.RUNNING,
            unknownId,
            null
        )

        assertThatThrownBy {
            battleStore.save(battleDEntity)
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }


    @Test
    fun `save() shall fail when winner does not exist`() {
        val battleDEntity = BattleDEntity(
            battleId,
            player1Id,
            player2Id,
            BattleStatus.RUNNING,
            player2Id,
            unknownId
        )

        assertThatThrownBy {
            battleStore.save(battleDEntity)
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }


    @Test
    fun `save() shall save when player in battle`() {
        val battle = BattleDEntity(
            battleId,
            player1Id,
            player2Id,
            BattleStatus.RUNNING,
            player2Id,
            null
        )

        every { uuidProvider.create() }.returns(battleId)

        battleStore.save(battle)
        entityManager.flush()


        assertThat(battleRepository.findById(battleId).get())
            .extracting("id", "player1Id", "player2Id", "status", "shooterPlayer", "winner")
            .containsExactly(battleId, player1Id, player2Id, BattleStatus.RUNNING, player2Id, null)
    }


    @Test
    fun `getAll() shall return empty result when no battles`() {
        assertThat(battleStore.getAll())
            .isEmpty()
    }


    @Test
    fun `getAll() shall return all battles`() {
        val battle1 = BattlePEntity(
            battleId,
            player1Id,
            player2Id,
            BattleStatus.RUNNING,
            player2Id,
            null
        )
        val battle2 = BattlePEntity(
            battleId2,
            player1Id2,
            player2Id2,
            BattleStatus.FINISHED,
            player2Id2,
            player2Id2
        )

        saveBattles(battle1, battle2)


        assertThat(battleStore.getAll())
            .extracting("id", "player1Id", "player2Id", "status", "shooterPlayerId", "winnerId")
            .containsOnly(
                Tuple(battleId, player1Id, player2Id, BattleStatus.RUNNING, player2Id, null),
                Tuple(battleId2, player1Id2, player2Id2, BattleStatus.FINISHED, player2Id2, player2Id2)
            )
    }

    @Test
    fun `delete shall do nothing when battle does not exist`() {
        battleStore.delete(battleId)
    }

    @Test
    fun `delete shall remove existing battle`() {
        val battle = BattlePEntity(
            battleId,
            player1Id,
            player2Id,
            BattleStatus.RUNNING,
            player2Id,
            null
        )
        saveBattles(battle)

        battleStore.delete(battleId)

        assertThat(battleRepository.count()).isEqualTo(0)
    }

    companion object {
        val battleId: UUID = UUID.randomUUID()
        val player1Id: UUID = UUID.randomUUID()
        val player2Id: UUID = UUID.randomUUID()

        val battleId2: UUID = UUID.randomUUID()
        val player1Id2: UUID = UUID.randomUUID()
        val player2Id2: UUID = UUID.randomUUID()

        val unknownId: UUID = UUID.randomUUID()
    }
}