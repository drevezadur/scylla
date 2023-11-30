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

import io.drevezadur.scylla.teacher.restserver.lang.GridOrientation
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.ShipStructuralStatus
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.persist.model.ShipPEntity

object ShipMapping {

    fun toLocations(origin: Location, orientation: GridOrientation, type: ShipType): List<Location> {
        return when (orientation) {
            GridOrientation.ROW -> buildRowShipLocation(origin, type)
            GridOrientation.COLUMN -> buildColumnShipLocations(origin, type)
        }
    }

    private fun buildRowShipLocation(origin: Location, type: ShipType): List<Location> {
        val maxX = origin.x + type.size - 1
        if (!Location.isValidX(maxX))
            throw IllegalArgumentException("The ship is too long (size=${type.size}) and part of the ship is out of the grid")

        val locations = ArrayList<Location>()
        locations.add(origin)
        for (x in origin.x + 1..maxX) {
            locations.add(Location(x, origin.y))
        }
        return locations
    }

    private fun buildColumnShipLocations(origin: Location, type: ShipType): List<Location> {
        val maxY = origin.y + type.size - 1
        if (!Location.isValidY(maxY))
            throw IllegalArgumentException("The ship is too long (size=${type.size}) and part of the ship is out of the grid")

        val locations = ArrayList<Location>()
        locations.add(origin)
        for (y in origin.y + 1..maxY) {
            locations.add(Location(origin.x, y))
        }
        return locations
    }


    /**
     * Create a persisted ship from a creation instruction
     * @param creation the creation information
     * @return the persisted ship
     */
    fun toPersistedShip(creation: ShipCreation): ShipPEntity {
        return ShipPEntity(
            battleId = creation.battleId,
            playerId = creation.playerId,
            type = creation.type,
            status = ShipStructuralStatus.UNHARMED,
            origin = creation.location,
            orientation = creation.orientation,
            hits = emptyList()
        )
    }
}
