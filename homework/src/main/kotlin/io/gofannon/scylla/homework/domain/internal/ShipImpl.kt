/*
 * Copyright (c) 2022-2023. gofannon.io
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
package io.gofannon.scylla.homework.domain.internal

import io.gofannon.scylla.homework.domain.Ship
import io.gofannon.scylla.homework.lang.*
import io.gofannon.scylla.homework.lang.GridOrientation.COLUMN
import io.gofannon.scylla.homework.lang.GridOrientation.ROW
import io.gofannon.scylla.homework.lang.ShipStructuralStatus.*
import io.gofannon.scylla.homework.lang.ShotResult.*

internal class ShipImpl(
    override val type: ShipType,
    override val origin: Location,
    override val orientation: GridOrientation,
    initialHits: Set<Location> = emptySet()
) : MutableShip {

    private val locations = ArrayList<Location>()
    private val mutableHits = HashSet<Location>()
    private lateinit var mutableStatus: ShipStructuralStatus

    override val hits: Set<Location>
        get() = mutableHits

    override val status: ShipStructuralStatus
        get() = mutableStatus


    init {
        buildShipLocation(origin)
        buildHitsOnShip(initialHits)
        updateStatus()
    }

    private fun buildShipLocation(origin: Location) {
        when (orientation) {
            ROW -> buildRowShipLocation(origin)
            COLUMN -> buildColumnShipLocations(origin)
        }
    }

    private fun buildRowShipLocation(origin: Location) {
        val maxX = origin.x + type.size - 1
        if (!Location.isValidX(maxX))
            throw IllegalArgumentException("The ship is too long (size=${type.size}) and part of the ship is out of the grid")

        locations.add(origin)
        for (x in origin.x + 1..maxX) {
            locations.add(Location(x, origin.y))
        }
    }

    private fun buildColumnShipLocations(origin: Location) {
        val maxY = origin.y + type.size - 1
        if (!Location.isValidY(maxY))
            throw IllegalArgumentException("The ship is too long (size=${type.size}) and part of the ship is out of the grid")

        locations.add(origin)
        for (y in origin.y + 1..maxY) {
            locations.add(Location(origin.x, y))
        }
    }


    private fun buildHitsOnShip(initialHits: Set<Location>) {
        for (hit in mutableHits) {
            if (!locations.contains(hit))
                throw IllegalArgumentException("Some hits are not on the ship (ex: $hit)")
        }

        mutableHits.addAll(initialHits)
    }


    private fun updateStatus() {
        mutableStatus = when (hits.size) {
            0 -> UNHARMED
            type.size -> DESTROYED
            else -> DAMAGED
        }
    }

    override fun contains(location: Location): Boolean {
        return locations.contains(location)
    }

    override fun hitAt(location: Location): ShotResult {
        if (!locations.contains(location))
            return MISSED

        if (hits.contains(location))
            return ALREADY_SHOT

        mutableHits.add(location)
        updateStatus()

        return if (status == DESTROYED) SUNK else HIT
    }

    override fun intersects(ship: Ship): Boolean {
        return locations.any(ship::contains)
    }
}