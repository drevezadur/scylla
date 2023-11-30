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

package io.drevezadur.scylla.teacher.restserver.service.model

import io.drevezadur.scylla.teacher.restserver.domain.model.ShipCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipDEntity
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper
import io.drevezadur.scylla.teacher.restserver.service.util.RestException

object ShipMapping {

    fun toShipCreation(battleId: BattleUUID, playerId: PlayerUUID, shipDeployment: ShipDeploymentBody): ShipCreation {
        return ShipCreation(
            battleId,
            playerId,
            shipDeployment.type,
            Location(shipDeployment.x, shipDeployment.y),
            shipDeployment.orientation
        )
    }

    fun toPojo(ship: ShipDEntity): ShipPojo {
        return ShipPojo(
            ship.battleId,
            ship.playerId,
            ship.type,
            ship.status,
            LocationMapping.toService(ship.origin),
            ship.orientation,
            ship.hits.map(LocationMapping::toService)
        )
    }

    fun toShipType(shipTypeRaw: String): ShipType {
        try {
            return ShipType.valueOf(shipTypeRaw)
        } catch (ex: IllegalArgumentException) {
            throw RestException(ResponseHelper.toUnsupportedShipTypeResponse(shipTypeRaw))
        }
    }
}
