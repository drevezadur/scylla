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

import io.drevezadur.scylla.teacher.restserver.domain.FleetNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.FleetStore
import io.drevezadur.scylla.teacher.restserver.domain.usecase.FleetUseCaseManager
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FleetUseCaseManagerImpl(
    @Autowired
    private val fleetStore: FleetStore
) : FleetUseCaseManager {
    override fun findFleet(battleId: BattleUUID, playerId: PlayerUUID): FleetDEntity? {
        return fleetStore.findFleetByBattleAndPlayer(battleId, playerId)
    }

    override fun getFleet(battleId: BattleUUID, playerId: PlayerUUID): FleetDEntity {
        return fleetStore.findFleetByBattleAndPlayer(battleId, playerId) ?: throw FleetNotFoundException(
            battleId,
            playerId,
            "Cannot found fleet of player $playerId in battle $battleId"
        )
    }
}