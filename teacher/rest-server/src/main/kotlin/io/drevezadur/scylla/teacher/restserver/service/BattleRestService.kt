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

package io.drevezadur.scylla.teacher.restserver.service

import io.drevezadur.scylla.teacher.restserver.domain.model.BattleCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleDEntity
import io.drevezadur.scylla.teacher.restserver.domain.usecase.BattleUseCaseManager
import io.drevezadur.scylla.teacher.restserver.service.model.BattleCreationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.BattleMapping
import io.drevezadur.scylla.teacher.restserver.service.model.BattlePojo
import io.drevezadur.scylla.teacher.restserver.service.pathcontext.PathContextFactory
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.createdAt
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.noContent
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/battles")
class BattleRestService(
    @Autowired
    private val battleUseCaseManager: BattleUseCaseManager,
    @Autowired
    private val contextConverterFactory: PathContextFactory
) {

    /**
     * Get all battles
     * @return battle list as POJO
     */
    @GetMapping(produces = ["application/json"])
    fun getAll(): ResponseEntity<out Any> {
        val battleList = battleUseCaseManager.getAllBattles()
        return ok(battleList)
    }

    private fun ok(battleList: List<BattleDEntity>): ResponseEntity<List<BattlePojo>> {
        val battlePojoList = battleList
            .map(BattleMapping::toPojo)
            .toList()
        return ResponseEntity.ok(battlePojoList)
    }


    /**
     * Create a battle
     * @param request
     * @param battleCreation
     * @return battle POJO
     */
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun create(
        request: HttpServletRequest,
        @RequestBody battleCreation: BattleCreationPojo
    ): ResponseEntity<out Any> {
        val battleCreationD = BattleCreation(battleCreation.player1Id, battleCreation.player2Id)
        val createdBattle = battleUseCaseManager.createBattle(battleCreationD)
        return createdAt(request.requestURI + "/${createdBattle.id}")
    }

    /**
     * Delete a battle
     * @param playerIdRaw the player identifier in string format
     * @return the HTTP response
     */
    @DeleteMapping(value = ["/{id}"], produces = ["application/json"])
    fun delete(@PathVariable("id") playerIdRaw: String): ResponseEntity<out Any> {
        val context = contextConverterFactory.createPlayerContext(playerIdRaw)
        battleUseCaseManager.delete(context.playerId)
        return noContent()
    }

    /**
     * Find a battle from its identifier
     */
    @GetMapping(value = ["/{id}"], produces = ["application/json"])
    fun getById(@PathVariable("id") battleIdRaw: String): ResponseEntity<out Any> {
        val context = contextConverterFactory.createBattleContext(battleIdRaw)
        val battle = battleUseCaseManager.getById(context.battleId)
        return ok(battle)
    }

    private fun ok(battle: BattleDEntity): ResponseEntity<BattlePojo> {
        val battlePojo = BattleMapping.toPojo(battle)
        return ResponseEntity.ok(battlePojo)
    }
}
