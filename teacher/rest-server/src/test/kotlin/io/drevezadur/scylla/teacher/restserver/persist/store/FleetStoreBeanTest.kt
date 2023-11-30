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

import io.drevezadur.scylla.teacher.restserver.domain.FleetNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetDEntity
import io.drevezadur.scylla.teacher.restserver.lang.BattleStatus
import io.drevezadur.scylla.teacher.restserver.lang.FleetStatus
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.persist.BattleRepository
import io.drevezadur.scylla.teacher.restserver.persist.FleetRepository
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.*
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@DataJpaTest
class FleetStoreBeanTest {

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var playerRepository: PlayerRepository

    @Autowired
    lateinit var battleRepository: BattleRepository

    @Autowired
    lateinit var fleetRepository: FleetRepository

    lateinit var fleetStore: FleetStoreBean


    @BeforeEach
    fun setUp() {
        createPlayers()
        createBattles()
        fleetStore = FleetStoreBean(fleetRepository)
    }

    private fun createPlayers() {
        playerRepository.save(PlayerPEntity(player1Id, "John"))
        playerRepository.save(PlayerPEntity(player2Id, "Jane"))
        playerRepository.save(PlayerPEntity(player3Id, "Sacha"))
//        userRepository.save(UserPEntity(BattleStoreBeanTest.player2Id2, "Sarah"))
        entityManager.flush()
    }

    private fun createBattles() {
        battleRepository.save(
            BattlePEntity(
                battleId,
                player1Id,
                player2Id,
                BattleStatus.RUNNING,
                player1Id,
                null
            )
        )
        entityManager.flush()
    }

    @AfterEach
    fun tearDown() {
    }


    @Test
    fun `create() shall not create a fleet with unknown battle`() {
        val creation = FleetCreation(unknownId, player1Id)

        assertThatThrownBy {
            fleetStore.create(creation)
            entityManager.flush()
        }
            .isInstanceOf(ConstraintViolationException::class.java)
    }


    @Test
    fun `create() shall not create a fleet with unknown player id`() {
        val creation = FleetCreation(battleId, unknownId)

        assertThatThrownBy {
            fleetStore.create(creation)
            entityManager.flush()
        }
            .isInstanceOf(ConstraintViolationException::class.java)
    }


    @Test
    fun `create() shall create a fleet and persist it`() {
        val creation = FleetCreation(battleId, player1Id)

        assertThat(fleetStore.create(creation))
            .extracting("battleId", "playerId", "status")
            .containsExactly(battleId, player1Id, FleetStatus.NOT_DEPLOYED)

        assertThat(fleetRepository.findById(FleetId(battleId, player1Id)).get())
            .extracting("battleId", "playerId", "status", "shotStorage")
            .containsExactly(battleId, player1Id, FleetStatus.NOT_DEPLOYED, "")
    }

    @Test
    fun `findFleetByBattleAndPlayer shall return null when fleet is missing`() {
        assertThat(fleetStore.findFleetByBattleAndPlayer(battleId, player1Id))
            .isNull()
    }


    @Test
    fun `findFleetByBattleAndPlayer shall return fleet when fleet exists`() {
        val fleetPEntity = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.DAMAGED,
            shotStorage = "149035"
        )
        fleetRepository.save(fleetPEntity)
        entityManager.flush()


