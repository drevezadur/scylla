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

import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.impl.LocationHelper
import io.drevezerezh.scylla.advanced.lang.*

data class Ship(
    val battleId: String,
    val player: BattlePlayer,
    val type: ShipType,
    val location: GridLocation,
    val orientation: GridOrientation,
    val hits: Set<GridLocation> = emptySet()
) {

    val id : ShipId = ShipId(battleId, player, type)


    val locations: List<GridLocation> = LocationHelper.computeSegment(location, orientation, type.size)

    val status : ShipStatus = computeStatus()


    val fleetId : FleetId = FleetId(battleId, player)


    fun containsHit(location: GridLocation): Boolean {
        return hits.contains(location)
    }


    fun isAt(location: GridLocation) : Boolean {
        return locations.contains(location)
    }

    private fun computeStatus() : ShipStatus{
        return when (hits.size) {
            0 -> ShipStatus.UNHARMED
            type.size -> ShipStatus.SUNK
            else -> ShipStatus.DAMAGED
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ship) return false

        if (battleId != other.battleId) return false
        if (player != other.player) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = battleId.hashCode()
        result = 31 * result + player.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    fun intersect(other: Ship): Boolean {
        return LocationHelper.isIntersection(
            locations,
            other.locations
        )
    }
}