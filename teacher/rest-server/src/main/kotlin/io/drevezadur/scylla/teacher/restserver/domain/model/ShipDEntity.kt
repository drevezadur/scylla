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

package io.drevezadur.scylla.teacher.restserver.domain.model

import io.drevezadur.scylla.teacher.restserver.lang.*

class ShipDEntity(

    val battleId: BattleUUID,
    val playerId: PlayerUUID,
    val type: ShipType,
    val origin: Location,
    val orientation: GridOrientation,

    status: ShipStructuralStatus = ShipStructuralStatus.UNHARMED,
    hits: Set<Location> = emptySet()
) {
    private var mutableStatus: ShipStructuralStatus = status
    private var mutableHits = hits.toMutableList()

    private val shipLocations: List<Location> = computeShipLocation()

    private fun computeShipLocation(): List<Location> {
        val x = origin.x
        val y = origin.y

        return if (orientation == GridOrientation.ROW) {
            val maxX = x + type.size
            (x..<maxX).map { Location(it, y) }
        } else {
            val maxY = x + type.size
            (y..<maxY).map { Location(x, it) }
        }
    }

    val hits: Set<Location>
        get() = mutableHits.toSet()

    val status: ShipStructuralStatus
        get() = mutableStatus

    fun contains(location: Location): Boolean {
        return shipLocations.contains(location)
    }

    fun addHit(hit: Location) {
        if (!mutableHits.contains(hit) && shipLocations.contains(hit)) {
            mutableHits.add(hit)
            computeStatus()
        }
    }

    private fun computeStatus() {
        mutableStatus = when (mutableHits.size) {
            0 -> ShipStructuralStatus.UNHARMED
            shipLocations.size -> ShipStructuralStatus.DESTROYED
            else -> ShipStructuralStatus.DAMAGED
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShipDEntity) return false
        if (battleId != other.battleId) return false
        if (playerId != other.playerId) return false
        if (type != other.type) return false
        return true
    }

    override fun hashCode(): Int {
        var result = battleId.hashCode()
        result = 31 * result + playerId.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}