/*
 * Copyright (c) 2024 gofannon.xyz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.drevezerezh.scylla.advanced.domain.impl

import io.drevezerezh.scylla.advanced.domain.api.ship.Ship
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipAlreadyDeployedException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipNotFoundException
import io.drevezerezh.scylla.advanced.domain.data.BattleTestData.BATTLE_ID
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.A0
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.A1
import io.drevezerezh.scylla.advanced.domain.spi.ShipStore
import io.drevezerezh.scylla.advanced.domain.spi.ShipStoreStub
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation
import io.drevezerezh.scylla.advanced.lang.ShipType.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ShipManagerBeanTest {

    private lateinit var shipStore: ShipStore

    private lateinit var shipManager: ShipManager

    @BeforeEach
    fun setUp() {
        shipStore = ShipStoreStub()
        shipManager = ShipManagerBean(shipStore)
    }


    @Test
    fun `containsShip shall return false when not in store`() {
        assertThat(shipManager.containsShip(SHIP_1.id))
            .isFalse()
    }


    @Test
    fun `containsShip shall return true when in store`() {
        shipStore.save(SHIP_1)

        assertThat(shipManager.containsShip(SHIP_1.id))
            .isTrue()
    }


    @Test
    fun getFleetShips() {
        shipStore.saveAll(SHIP_1, SHIP_2)

        assertThat(shipManager.getFleetShips(SHIP_1.fleetId))
            .containsOnly(SHIP_1, SHIP_2)
    }


    @Test
    fun `create shall fail when when ship is already defined`() {
        shipStore.saveAll(SHIP_1)

        assertThatThrownBy {
            shipManager.create(SHIP_1)
        }.isInstanceOf(ShipAlreadyDeployedException::class.java)
    }


    @Test
    fun `create shall store the new ship`() {
        shipManager.create(SHIP_1)

        assertThat(shipStore.getById(SHIP_1.id))
            .isEqualTo(SHIP_1)
    }


    @Test
    fun `update shall fail when ship does not exist`() {
        assertThatThrownBy {
            shipManager.update(SHIP_1)
        }.isInstanceOf(ShipNotFoundException::class.java)

        assertThat(shipStore.getAll())
            .isEmpty()
    }


    @Test
    fun `update shall store the updated ship when it exists`() {
        val previousShip = SHIP_1.copy( location = A0)
        val nextShip =SHIP_1.copy( location = A1)

        shipStore.save(previousShip)

        shipManager.update(nextShip)

        assertThat(shipStore.getById(nextShip.id))
            .isEqualTo(nextShip)
    }


    @Test
    fun `isFleetDeployed shall return false when no ships in fleet`() {
        assertThat(shipManager.isFleetDeployed(SHIP_1.fleetId))
            .isFalse()
    }


    @Test
    fun `isFleetDeployed shall return false when no all ships in fleet`() {
        shipStore.saveAll(SHIP_1, SHIP_2, SHIP_3)

        assertThat(shipManager.isFleetDeployed(SHIP_1.fleetId))
            .isFalse()
    }


    @Test
    fun `isFleetDeployed shall return true when all ships are in fleet`() {
        shipStore.saveAll(SHIP_1, SHIP_2, SHIP_3, SHIP_4, SHIP_5)

        assertThat(shipManager.isFleetDeployed(SHIP_1.fleetId))
            .isTrue()
    }

    companion object {
        val SHIP_1 =
            Ship(BATTLE_ID, BattlePlayer.SECOND, DESTROYER, GridLocation(0, 0), GridOrientation.ROW)
        val SHIP_2 =
            Ship(BATTLE_ID, BattlePlayer.SECOND, SUBMARINE, GridLocation(1, 1), GridOrientation.COLUMN)
        val SHIP_3 =
            Ship(BATTLE_ID, BattlePlayer.SECOND, CARRIER, GridLocation(0, 0), GridOrientation.ROW)
        val SHIP_4 =
            Ship(BATTLE_ID, BattlePlayer.SECOND, CRUISER, GridLocation(1, 1), GridOrientation.COLUMN)
        val SHIP_5 =
            Ship(BATTLE_ID, BattlePlayer.SECOND, BATTLESHIP, GridLocation(1, 1), GridOrientation.COLUMN)
    }
}