        assertThat(fleetStore.findFleetByBattleAndPlayer(battleId, player1Id))
            .extracting("battleId", "playerId", "status", "shots")
            .containsExactly(
                battleId,
                player1Id,
                FleetStatus.DAMAGED,
                listOf(Location(1, 4), Location(9, 0), Location(3, 5))
            )
    }


    @Test
    fun `getFleetByBattleAndPlayer shall throw exception when fleet is missing`() {
        assertThatThrownBy { fleetStore.getFleetByBattleAndPlayer(battleId, player1Id) }
            .isInstanceOf(FleetNotFoundException::class.java)
    }

    @Test
    fun `getFleetByBattleAndPlayer shall return fleet when fleet is present`() {
        val fleetPEntity = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.DAMAGED,
            shotStorage = "149035"
        )
        fleetRepository.save(fleetPEntity)
        entityManager.flush()


        assertThat(fleetStore.getFleetByBattleAndPlayer(battleId, player1Id))
            .extracting("battleId", "playerId", "status", "shots")
            .containsExactly(
                battleId,
                player1Id,
                FleetStatus.DAMAGED,
                listOf(Location(1, 4), Location(9, 0), Location(3, 5))
            )
    }

    @Test
    fun `getAllFleets(Battle) shall return empty list when battle not exists`() {
        assertThat(fleetStore.getAllFleets(unknownId)).isEmpty()
    }

    @Test
    fun `getAllFleetsOfBattle() shall return two fleets when battle exists`() {
        val fleetPEntity1 = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.DAMAGED,
            shotStorage = "149035"
        )
        val fleetPEntity2 = FleetPEntity(
            battleId,
            player2Id,
            FleetStatus.UNHARMED,
            shotStorage = "1723"
        )
        saveFleets(fleetPEntity1, fleetPEntity2)

        assertThat(fleetStore.getAllFleets(battleId))
            .extracting("battleId", "playerId", "status", "shots")
            .containsOnly(
                Tuple(battleId, player1Id, FleetStatus.DAMAGED, LocationMapper.toLocations("149035").toMutableList()),
                Tuple(battleId, player2Id, FleetStatus.UNHARMED, LocationMapper.toLocations("1723").toMutableList())
            )
    }

    private fun saveFleets(vararg fleets: FleetPEntity) {
        fleets.forEach { fleetRepository.save(it) }
        entityManager.flush()
    }

    @Test
    fun `getAll() shall return empty list when no battle`() {
        assertThat(fleetStore.getAll())
            .isEmpty()
    }

    @Test
    fun `getAll() shall return filled list when some battles are declared`() {
        val fleetPEntity1 = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.DAMAGED,
            shotStorage = "149035"
        )
        val fleetPEntity2 = FleetPEntity(
            battleId,
            player2Id,
            FleetStatus.UNHARMED,
            shotStorage = "1723"
        )
        saveFleets(fleetPEntity1, fleetPEntity2)


        assertThat(fleetStore.getAll())
            .extracting("battleId", "playerId", "status", "shots")
            .containsOnly(
                Tuple(battleId, player1Id, FleetStatus.DAMAGED, LocationMapper.toLocations("149035").toMutableList()),
                Tuple(battleId, player2Id, FleetStatus.UNHARMED, LocationMapper.toLocations("1723").toMutableList())
            )
    }

    @Test
    fun `isAllFleetsOfBattleHaveStatus() return false when none fleets have the expected status`() {
        val fleetPEntity1 = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.DAMAGED,
            shotStorage = "149035"
        )
        val fleetPEntity2 = FleetPEntity(
            battleId,
            player2Id,
            FleetStatus.UNHARMED,
            shotStorage = "1723"
        )
        saveFleets(fleetPEntity1, fleetPEntity2)

        assertThat(fleetStore.isAllFleetsOfBattleHaveStatus(battleId, FleetStatus.NOT_DEPLOYED))
            .isFalse()
    }

    @Test
    fun `isAllFleetsOfBattleHaveStatus() return false when only one fleet has the expected status`() {
        val fleetPEntity1 = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.DAMAGED,
            shotStorage = "149035"
        )
        val fleetPEntity2 = FleetPEntity(
            battleId,
            player2Id,
            FleetStatus.UNHARMED,
            shotStorage = "1723"
        )
        saveFleets(fleetPEntity1, fleetPEntity2)

        assertThat(fleetStore.isAllFleetsOfBattleHaveStatus(battleId, FleetStatus.UNHARMED))
            .isFalse()
    }

    @Test
    fun `isAllFleetsOfBattleHaveStatus() return true when all fleets have the expected status`() {
        val fleetPEntity1 = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.UNHARMED,
            shotStorage = "149035"
        )
        val fleetPEntity2 = FleetPEntity(
            battleId,
            player2Id,
            FleetStatus.UNHARMED,
            shotStorage = "1723"
        )
        saveFleets(fleetPEntity1, fleetPEntity2)

        assertThat(fleetStore.isAllFleetsOfBattleHaveStatus(battleId, FleetStatus.UNHARMED))
            .isTrue()
    }

    @Test
    fun `getAllFleets() shall return all fleets`() {
        val fleetPEntity1 = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.DAMAGED,
            shotStorage = "149035"
        )
        val fleetPEntity2 = FleetPEntity(
            battleId,
            player2Id,
            FleetStatus.UNHARMED,
            shotStorage = "1723"
        )
        saveFleets(fleetPEntity1, fleetPEntity2)

        assertThat(fleetStore.getAllFleets(battleId))
            .extracting("battleId", "playerId", "status", "shots")
            .containsOnly(
                Tuple(battleId, player1Id, FleetStatus.DAMAGED, LocationMapper.toLocations("149035").toMutableList()),
                Tuple(battleId, player2Id, FleetStatus.UNHARMED, LocationMapper.toLocations("1723").toMutableList())
            )
    }

    @Test
    fun `getOpponentFleetId shall fail when repository returns null`() {
        assertThatThrownBy { fleetStore.getOpponentFleetId(battleId, player1Id) }
            .isInstanceOf(FleetNotFoundException::class.java)
    }


    @Test
    fun `getOpponentFleetId() shall success when player in battle`() {
        val fleetPEntity1 = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.DAMAGED,
            shotStorage = "149035"
        )
        val fleetPEntity2 = FleetPEntity(
            battleId,
            player2Id,
            FleetStatus.UNHARMED,
            shotStorage = "1723"
        )
        saveFleets(fleetPEntity1, fleetPEntity2)


        assertThat(fleetStore.getOpponentFleetId(battleId, player1Id))
            .isEqualTo(player2Id)
    }

    @Test
    fun `getOpponentFleet() shall fail when battle not exists`() {
        assertThatThrownBy { fleetStore.getOpponentFleetId(unknownId, player1Id) }
            .isInstanceOf(FleetNotFoundException::class.java)
    }

    @Test
    fun `getOpponentFleet() shall fail when player not exist`() {
        assertThatThrownBy { fleetStore.getOpponentFleetId(battleId, unknownId) }
            .isInstanceOf(FleetNotFoundException::class.java)
    }

    @Test
    fun `getOpponentFleet() shall fail when player not in battle`() {
        assertThatThrownBy { fleetStore.getOpponentFleetId(battleId, player3Id) }
            .isInstanceOf(FleetNotFoundException::class.java)
    }

    @Test
    fun `getOpponentFleet() shall success when player in battle`() {
        val fleetPEntity1 = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.DAMAGED,
            shotStorage = "149035"
        )
        val fleetPEntity2 = FleetPEntity(
            battleId,
            player2Id,
            FleetStatus.UNHARMED,
            shotStorage = "1723"
        )
        saveFleets(fleetPEntity1, fleetPEntity2)


        assertThat(fleetStore.getOpponentFleet(battleId, player1Id))
            .extracting("battleId", "playerId")
            .containsExactly(battleId, player2Id)
    }

    @Test
    fun `delete() shall do nothing when fleet does not exist`() {
        fleetStore.delete(battleId, player1Id)
    }

    @Test
    fun `delete() shall call the repository deletion`() {
        val fleetPEntity1 = FleetPEntity(
            battleId,
            player1Id,
            FleetStatus.DAMAGED,
            shotStorage = "149035"
        )
        saveFleets(fleetPEntity1)

        fleetStore.delete(battleId, player1Id)
        entityManager.flush()

        assertThat(fleetRepository.count())
            .isEqualTo(0)
    }

    @Test
    fun `save() shall fail when battle does not exist`() {
        val fleet = FleetDEntity(
            unknownId,
            player1Id,
            FleetStatus.DAMAGED,
            mutableListOf(Location(3, 2))
        )

        assertThatThrownBy {
            fleetStore.save(fleet)
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }

    @Test
    fun `save() shall fail when player does not exist`() {
        val fleet = FleetDEntity(
            battleId,
            unknownId,
//            player1Id,
            FleetStatus.DAMAGED,
            mutableListOf(Location(3, 2))
        )

        assertThatThrownBy {
            fleetStore.save(fleet)
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }


    @Test
    fun `save() shall call the repository save`() {
        fleetStore.save(
            FleetDEntity(
                battleId,
                player1Id,
                FleetStatus.DAMAGED,
                LocationMapper.toLocations("3456").toMutableList()
            )
        )
        entityManager.flush()

        assertThat(fleetRepository.findById(FleetId(battleId, player1Id)).get())
            .extracting("battleId", "playerId", "status", "shotStorage")
            .containsExactly(battleId, player1Id, FleetStatus.DAMAGED, "3456")
    }

    companion object {
        val battleId: UUID = UUID.randomUUID()
        val player1Id: UUID = UUID.randomUUID()
        val player2Id: UUID = UUID.randomUUID()
        val player3Id: UUID = UUID.randomUUID()
        val unknownId: UUID = UUID.randomUUID()
    }
}