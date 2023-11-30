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

import io.drevezadur.scylla.teacher.restserver.domain.BattleNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetCreation
import io.drevezadur.scylla.teacher.restserver.domain.store.BattleStore
import io.drevezadur.scylla.teacher.restserver.domain.store.FleetStore
import io.drevezadur.scylla.teacher.restserver.domain.usecase.BattleUseCaseManager
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BattleUseCaseManagerImpl(
    @Autowired
    private val battleStore: BattleStore,
    @Autowired
    private val fleetStore: FleetStore
) : BattleUseCaseManager {
    override fun createBattle(battleCreation: BattleCreation): BattleDEntity {
        val battle = battleStore.create(battleCreation)
        createFleet(battle.id, battle.player1Id)
        createFleet(battle.id, battle.player2Id)
        return battle
    }

    private fun createFleet(battleId: BattleUUID, playerId: PlayerUUID) {
        fleetStore.create(FleetCreation(battleId, playerId))
    }

    override fun findById(battleId: BattleUUID): BattleDEntity? {
        return battleStore.findById(battleId)
    }

    override fun getById(battleId: BattleUUID): BattleDEntity {
        return battleStore.findById(battleId) ?: throw BattleNotFoundException(
            battleId,
            "Cannot found battle $battleId"
        )
    }

    override fun delete(battleId: BattleUUID) {
        return battleStore.delete(battleId)
    }

    override fun getAllBattles(): List<BattleDEntity> {
        return battleStore.getAll()
    }
}