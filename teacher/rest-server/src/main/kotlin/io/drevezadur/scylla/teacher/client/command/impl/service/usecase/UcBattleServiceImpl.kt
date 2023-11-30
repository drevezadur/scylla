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
import io.drevezadur.scylla.teacher.client.command.impl.service.BattleService
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleCreation
import io.drevezadur.scylla.teacher.restserver.domain.usecase.BattleUseCaseManager
import io.drevezadur.scylla.teacher.restserver.service.model.BattleCreationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.BattleMapping
import io.drevezadur.scylla.teacher.restserver.service.model.BattlePojo
import java.util.*

class UcBattleServiceImpl(
    private val battleUseCaseManager: BattleUseCaseManager
) : BattleService {


    override fun create(body: BattleCreationPojo): UUID {
        val battleCreation = BattleCreation(
            body.player1Id,
            body.player2Id
        )

        try {
            return battleUseCaseManager.createBattle(battleCreation).id
        } catch (ex: RuntimeException) {
            throw GameCommandException("Fail to create battle $body")
        }
    }


    override fun delete(battleId: UUID) {
        try {
            battleUseCaseManager.delete(battleId)
        } catch (ex: RuntimeException) {
            throw GameCommandException("Fail to delete battle $battleId")
        }
    }


    override fun getAll(): List<BattlePojo> {
        return battleUseCaseManager.getAllBattles()
            .map(BattleMapping::toPojo)
    }

    override fun findById(battleId: UUID): BattlePojo? {
        val battle = battleUseCaseManager.findById(battleId) ?: return null
        return BattleMapping.toPojo(battle)
    }
}