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

package io.drevezadur.scylla.teacher.client.command.impl.service.usecase

import io.drevezadur.scylla.teacher.client.command.GameCommandException
import io.drevezadur.scylla.teacher.client.command.impl.service.ShipService
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipCreation
import io.drevezadur.scylla.teacher.restserver.domain.usecase.ShipUseCaseManager
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.service.model.ShipDeploymentBody
import io.drevezadur.scylla.teacher.restserver.service.model.ShipMapping
import io.drevezadur.scylla.teacher.restserver.service.model.ShipPojo
import java.util.*

class UcShipServiceImpl(
    private val shipUseCaseManager: ShipUseCaseManager
) : ShipService {

    override fun deploy(battleId: UUID, playerId: UUID, content: ShipDeploymentBody) {
        try {

            val location = Location(content.x, content.y)
            val creation = ShipCreation(battleId, playerId, content.type, location, content.orientation)
            shipUseCaseManager.deploy(creation)

        } catch (ex: RuntimeException) {
            throw GameCommandException("Fail to create ship $battleId $playerId '$content'")
        }
    }

    override fun getAllInFleet(battleId: UUID, playerId: UUID): List<ShipPojo> {
        try {
            return shipUseCaseManager.getAllShipsInFleet(battleId, playerId).map(ShipMapping::toPojo)
        } catch (ex: RuntimeException) {
            throw GameCommandException("Fail to get all ships of player $playerId in battle $battleId")
        }
    }

    override fun findById(battleId: UUID, playerId: UUID, type: ShipType): ShipPojo? {
        val ship = shipUseCaseManager.findShip(battleId, playerId, type) ?: return null
        return ShipMapping.toPojo(ship)
    }
}