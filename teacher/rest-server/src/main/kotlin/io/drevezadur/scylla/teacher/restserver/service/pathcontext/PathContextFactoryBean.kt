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

package io.drevezadur.scylla.teacher.restserver.service.pathcontext

import io.drevezadur.scylla.teacher.restserver.common.UuidProvider
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.service.model.ShipMapping
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper
import io.drevezadur.scylla.teacher.restserver.service.util.RestException
import org.springframework.stereotype.Service

@Service
class PathContextFactoryBean(
    private val uuidProvider: UuidProvider
) : PathContextFactory {

    override fun createPlayerContext(playerIdRaw: String): PlayerPathContext {
        val playerId = uuidProvider.fromString(playerIdRaw) {
            throw RestException(ResponseHelper.toPlayerNotFoundResponse(playerIdRaw))
        }
        return PlayerPathContext(playerId)
    }

    override fun createBattleContext(battleIdRaw: String): BattlePathContext {
        val battleId = uuidProvider.fromString(battleIdRaw) {
            throw RestException(ResponseHelper.toBattleNotFoundResponse(it))
        }
        return BattlePathContext(battleId)
    }

    override fun createBattlePlayerContext(battleIdRaw: String, playerIdRaw: String): BattlePlayerPathContext {
        val battleId = uuidProvider.fromString(battleIdRaw) {
            throw RestException(ResponseHelper.toBattleNotFoundResponse(it))
        }
        val playerId = uuidProvider.fromString(playerIdRaw) {
            throw RestException(ResponseHelper.toPlayerNotFoundInBattleResponse(battleIdRaw, playerIdRaw))
        }
        return BattlePlayerPathContext(battleId, playerId)
    }

    override fun createShipContext(battleIdRaw: String, playerIdRaw: String, shipTypeRaw: String): ShipPathContext {
        val battleId = uuidProvider.fromString(battleIdRaw) {
            throw RestException(ResponseHelper.toBattleNotFoundResponse(it))
        }
        val playerId = uuidProvider.fromString(playerIdRaw) {
            throw RestException(ResponseHelper.toPlayerNotFoundInBattleResponse(battleIdRaw, playerIdRaw))
        }
        val shipType: ShipType = ShipMapping.toShipType(shipTypeRaw)
        return ShipPathContext(battleId, playerId, shipType)
    }
}