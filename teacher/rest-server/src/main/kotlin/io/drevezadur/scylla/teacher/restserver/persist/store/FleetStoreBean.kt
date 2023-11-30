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

package io.drevezadur.scylla.teacher.restserver.persist.store

import io.drevezadur.scylla.teacher.restserver.domain.FleetNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.FleetStore
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.FleetStatus
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID
import io.drevezadur.scylla.teacher.restserver.persist.FleetRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.FleetId
import io.drevezadur.scylla.teacher.restserver.persist.model.FleetPEntity
import io.drevezadur.scylla.teacher.restserver.persist.model.LocationMapper
import org.springframework.stereotype.Service

@Service
class FleetStoreBean(
    private val repository: FleetRepository
) : FleetStore {

    override fun create(creation: FleetCreation): FleetDEntity {
        val toCreate = FleetPEntity(
            battleId = creation.battleId,
            playerId = creation.playerId,
            status = FleetStatus.NOT_DEPLOYED
        )
        val created = repository.save(toCreate)
        return toDomain(created)
    }

    private fun toDomain(persisted: FleetPEntity): FleetDEntity {
        val shots: MutableList<Location> = LocationMapper.toLocations(persisted.shotStorage).toMutableList()
        return FleetDEntity(persisted.battleId!!, persisted.playerId!!, persisted.status, shots)
    }

    override fun findFleetByBattleAndPlayer(battleId: BattleUUID, playerId: PlayerUUID): FleetDEntity? {
        val persisted = repository.findByBattleIdAndPlayerId(battleId, playerId) ?: return null
        return toDomain(persisted)
    }

    override fun getFleetByBattleAndPlayer(battleId: BattleUUID, playerId: PlayerUUID): FleetDEntity {
        val persisted = repository.findByBattleIdAndPlayerId(battleId, playerId)
            ?: throw FleetNotFoundException(
                battleId,
                playerId,
                "Cannot find fleet of player '$playerId' in battle '$battleId'"
            )

        return toDomain(persisted)
    }

    override fun getAllFleetsOfBattle(battleId: BattleUUID): List<FleetDEntity> {
        return repository.findAllByBattleId(battleId)
            .map(this::toDomain)
            .toList()
    }

    override fun getAll(): List<FleetDEntity> {
        return repository.findAll()
            .map(this::toDomain)
    }


    override fun isAllFleetsOfBattleHaveStatus(battleId: BattleUUID, status: FleetStatus): Boolean {
        return repository.findAllByBattleId(battleId)
            .all { it.status == status }
    }


    override fun getAllFleets(battleId: BattleUUID): List<FleetDEntity> {
        return repository.findAllByBattleId(battleId)
            .map(this::toDomain)
    }


    override fun getOpponentFleetId(battleId: BattleUUID, playerId: PlayerUUID): PlayerUUID {
        return repository.getOpponentIdOf(battleId, playerId)
            ?: throw FleetNotFoundException(battleId, playerId, "Cannot find opponent of fleet '$battleId / $playerId'")
    }


    override fun getOpponentFleet(battleId: BattleUUID, playerId: PlayerUUID): FleetDEntity {
        val persisted = repository.findAllByBattleId(battleId).firstOrNull { it.playerId != playerId }
            ?: throw FleetNotFoundException(battleId, playerId, "Cannot find opponent of fleet '$battleId / $playerId'")
        return toDomain(persisted)
    }


    override fun delete(battleId: BattleUUID, playerId: PlayerUUID) {
        repository.deleteById(FleetId(battleId, playerId))
    }


    override fun save(fleet: FleetDEntity) {
        repository.save(toPersistance(fleet))
    }

    private fun toPersistance(fleet: FleetDEntity): FleetPEntity {
        val shots = LocationMapper.toString(fleet.shots)
        return FleetPEntity(
            fleet.battleId,
            fleet.playerId,
            fleet.status,
            shots
        )
    }
}