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

import io.drevezadur.scylla.teacher.restserver.domain.model.ShipCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipMapping
import io.drevezadur.scylla.teacher.restserver.domain.store.ShipStore
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.persist.ShipRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.ShipId
import io.drevezadur.scylla.teacher.restserver.persist.model.ShipPEntity
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class ShipStoreBean(
    private val repository: ShipRepository
) : ShipStore {

    override fun create(creation: ShipCreation): ShipDEntity {
        val toCreate = ShipMapping.toPersistedShip(creation)
        val created = repository.save(toCreate)
        return toShip(created)
    }

    private fun toShip(persisted: ShipPEntity): ShipDEntity {
        return ShipDEntity(
            persisted.battleId!!,
            persisted.playerId!!,
            persisted.type!!,
            persisted.getOriginLocation(),
            persisted.orientation,
            persisted.status,
            persisted.getHitLocations().toSet()
        )
    }

    override fun save(ship: ShipDEntity) {
        repository.save(toPersistance(ship))
    }

    private fun toPersistance(ship: ShipDEntity): ShipPEntity {
        return ShipPEntity(
            ship.battleId,
            ship.playerId,
            ship.type,
            ship.status,
            ship.origin,
            ship.orientation,
            ship.hits.toList()
        )
    }

    override fun findById(battleId: BattleUUID, playerId: PlayerUUID, type: ShipType): ShipDEntity? {
        val persisted = repository.findById(ShipId(battleId, playerId, type)).getOrNull() ?: return null
        return toShip(persisted)
    }

    override fun getAllShipsInFleet(battleId: BattleUUID, playerId: PlayerUUID): List<ShipDEntity> {
        return repository.findAllByBattleIdAndPlayerId(battleId, playerId)
            .map(this::toShip)
    }

    override fun getAll(): List<ShipDEntity> {
        return repository.findAll()
            .map(this::toShip)
    }

    override fun delete(battleId: BattleUUID, playerId: PlayerUUID, type: ShipType) {
        repository.deleteById(ShipId(battleId, playerId, type))
    }
}