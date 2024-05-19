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

package io.drevezerezh.scylla.advanced.persistance

import io.drevezerezh.scylla.advanced.domain.api.ship.Ship
import io.drevezerezh.scylla.advanced.domain.spi.ShipStore
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation
import io.drevezerezh.scylla.advanced.lang.ShipType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MemoryShipStoreBeanTest {

    private lateinit var shipStore : ShipStore

    @BeforeEach
    fun setUp() {
        shipStore = MemoryShipStoreBean()
    }


    @Test
    fun getFleet() {
        shipStore.saveAll(SHIP_1, SHIP_2, SHIP_3)

        assertThat( shipStore.getFleet(SHIP_1.fleetId) )
            .containsOnly(SHIP_1, SHIP_2)
    }


    companion object {
        val SHIP_1 =
            Ship("battle-01", BattlePlayer.SECOND, ShipType.DESTROYER, GridLocation(0, 0), GridOrientation.ROW)
        val SHIP_2 =
            Ship("battle-01", BattlePlayer.SECOND, ShipType.SUBMARINE, GridLocation(1, 1), GridOrientation.COLUMN)
        val SHIP_3 =
            Ship("battle-02", BattlePlayer.SECOND, ShipType.CARRIER, GridLocation(0, 0), GridOrientation.ROW)
    }
}