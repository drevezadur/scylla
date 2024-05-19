/*
 * Copyright (c) 2024 gofannon.xyz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.drevezerezh.scylla.advanced.webserver.controller

import io.drevezerezh.scylla.advanced.domain.api.player.Player
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.usecase.PlayerUseCaseManager
import io.drevezerezh.scylla.advanced.webserver.controller.PlayerMapper.toJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerCreationJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerUpdateJson
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder


@RestController
@RequestMapping(path = ["/players"])
class PlayerController(
    private val playerUseCaseManager: PlayerUseCaseManager,
    private val  request: HttpServletRequest
) {

    /**
     * Get all players
     */
    @GetMapping(path = ["", "/"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllPlayers(@RequestParam(name = "style", required = false) style: String?): ResponseEntity<Any> {
        LOGGER.info("GET /players")
        LOGGER.info("    style:$style")
        LOGGER.info("    context: "+request.queryString)
        return when (style?.lowercase()) {
            null, "", "id" -> getAllPlayerIds()
            "full" -> getAllFullPlayers()
            else -> getAllPlayersFailure(style)
        }
    }

    private fun getAllPlayerIds(): ResponseEntity<Any> {
        val idList = playerUseCaseManager.getAll().map(Player::id)
        return ResponseEntity.ok(idList)
    }

    private fun getAllFullPlayers(): ResponseEntity<Any> {
        val playerJsonList = playerUseCaseManager.getAll().map(PlayerMapper::toJson)
        return ResponseEntity.ok(playerJsonList)
    }

    private fun getAllPlayersFailure(style: String): ResponseEntity<Any> {
        val content = ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Unsupported style '$style'").type
        return ResponseEntity.badRequest().body(content)
    }


    /**
     * Get a player from its identifier
     * @param id : the identifier of the  player
     * @return the player with 200 code
     */
    @GetMapping(path = ["/{id}", "/{id}/"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPlayerById(@PathVariable("id") id: String): ResponseEntity<PlayerJson> {
        LOGGER.info("GET /players/$id")
        val player = playerUseCaseManager.getById(id)
        val playerJson = toJson(player)
        return ResponseEntity.ok(playerJson)
    }


    /**
     * Create a new player
     * @param playerCreationJson the information about the player to create
     * @return an HTTP response with 201 code and location field filled
     */
    @PostMapping(
        path = ["", "/"],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createPlayer(
        @RequestBody playerCreationJson: PlayerCreationJson,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        LOGGER.info("POST /players")
        val playerCreation = PlayerMapper.toDomain(playerCreationJson)
        val player = playerUseCaseManager.create(playerCreation)
        val uri = ServletUriComponentsBuilder.fromRequest(request)
            .path("/${player.id}")
            .build()
            .toUri()
        return ResponseEntity.created(uri).build()
    }


    /**
     * Update de player
     * @param id the identifier of the player
     * @param updatePlayerJson the information to update
     * @return an HTTP response with 204 code
     */
    @PutMapping(
        path = ["/{id}", "/{id}/"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updatePlayer(
        @PathVariable("id") id: String,
        @RequestBody updatePlayerJson: PlayerUpdateJson
    ): ResponseEntity<PlayerJson> {
        LOGGER.info("PUT /players/$id")
        val updatePlayer = PlayerMapper.toDomain(updatePlayerJson)
        val updatedPlayer = playerUseCaseManager.update(id, updatePlayer)
        val updatedPlayerJson = toJson(updatedPlayer)
        return ResponseEntity.ok(updatedPlayerJson)
    }


    @DeleteMapping(path = ["/{id}", "/{id}/"])
    fun deletePlayer(@PathVariable("id") id: String): ResponseEntity<Void> {
        LOGGER.info("DELETE /players/$id")
        if (!playerUseCaseManager.delete(id))
            throw PlayerNotFoundException(id)

        return ResponseEntity.noContent().build()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PlayerController::class.java)
    }
}