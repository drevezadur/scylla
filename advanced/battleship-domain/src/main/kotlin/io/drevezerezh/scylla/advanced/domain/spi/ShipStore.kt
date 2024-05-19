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

package io.drevezerezh.scylla.advanced.domain.spi

import io.drevezerezh.scylla.advanced.domain.api.ship.ShipNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.ship.Ship
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipId

/**
 * Storage of ship
 */
interface ShipStore : ItemStore<ShipId,Ship> {

//    /**
//     * Store a ship, overwrite it if it already exists
//     * @param ship the ship to store
//     */
//    fun save(ship: Ship)
//
//    /**
//     * Delete a ship from its identifier
//     *
//     * Nothing is done when the ship does not exist
//     * @param id the ship identifier
//     * @return true when the ship was existing, false otherwise
//     */
//    fun deleteById(id: ShipId): Boolean
//
//    /**
//     * Delete all ships in the store
//     *
//     * Nothing is done when the store is already empty
//     */
//    fun deleteAll()

    /**
     * Get a ship from its identifier
     * @param id the identifier of the ship to provide
     * @return the ship matching its identifier
     * @throws ShipNotFoundException when the ship cannot be found
     */
    @Throws(ShipNotFoundException::class)
    override fun getById(id: ShipId): Ship

//    /**
//     * Check if a ship is stored
//     * @param id the identifier of the ship
//     * @return true when the ship is stored, false otherwise
//     */
//    fun contains(id: ShipId): Boolean

    fun getFleet(fleetId: FleetId): Set<Ship>
}