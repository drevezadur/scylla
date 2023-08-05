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
package io.gofannon.scylla.homework.domain

import io.gofannon.scylla.homework.lang.GridOrientation
import io.gofannon.scylla.homework.lang.Location
import io.gofannon.scylla.homework.lang.ShipStructuralStatus
import io.gofannon.scylla.homework.lang.ShipType

interface Ship {
    /**
     * Get the type of the ship
     */
    val type: ShipType

    /**
     * Get the origin location of the ship
     */
    val origin: Location

    /**
     * Get the orientation of the ship
     */
    val orientation: GridOrientation

    /**
     * Get the hits on the ship
     */
    val hits: Set<Location>

    /**
     * Get the structural status of the ship
     */
    val status: ShipStructuralStatus

    /**
     * Check if the ship is located on a specific location
     * @param location the location to check
     * @return true if the ship is on the location, false otherwise
     */
    fun contains(location: Location): Boolean

    /**
     * Check if another ship is intersecting the current ship
     * @param ship the another ship
     * @return true when the two ship have an intersection, false otherwise
     */
    fun intersects(ship: Ship): Boolean
}
