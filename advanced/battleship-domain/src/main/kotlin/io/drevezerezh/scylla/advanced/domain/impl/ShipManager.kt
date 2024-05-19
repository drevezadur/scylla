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

import io.drevezerezh.scylla.advanced.domain.api.ship.ShipNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.ship.Ship
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipId

/**
 * Manager of ship information
 */
interface ShipManager {

    /**
     * Check if a ship exists
     * @param id the identifier of the ship
     * @return true when the ship is deployed, false otherwise
     */
    fun containsShip(id: ShipId): Boolean

    /**
     * Get all ships belonging to the same fleet
     *
     * Warning: nothing is done when battle identifier does not match an existing battle
     *
     * @param fleetId the fleet identifier
     * @return all ships in the fleet
     */
    fun getFleetShips(fleetId: FleetId): Set<Ship>

    /**
     * Check if all ships of a fleet are deployed
     * @param fleetId the fleet identifier
     * @return true if the fleet is deployed, false otherwise
     */
    fun isFleetDeployed(fleetId: FleetId): Boolean


    /**
     * Deploy a ship
     * @param ship the ship to deploy
     */
    fun create(ship: Ship)

    /**
     * Update an existing ship and store it
     * @param ship the ship to update
     * @throws ShipNotFoundException when the ship
     */
    @Throws(ShipNotFoundException::class)
    fun update(ship : Ship)
}