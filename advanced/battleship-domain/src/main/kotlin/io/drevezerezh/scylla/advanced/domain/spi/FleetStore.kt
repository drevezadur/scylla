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

import io.drevezerezh.scylla.advanced.domain.api.fleet.Fleet
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetNotFoundException

/**
 * Store of fleets
 */
interface FleetStore : ItemStore<FleetId,Fleet> {

//    /**
//     * Store a fleet
//     *
//     * Overwrite it when it is already stored
//     * @param fleet the fleet to store
//     */
//    fun save(fleet: Fleet)
//
//    /**
//     * Delete a fleet from its identifier
//     *
//     * Nothing is done when fleet is not stored
//     *
//     * @param id the fleet identifier
//     * @return true when such a fleet was stored, false when fleet wasn't stored
//     */
//    fun deleteById(id: FleetId): Boolean

//    /**
//     * Delete all stored fleets
//     *
//     * Nothing is done when no fleet are stored
//     */
//    fun deleteAll()
//
//    /**
//     * Check if a fleet is stored or not
//     *
//     * @param id the fleet identifier
//     * @return true when such a fleet is stored, false otherwise
//     */
//    fun contains(id: FleetId): Boolean

    /**
     * Get a stored fleet from its identifier
     * @param id the fleet identifier
     * @return the stored fleet
     * @throws FleetNotFoundException when no such fleet is stored
     */
    @Throws(FleetNotFoundException::class)
    override fun getById(id: FleetId): Fleet
}