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

import io.drevezadur.scylla.teacher.restserver.domain.model.ShipCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.ShipStore
import io.drevezadur.scylla.teacher.restserver.lang.*
import io.drevezadur.scylla.teacher.restserver.persist.BattleRepository
import io.drevezadur.scylla.teacher.restserver.persist.FleetRepository
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.persist.ShipRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.*
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
class ShipStoreBeanTest {

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var playerRepository: PlayerRepository

    @Autowired
    lateinit var battleRepository: BattleRepository

    @Autowired
    lateinit var fleetRepository: FleetRepository

    @Autowired
    lateinit var shipRepository: ShipRepository

    lateinit var shipStore: ShipStore

    @BeforeEach
    fun setUp() {
        createPlayers()
        createBattle()
        createFleet()
        shipStore = ShipStoreBean(shipRepository)
    }

    private fun createPlayers() {
        playerRepository.save(PlayerPEntity(playerId1, "John"))
        playerRepository.save(PlayerPEntity(playerId2, "Jane"))
        playerRepository.save(PlayerPEntity(playerId3, "Jack"))
        entityManager.flush()
    }

    private fun createBattle() {
        battleRepository.save(
            BattlePEntity(
                battleId,
                playerId1, playerId2,
                BattleStatus.RUNNING,
                playerId1
            )
        )
        entityManager.flush()
    }

