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

package io.drevezadur.scylla.teacher.restserver.domain.store

import io.drevezadur.scylla.teacher.restserver.domain.model.FleetDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetCreation
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.FleetStatus
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID

interface FleetStore {

    fun create(creation: FleetCreation): FleetDEntity

    fun findFleetByBattleAndPlayer(battleId: BattleUUID, playerId: PlayerUUID): FleetDEntity?

    fun getFleetByBattleAndPlayer(battleId: BattleUUID, playerId: PlayerUUID): FleetDEntity

    fun getOpponentFleet(battleId: BattleUUID, playerId: PlayerUUID): FleetDEntity

    fun getAllFleetsOfBattle(battleId: BattleUUID): List<FleetDEntity>

    fun getAll(): List<FleetDEntity>

    fun isAllFleetsOfBattleHaveStatus(battleId: BattleUUID, status: FleetStatus): Boolean

    fun getOpponentFleetId(battleId: BattleUUID, playerId: PlayerUUID): PlayerUUID

    fun getAllFleets(battleId: BattleUUID): List<FleetDEntity>

    fun delete(battleId: BattleUUID, playerId: PlayerUUID)

    fun save(fleet: FleetDEntity)

}