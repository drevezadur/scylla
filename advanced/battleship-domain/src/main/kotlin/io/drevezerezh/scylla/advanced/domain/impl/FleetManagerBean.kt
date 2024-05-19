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

import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.fleet.Fleet
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetAlreadyDeployedException
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.spi.FleetStore
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FleetManagerBean(
    private val fleetStore: FleetStore
) : FleetManager {

    private val  logger = LoggerFactory.getLogger(FleetManagerBean::class.java)

    override fun getFleet(id: FleetId): Fleet {
        return fleetStore.getById(id)
    }

    override fun createFleet(id: FleetId): Fleet {
        if( fleetStore.contains(id)) {
            if( logger.isTraceEnabled)
                logger.trace("Cannot create fleet $id because such a fleet is already exist")
            throw FleetAlreadyDeployedException(id)
        }

        val fleet = Fleet(id.battleId, id.player)
        fleetStore.save(fleet)
        return fleet
    }

    override fun updateFleet(fleet: Fleet) {
        if( !fleetStore.contains(fleet.id))
            throw FleetNotFoundException(fleet.id,"Cannot update fleet when it does not exist previously")

        fleetStore.save(fleet)
    }

    override fun deleteFleet(id: FleetId) : Boolean{
        return fleetStore.deleteById(id)
    }
}