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

package io.drevezadur.scylla.teacher.restserver.domain.internal.usecase

import io.drevezadur.scylla.teacher.restserver.domain.ShipCollisionException
import io.drevezadur.scylla.teacher.restserver.domain.ShipNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.ShipStore
import io.drevezadur.scylla.teacher.restserver.domain.usecase.ShipDeploymentUseCase
import io.drevezadur.scylla.teacher.restserver.domain.usecase.ShipUseCaseManager
import io.drevezadur.scylla.teacher.restserver.lang.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShipUseCaseManagerImpl(
    @Autowired
    private val shipStore: ShipStore,
    @Autowired
    private val shipDeploymentUseCase: ShipDeploymentUseCase
) : ShipUseCaseManager {

    override fun deploy(shipCreation: ShipCreation): ShipDEntity {
        checkNoCollision(shipCreation)

        val ship = shipStore.create(shipCreation)
        shipDeploymentUseCase.shipDeployed(ship.battleId, shipCreation.playerId, shipCreation.type)
        return ship
    }

    private fun checkNoCollision(shipCreation: ShipCreation) {
        val collisionLocation = findFirstCollisionLocation(shipCreation)
        if (collisionLocation != null)
            throw ShipCollisionException(
                shipCreation.battleId,
                shipCreation.playerId,
                shipCreation.type,
                collisionLocation,
                "Collision at $collisionLocation"
            )
    }

    private fun findFirstCollisionLocation(shipCreation: ShipCreation): Location? {
        val allShips = shipStore.getAllShipsInFleet(shipCreation.battleId, shipCreation.playerId)
        val otherShips = allShips.filterNot { it.type == shipCreation.type }
        if (otherShips.isEmpty())
            return null

        val locations: Set<Location> = computeLocations(shipCreation).toSet()
        for (ship in otherShips) {
            for (location in locations) {
                if (ship.contains(location))
                    return location
            }
        }
        return null
    }

    private fun computeLocations(shipCreation: ShipCreation): List<Location> {
        return computeLocations(shipCreation.location, shipCreation.type.size, shipCreation.orientation)
    }

    private fun computeLocations(location: Location, size: Int, orientation: GridOrientation): List<Location> {
        val locations = ArrayList<Location>()
        locations.add(location)
        if (orientation == GridOrientation.ROW) {
            for (index in 1..size) {
                locations.add(Location(location.x + index, location.y))
            }
        } else {
            for (index in 1..size) {
                locations.add(Location(location.x, location.y + index))
            }
        }
        return locations
    }

    override fun getAllShipsInFleet(battleId: BattleUUID, playerId: PlayerUUID): List<ShipDEntity> {
        return shipStore.getAllShipsInFleet(battleId, playerId)
    }

    override fun getShip(battleId: UUID, playerId: UUID, shipType: ShipType): ShipDEntity {
        return shipStore.findById(battleId, playerId, shipType) ?: throw ShipNotFoundException(
            battleId, playerId, shipType,
            "Cannot found ship $shipType of player $playerId in battle $battleId"
        )
    }

    override fun findShip(battleId: UUID, playerId: UUID, shipType: ShipType): ShipDEntity? {
        return shipStore.findById(battleId, playerId, shipType)
    }
}