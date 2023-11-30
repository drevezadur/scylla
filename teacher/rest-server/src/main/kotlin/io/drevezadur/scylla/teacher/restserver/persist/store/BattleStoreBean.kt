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

import io.drevezadur.scylla.teacher.restserver.common.UuidProvider
import io.drevezadur.scylla.teacher.restserver.domain.BattleNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.BattleStore
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.persist.BattleRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.BattlePEntity
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class BattleStoreBean(
    private val repository: BattleRepository,
    private val uuidProvider: UuidProvider,
) : BattleStore {

    override fun create(creation: BattleCreation): BattleDEntity {
        val toCreate = BattlePEntity(
            id = uuidProvider.create(),
            player1Id = creation.player1Id,
            player2Id = creation.player2Id,
            shooterPlayer = creation.player1Id
        )
        val created = repository.save(toCreate)
        return toDomain(created)
    }

    private fun toDomain(persisted: BattlePEntity): BattleDEntity {
        return BattleDEntity(
            id = persisted.id!!,
            player1Id = persisted.player1Id!!,
            player2Id = persisted.player2Id!!,
            status = persisted.status,
            shooterPlayerId = persisted.shooterPlayer!!,
            winnerId = persisted.winner
        )
    }


    override fun findById(id: BattleUUID): BattleDEntity? {
        val persisted = repository.findById(id).getOrNull() ?: return null
        return toDomain(persisted)
    }

    override fun getById(id: BattleUUID): BattleDEntity {
        val persisted =
            repository.findById(id).getOrNull() ?: throw BattleNotFoundException(id, "Cannot found battle $id")
        return toDomain(persisted)
    }


    override fun save(battle: BattleDEntity) {
        repository.save(toPersistance(battle))
    }

    private fun toPersistance(battle: BattleDEntity): BattlePEntity {
        return BattlePEntity(
            battle.id,
            battle.player1Id,
            battle.player2Id,
            battle.status,
            battle.shooterPlayerId,
            battle.winnerId
        )
    }

    override fun getAll(): List<BattleDEntity> {
        return repository.findAll()
            .map(this::toDomain)
    }

    override fun delete(id: BattleUUID) {
        repository.deleteById(id)
    }
}