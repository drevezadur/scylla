/*
 * Copyright (c) 2023. gofannon.io
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

import io.gofannon.scylla.homework.lang.GridOrientation
import io.gofannon.scylla.homework.lang.Location
import io.gofannon.scylla.homework.lang.ShipType

/**
 * A factory of [MutableShip]
 */
internal interface ShipFactory {

    /**
     * Create a new ship
     * @param type the type of the ship
     * @param origin the first location of the ship
     * @param orientation the orientation of the ship from the origin ([GridOrientation.ROW] from location to right, [GridOrientation.COLUMN] from location to bottom)
     * @return the newly created ship
     */
    fun createShip(type: ShipType, origin: Location, orientation: GridOrientation): MutableShip

}