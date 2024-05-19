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

package io.drevezerezh.scylla.advanced.domain.api.fleet

import io.drevezerezh.scylla.advanced.lang.FleetStatus
import io.drevezerezh.scylla.advanced.lang.GridLocation

class FleetUpdate {
    private var updatedStatus: FleetStatus? = null
    private var newShots: MutableSet<GridLocation> = HashSet()

    fun status(status: FleetStatus): FleetUpdate {
        this.updatedStatus = status
        return this
    }

    fun status(): FleetStatus? {
        return this.updatedStatus
    }


    fun addShot(shot: GridLocation): FleetUpdate {
        this.newShots.add(shot)
        return this
    }

    fun isEmpty(): Boolean {
        return this.updatedStatus == null && this.newShots.isEmpty()
    }

    fun updateFleet(fleet: Fleet): Fleet {
        val newStatus = computeNewStatus(fleet)
        val newShots = computeNewShots(fleet)
        return fleet.copy(
            status = newStatus,
            shots = newShots
        )
    }

    private fun computeNewStatus(fleet: Fleet): FleetStatus {
        return updatedStatus ?: fleet.status
    }

    private fun computeNewShots(fleet: Fleet): Set<GridLocation> {
        if (newShots.isEmpty())
            return fleet.shots

        val allShots = HashSet<GridLocation>(fleet.shots)
        allShots.addAll(newShots)
        return allShots
    }
}