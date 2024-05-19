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

package io.drevezerezh.scylla.advanced.domain.api.ship

import io.drevezerezh.scylla.advanced.lang.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ShipTest {

    @Test
    fun `constructor shall construct unharmed ship`() {
        val ship = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN
        )

        assertThat(ship.hits)
            .isEmpty()

        assertThat(ship.status)
            .isSameAs(ShipStatus.UNHARMED)
    }


    @Test
    fun `constructor shall construct damaged ship`() {
        val ship = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN,
            setOf(loc1, loc3)
        )

        assertThat(ship.hits)
            .containsOnly(loc1, loc3)
            .hasSize(2)

        assertThat(ship.status)
            .isSameAs(ShipStatus.DAMAGED)
    }


    @Test
    fun `constructor shall construct destroyed ship`() {
        val ship = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN,
            setOf(loc1, loc2, loc3)
        )

        assertThat(ship.hits)
            .containsOnly(loc1, loc2, loc3)
            .hasSize(3)

        assertThat(ship.status)
            .isSameAs(ShipStatus.SUNK)
    }


    @Test
    fun `hits shall return empty set when no hit`() {
        val ship = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN
        )

        assertThat(ship.hits)
            .isEmpty()
    }


    @Test
    fun `hits shall return filled set when some hits`() {
        val ship = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN,
            setOf(loc1)
        )

        assertThat(ship.hits)
            .containsOnly(loc1)
            .hasSize(1)
    }


    @Test
    fun `containsHit shall return false when location is not on an hit`() {
        val ship = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN,
            setOf(loc2)
        )

        assertThat(ship.containsHit(loc3))
            .isFalse()
    }


    @Test
    fun `containsHit shall return true when location is on an hit`() {
        val ship = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN,
            setOf(loc2)
        )

        assertThat(ship.containsHit(loc2))
            .isTrue()
    }


    @Test
    fun `equals shall return false when not same id`() {
        val ship1 = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN
        )
        val ship2 = Ship(
            "battle-02",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN
        )

        assertThat(ship1)
            .isNotEqualTo(ship2)
    }


    @Test
    fun `equals shall return false when not same player`() {
        val ship1 = Ship(
            "battle-01",
            BattlePlayer.FIRST,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN
        )
        val ship2 = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN
        )

        assertThat(ship1)
            .isNotEqualTo(ship2)
    }

    @Test
    fun `equals shall return false when not same type`() {
        val ship1 = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN
        )
        val ship2 = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.CARRIER,
            loc1,
            GridOrientation.COLUMN
        )

        assertThat(ship1)
            .isNotEqualTo(ship2)
    }


    @Test
    fun `equals shall return true when same id, player and type`() {
        val ship1 = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN
        )
        val ship2 = Ship(
            "battle-01",
            BattlePlayer.SECOND,
            ShipType.SUBMARINE,
            loc1,
            GridOrientation.COLUMN
        )

        assertThat(ship1)
            .isEqualTo(ship2)
    }


    companion object {
        val loc1 = GridLocation(2, 3)
        val loc2 = GridLocation(2, 4)
        val loc3 = GridLocation(2, 5)
    }
}