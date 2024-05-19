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
import io.drevezerezh.scylla.advanced.lang.GridLocation

class ShipUpdate {
    private val hits: MutableSet<GridLocation> = mutableSetOf()

    fun addHit(location: GridLocation): ShipUpdate {
        this.hits.add(location)
        return this
    }


    fun isEmpty(): Boolean {
        return this.hits.isEmpty()
    }


    fun updateShip(ship: Ship): Ship {
        val newHits = computeHits(ship)
        return Ship(
            ship.battleId,
            ship.player,
            ship.type,
            ship.location,
            ship.orientation,
            newHits
        )
    }


    private fun computeHits(ship: Ship): Set<GridLocation> {
        val newHits = mutableSetOf<GridLocation>()
        newHits.addAll(ship.hits)
        newHits.addAll(hits)
        return newHits
    }

    override fun toString(): String {
        return "ShipUpdate(hits=$hits)"
    }
}