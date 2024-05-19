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
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipAlreadyDeployedException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipId
import io.drevezerezh.scylla.advanced.domain.spi.ShipStore
import io.drevezerezh.scylla.advanced.lang.ShipType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ShipManagerBean(
    private val shipStore: ShipStore
) : ShipManager {

    private val logger: Logger = LoggerFactory.getLogger(ShipManagerBean::class.java)


    override fun containsShip(id: ShipId): Boolean {
        return shipStore.contains(id)
    }


    override fun getFleetShips(fleetId: FleetId): Set<Ship> {
        return shipStore.getFleet(fleetId)
    }


    override fun create(ship: Ship) {
        if (logger.isTraceEnabled)
            logger.trace("create ship: battleId=${ship.battleId}, player=${ship.player}, type=${ship.type}, location=${ship.location}, orientation=${ship.orientation}")

        if (shipStore.contains(ship.id)) {
            if (logger.isTraceEnabled)
                logger.trace("ship already exists")
            throw ShipAlreadyDeployedException(ship.id)
        }
        shipStore.save(ship)
    }


    override fun update(ship: Ship) {
        if (logger.isTraceEnabled)
            logger.trace("update ship id=${ship.id}")

        if (!shipStore.contains(ship.id)) {
            if (logger.isTraceEnabled)
                logger.trace("ship not found")
            throw ShipNotFoundException(
                ship.id,
                "Cannot found ship into store. A ship shall already deployed before it can be updated."
            )
        }

        shipStore.save(ship)
    }

    override fun isFleetDeployed(fleetId: FleetId): Boolean {
        val firstFleetShips = getFleetShips(fleetId)
        return firstFleetShips.size == ShipType.entries.size
    }
}