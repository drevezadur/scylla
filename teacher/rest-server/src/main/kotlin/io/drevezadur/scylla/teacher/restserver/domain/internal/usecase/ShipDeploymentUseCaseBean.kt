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

import io.drevezadur.scylla.teacher.restserver.domain.usecase.ShipDeploymentUseCase
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.BattleStore
import io.drevezadur.scylla.teacher.restserver.domain.store.FleetStore
import io.drevezadur.scylla.teacher.restserver.domain.store.ShipStore
import io.drevezadur.scylla.teacher.restserver.lang.*
import org.springframework.stereotype.Service

@Service
class ShipDeploymentUseCaseBean(
    private val battleStore: BattleStore,
    private val fleetStore: FleetStore,
    private val shipStore: ShipStore
) : ShipDeploymentUseCase {

    override fun shipDeployed(battleId: BattleUUID, playerId: PlayerUUID, type: ShipType) {
        val ships = shipStore.getAllShipsInFleet(battleId, playerId)
        if (isCompleteFleet(ships)) {
            markFleetAsDeployed(battleId, playerId)
            if (areAllFleetDeployed(battleId)) {
                markBattleAsDeployed(battleId)
            }
        }
    }

    private fun markFleetAsDeployed(battleId: BattleUUID, playerId: PlayerUUID) {
        val fleet = fleetStore.getFleetByBattleAndPlayer(battleId, playerId)
        fleet.status = FleetStatus.UNHARMED
        fleetStore.save(fleet)
    }

    private fun areAllFleetDeployed(battleId: BattleUUID): Boolean {
        val fleets: List<FleetDEntity> = fleetStore.getAllFleets(battleId)
        val statusSet = fleets.map(FleetDEntity::status).toSet()
        return statusSet.size == 1 && statusSet.contains(FleetStatus.UNHARMED)
    }

    private fun markBattleAsDeployed(battleId: BattleUUID) {
        val battle: BattleDEntity = battleStore.getById(battleId)
        battle.status = BattleStatus.DEPLOYED
        battleStore.save(battle)
    }


    companion object {
        /**
         * Check if a set of ships constitues a complete fleet
         * @param ships all the ships currently in the fleet
         * @return true if all ships is a complete fleet, false if some ships are missing
         */
        private fun isCompleteFleet(ships: List<ShipDEntity>): Boolean {
            val size = ships.map { it.type }.toSet().size
            return size == ShipType.entries.size
        }
    }
}