    private fun createFleet() {
        fleetRepository.save(FleetPEntity(battleId, playerId1, FleetStatus.DAMAGED))
        entityManager.flush()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `create() shall not create a ship with unknown battleId`() {
        assertThatThrownBy {
            shipRepository.save(
                ShipPEntity(
                    unknownId, playerId1,
                    ShipType.CRUISER,
                    ShipStructuralStatus.UNHARMED,
                    origin = "01",
                    GridOrientation.ROW
                )
            )
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }

    @Test
    fun `create() shall not create a ship with unknown playerId`() {
        assertThatThrownBy {
            shipRepository.save(
                ShipPEntity(
                    battleId, unknownId,
                    ShipType.CRUISER,
                    ShipStructuralStatus.UNHARMED,
                    origin = "01",
                    GridOrientation.ROW
                )
            )
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }

    @Test
    fun `create() shall overwrite the previous ship`() {
        saveShips(
            ShipPEntity(
                battleId, playerId1,
                ShipType.CRUISER,
                ShipStructuralStatus.UNHARMED,
                origin = "01",
                GridOrientation.ROW
            )
        )

        shipStore.create(
            ShipCreation(
                battleId, playerId1,
                ShipType.CRUISER,
                Location(1, 2),
                GridOrientation.ROW
            )
        )
        entityManager.flush()

        assertThat(
            shipRepository.findById(ShipId(battleId, playerId1, ShipType.CRUISER)).get()
        ).extracting("origin")
            .isEqualTo("12")
    }


    private fun saveShips(vararg ships: ShipPEntity) {
        ships.forEach { shipRepository.save(it) }
        entityManager.flush()
    }

    @Test
    fun `create() shall save a new ship into repository`() {
        shipStore.create(
            ShipCreation(
                battleId, playerId1,
                ShipType.CRUISER,
                Location(1, 2),
                GridOrientation.ROW
            )
        )
        entityManager.flush()


        assertThat(
            shipRepository.findById(ShipId(battleId, playerId1, ShipType.CRUISER)).get()
        ).extracting(
            "battleId",
            "playerId",
            "type",
            "origin",
            "orientation",
            "status",
            "hits"
        ).containsExactly(
            battleId,
            playerId1,
            ShipType.CRUISER,
            "12",
            GridOrientation.ROW,
            ShipStructuralStatus.UNHARMED,
            ""
        )
    }


    @Test
    fun `save shall not accept unknown battle`() {
        val cruiser = ShipDEntity(
            unknownId, playerId1, ShipType.CRUISER,
            origin = Location(1, 3), orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.DAMAGED
        )

        assertThatThrownBy {
            shipStore.save(cruiser)
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }

    @Test
    fun `save shall not accept unknown player`() {
        val cruiser = ShipDEntity(
            battleId, unknownId, ShipType.CRUISER,
            origin = Location(1, 3), orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.DAMAGED
        )

        assertThatThrownBy {
            shipStore.save(cruiser)
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }

    @Test
    fun `save shall accept new ship`() {
        val cruiser = ShipDEntity(
            battleId, playerId1, ShipType.CRUISER,
            origin = Location(1, 3), orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.DAMAGED
        )

        shipStore.save(cruiser)
        entityManager.flush()

        assertThat(shipRepository.count()).isEqualTo(1)
    }


    @Test
    fun `save shall override existing ship`() {
        val cruiser = ShipDEntity(
            battleId, playerId1, ShipType.CRUISER,
            origin = Location(1, 3), orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.UNHARMED
        )
        shipStore.save(cruiser)
        entityManager.flush()


        val cruiser2 = ShipDEntity(
            battleId, playerId1, ShipType.CRUISER,
            origin = Location(1, 3), orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.DAMAGED, hits = setOf(Location(1, 3))
        )
        shipStore.save(cruiser2)
        entityManager.flush()

        assertThat(shipRepository.count()).isEqualTo(1)
    }


    @Test
    fun `findById shall return null when unknown battle id`() {
        saveShips(
            ShipPEntity(
                battleId, playerId1, ShipType.SUBMARINE,
                ShipStructuralStatus.DAMAGED,
                "23",
                GridOrientation.ROW,
                "33"
            )
        )

        assertThat(
            shipStore.findById(unknownId, playerId1, ShipType.SUBMARINE)
        ).isNull()
    }

    @Test
    fun `findById shall return null when unknown player id`() {
        saveShips(
            ShipPEntity(
                battleId, playerId1, ShipType.SUBMARINE,
                ShipStructuralStatus.DAMAGED,
                "23",
                GridOrientation.ROW,
                " x "
            )
        )

        assertThat(
            shipStore.findById(battleId, unknownId, ShipType.SUBMARINE)
        ).isNull()
    }

    @Test
    fun `findById shall return null when ship not deployed`() {
        saveShips(
            ShipPEntity(
                battleId, playerId1, ShipType.SUBMARINE,
                ShipStructuralStatus.DAMAGED,
                "23",
                GridOrientation.ROW,
                " x "
            )
        )

        assertThat(
            shipStore.findById(battleId, playerId1, ShipType.CRUISER)
        ).isNull()
    }


    @Test
    fun `findById shall return the deployed ship`() {
        saveShips(
            ShipPEntity(
                battleId, playerId1, ShipType.SUBMARINE,
                ShipStructuralStatus.DAMAGED,
                "23",
                GridOrientation.ROW,
                "33"
            )
        )

        assertThat(
            shipStore.findById(battleId, playerId1, ShipType.SUBMARINE)
        ).isNotNull()
            .extracting(
                "battleId", "playerId", "type",
                "origin", "orientation",
                "status", "hits"
            )
            .containsExactly(
                battleId, playerId1, ShipType.SUBMARINE,
                Location(2, 3), GridOrientation.ROW,
                ShipStructuralStatus.DAMAGED, setOf(Location(3, 3))
            )
    }


    @Test
    fun `getAllShipsInFleet() shall return empty list when battle not exist`() {
        assertThat(
            shipStore.getAllShipsInFleet(unknownId, playerId1)
        ).isEmpty()
    }

    @Test
    fun `getAllShipsInFleet() shall return empty list when player not exist`() {
        assertThat(
            shipStore.getAllShipsInFleet(battleId, unknownId)
        ).isEmpty()
    }

    @Test
    fun `getAllShipsInFleet() shall return empty list when player not in battle`() {
        assertThat(
            shipStore.getAllShipsInFleet(battleId, playerId3)
        ).isEmpty()
    }

    @Test
    fun `getAllShipsInFleet() shall return empty list when no ships in fleet`() {
        assertThat(
            shipStore.getAllShipsInFleet(battleId, playerId1)
        ).isEmpty()
    }

    @Test
    fun `getAllShipsInFleet() shall return filled list when some ships in fleet`() {
        val cruiser = ShipPEntity(
            battleId, playerId1, ShipType.CRUISER,
            origin = "13", orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.DAMAGED
        )
        val carrier = ShipPEntity(
            battleId, playerId1, ShipType.CARRIER,
            origin = "46", orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.UNHARMED,
            hits = "5666"
        )
        val submarine = ShipPEntity(
            battleId, playerId1, ShipType.SUBMARINE,
            origin = "01", orientation = GridOrientation.COLUMN,
            status = ShipStructuralStatus.DESTROYED,
            hits = "010203"
        )
        saveShips(cruiser, carrier, submarine)

        assertThat(
            shipStore.getAllShipsInFleet(battleId, playerId1)
        ).contains(
            ShipDEntity(
                battleId, playerId1, ShipType.CRUISER,
                origin = Location(1, 3), orientation = GridOrientation.ROW,
                status = ShipStructuralStatus.UNHARMED
            ),
            ShipDEntity(
                battleId, playerId1, ShipType.CARRIER,
                origin = Location(4, 6), orientation = GridOrientation.ROW,
                status = ShipStructuralStatus.UNHARMED,
                hits = LocationMapper.toLocations("5666").toSet()
            ),
            ShipDEntity(
                battleId, playerId1, ShipType.SUBMARINE,
                origin = Location(0, 1), orientation = GridOrientation.COLUMN,
                status = ShipStructuralStatus.DESTROYED,
                hits = LocationMapper.toLocations("010203").toSet()
            )
        )
    }


    @Test
    fun `getAll() shall return empty list when no ships`() {
        assertThat(shipStore.getAll()).isEmpty()
    }


    @Test
    fun getAll() {
        val cruiser = ShipPEntity(
            battleId, playerId1, ShipType.CRUISER,
            origin = "13", orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.DAMAGED
        )
        val carrier = ShipPEntity(
            battleId, playerId1, ShipType.CARRIER,
            origin = "46", orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.UNHARMED,
            hits = "5666"
        )
        val submarine = ShipPEntity(
            battleId, playerId1, ShipType.SUBMARINE,
            origin = "01", orientation = GridOrientation.COLUMN,
            status = ShipStructuralStatus.DESTROYED,
            hits = "010203"
        )
        saveShips(cruiser, carrier, submarine)

        assertThat(shipStore.getAll())
            .hasSize(3)
    }


    @Test
    fun `delete shall do nothing when ship not exists`() {
        val cruiser = ShipPEntity(
            battleId, playerId1, ShipType.CRUISER,
            origin = "13", orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.DAMAGED
        )
        val carrier = ShipPEntity(
            battleId, playerId1, ShipType.CARRIER,
            origin = "46", orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.UNHARMED,
            hits = "5666"
        )
        val submarine = ShipPEntity(
            battleId, playerId1, ShipType.SUBMARINE,
            origin = "01", orientation = GridOrientation.COLUMN,
            status = ShipStructuralStatus.DESTROYED,
            hits = "010203"
        )
        saveShips(cruiser, carrier, submarine)

        shipStore.delete(battleId, playerId1, ShipType.BATTLESHIP)
        entityManager.flush()

        assertThat(shipRepository.count()).isEqualTo(3)
    }


    @Test
    fun delete() {
        val cruiser = ShipPEntity(
            battleId, playerId1, ShipType.CRUISER,
            origin = "13", orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.DAMAGED
        )
        val carrier = ShipPEntity(
            battleId, playerId1, ShipType.CARRIER,
            origin = "46", orientation = GridOrientation.ROW,
            status = ShipStructuralStatus.UNHARMED,
            hits = "5666"
        )
        val submarine = ShipPEntity(
            battleId, playerId1, ShipType.SUBMARINE,
            origin = "01", orientation = GridOrientation.COLUMN,
            status = ShipStructuralStatus.DESTROYED,
            hits = "010203"
        )
        saveShips(cruiser, carrier, submarine)

        shipStore.delete(battleId, playerId1, ShipType.CARRIER)
        entityManager.flush()

        assertThat(shipRepository.count()).isEqualTo(2)
    }

    companion object {
        val unknownId: UUID = UUID.randomUUID()
        val battleId: UUID = UUID.randomUUID()
        val playerId1: UUID = UUID.randomUUID()
        val playerId2: UUID = UUID.randomUUID()
        val playerId3: UUID = UUID.randomUUID()
        val fleetId: UUID = UUID.randomUUID()
    }
}