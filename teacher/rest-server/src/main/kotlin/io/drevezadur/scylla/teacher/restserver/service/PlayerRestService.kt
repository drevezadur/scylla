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

import io.drevezadur.scylla.teacher.restserver.domain.model.PlayerDEntity
import io.drevezadur.scylla.teacher.restserver.domain.usecase.PlayerUseCaseManager
import io.drevezadur.scylla.teacher.restserver.service.model.PlayerCreationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.PlayerMapping
import io.drevezadur.scylla.teacher.restserver.service.model.PlayerPojo
import io.drevezadur.scylla.teacher.restserver.service.pathcontext.PathContextFactory
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.createdAt
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.noContent
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.toPlayerNotFoundResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/players")
class PlayerRestService(
    @Autowired
    private val playerUseCaseManager: PlayerUseCaseManager,
    @Autowired
    private val contextConverterFactory: PathContextFactory
) {

    @GetMapping(produces = ["application/json"])
    fun getAll(): ResponseEntity<out Any> {
        val playerList = playerUseCaseManager.getAllPlayers()
        return ok(playerList)
    }

    private fun ok(playerList: List<PlayerDEntity>): ResponseEntity<List<PlayerPojo>> {
        val playerPojoList = playerList
            .map(PlayerMapping::toPojo)
            .toList()
        return ResponseEntity.ok(playerPojoList)
    }

    @GetMapping(value = ["/{id}"], produces = ["application/json"])
    fun getById(@PathVariable("id") playerIdRaw: String): ResponseEntity<out Any> {
        val playerCommand = contextConverterFactory.createPlayerContext(playerIdRaw)
        val player = playerUseCaseManager.findById(playerCommand.playerId)
        return if (player != null)
            ok(player)
        else
            toPlayerNotFoundResponse(playerIdRaw)
    }

    private fun ok(player: PlayerDEntity): ResponseEntity<PlayerPojo> {
        return ResponseEntity.ok(PlayerMapping.toPojo(player))
    }

    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun create(
        request: HttpServletRequest,
        @RequestBody playerCreation: PlayerCreationPojo
    ): ResponseEntity<out Any> {
        val createdPlayer = playerUseCaseManager.create(playerCreation.name)
        return createdAt(request.requestURI + "/${createdPlayer.id}")
    }

    @DeleteMapping(value = ["/{id}"], produces = ["application/json"])
    fun deleteUser(@PathVariable("id") playerIdRaw: String): ResponseEntity<out Any> {
        val playerCommand = contextConverterFactory.createPlayerContext(playerIdRaw)
        playerUseCaseManager.delete(playerCommand.playerId)
        return noContent()
    }
}