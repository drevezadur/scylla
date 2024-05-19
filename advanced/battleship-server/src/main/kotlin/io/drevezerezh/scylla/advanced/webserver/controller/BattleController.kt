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

import io.drevezerezh.scylla.advanced.domain.api.battle.Battle
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleEndedException
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.battle.NotPlayerTurnException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipAlreadyDeployedException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipDeployment
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipOutOfGridException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipOverlapException
import io.drevezerezh.scylla.advanced.domain.api.shot.Shot
import io.drevezerezh.scylla.advanced.domain.api.shot.ShotReport
import io.drevezerezh.scylla.advanced.domain.api.usecase.BattleUseCaseManager
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.webserver.controller.RestHelper.createCreationResponse
import io.drevezerezh.scylla.advanced.webserver.controller.dto.BattleCreationJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.BattleJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.ShipDeploymentJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.ShotJson
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/battles"])
class BattleController(
    private val battleUseCaseManager: BattleUseCaseManager
) {

    @GetMapping(path = ["", "/"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllBattles(@RequestParam(name = "style", required = false) style: String?): ResponseEntity<Any> {
        LOGGER.info("GET /battles")
        return when (style?.lowercase()) {
            null, "", "id" -> getAllBattleIds()
            "full" -> getAllFullBattles()
            else -> getAllBattlesFailure(style)
        }
    }

    private fun getAllBattleIds(): ResponseEntity<Any> {
        val idList = battleUseCaseManager.getAll().map(Battle::id)
        return ResponseEntity.ok(idList)
    }

    private fun getAllFullBattles(): ResponseEntity<Any> {
        val playerJsonList = battleUseCaseManager.getAll().map(BattleMapper::toJson)
        return ResponseEntity.ok(playerJsonList)
    }

    private fun getAllBattlesFailure(style: String): ResponseEntity<Any> {
        val content = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Unsupported style '$style'").type
        return ResponseEntity.badRequest().body(content)
    }

    /**
     * Get a player from its identifier
     * @param id : the identifier of the  player
     * @return the player with 200 code
     */
    @GetMapping(path = ["/{id}", "/{id}/"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBattleById(@PathVariable("id") id: String): ResponseEntity<BattleJson> {
        LOGGER.info("GET /battles/$id")
        val battle = battleUseCaseManager.getById(id)
        val battleJson = BattleMapper.toJson(battle)
        return ResponseEntity.ok(battleJson)
    }


    /**
     * Create a new player
     * @param battleCreationJson the information about the battle to create
     * @return an HTTP response with 201 code and location field filled
     */
    @PostMapping(
        path = ["", "/"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE]
    )
    fun createBattle(
        @RequestBody battleCreationJson: BattleCreationJson,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        LOGGER.info("POST /battles")
        val battleCreation = BattleMapper.toDomain(battleCreationJson)
        val battle = battleUseCaseManager.create(battleCreation)
        return createCreationResponse(request, "/${battle.id}")
    }


    @DeleteMapping(path = ["/{id}", "/{id}/"])
    fun deleteBattle(@PathVariable("id") id: String): ResponseEntity<Void> {
        LOGGER.info("DELETE /battles/$id")
        if (!battleUseCaseManager.delete(id))
            throw BattleNotFoundException(id)

        return ResponseEntity.noContent().build()
    }


    @PostMapping(
        path = ["/{id}/fleets/{player}/ships"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE]
    )
    fun deployShip(
        @PathVariable("id") battleId: String,
        @PathVariable("player") rawPlayer: String,
        @RequestBody shipDeploymentJson: ShipDeploymentJson,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        LOGGER.info("POST /battles/$battleId/fleets/$rawPlayer/ships")
        val player = toPlayerOrderDomain(battleId, rawPlayer)
        val shipDeployment = ShipMapper.toDomain(battleId, player, shipDeploymentJson)
        deployShip(shipDeployment)
        val shipPath = "/${battleId}/fleets/$rawPlayer/ships/${shipDeployment.shipType}"
        return createCreationResponse(request, shipPath)
    }


    private fun toPlayerOrderDomain(battleId: String, rawPlayer: String): BattlePlayer {
        try {
            return ShipMapper.toDomainBattlePlayer(rawPlayer)
        } catch (ex: IllegalArgumentException) {
            throw InvalidPathVariableException(
                title = "Cannot found fleet of player '$rawPlayer'. Only values 'first' and 'second' are allowed",
                instancePath = "/battles/$battleId/fleets/$rawPlayer"
            )
        }
    }


    private fun deployShip(shipDeployment: ShipDeployment) {
        try {

            battleUseCaseManager.deployShip(shipDeployment)

        } catch (ex: ShipOverlapException) {
            throw RestProblemException(
                status = HttpStatus.BAD_REQUEST,
                title = "Fail to deploy ship because is override another one",
                type = "/errors/ship-deployment",
                instance = "/battles/${shipDeployment.battleId}/fleets/${shipDeployment.player.name.lowercase()}/ships",
                properties = mapOf(
                    Pair("battleId", shipDeployment.battleId),
                    Pair("player", shipDeployment.player.name),
                    Pair("ship-type", shipDeployment.shipType.name),
                    Pair("location", shipDeployment.location.toText()),
                    Pair("orientation", shipDeployment.orientation.name),
                    Pair("overlapped-ship-type", ex.overlappedShipType.name),
                ),
                cause = ex
            )
        } catch (ex: ShipAlreadyDeployedException) {
            throw RestProblemException(
                status = HttpStatus.BAD_REQUEST,
                title = "Fail to deploy ship because it is already deployed",
                type = "/errors/ship-deployment",
                instance = "/battles/${shipDeployment.battleId}/fleets/${shipDeployment.player.name.lowercase()}/ships",
                properties = mapOf(
                    Pair("battleId", shipDeployment.battleId),
                    Pair("player", shipDeployment.player.name),
                    Pair("ship-type", shipDeployment.shipType.name)
                ),
                cause = ex
            )
        } catch (ex: ShipOutOfGridException) {
            throw RestProblemException(
                status = HttpStatus.BAD_REQUEST,
                title = "Fail to deploy ship because it is out of the grid",
                type = "/errors/ship-deployment",
                instance = "/battles/${shipDeployment.battleId}/fleets/${shipDeployment.player.name.lowercase()}/ships",
                properties = mapOf(
                    Pair("battleId", shipDeployment.battleId),
                    Pair("player", shipDeployment.player.name),
                    Pair("ship-type", shipDeployment.shipType.name),
                    Pair("location", shipDeployment.location.toText()),
                    Pair("orientation", shipDeployment.orientation.name)
                ),
                cause = ex
            )
        }
    }


    @PostMapping(
        path = ["/{id}/fleets/{player}/shot"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun shot(
        @PathVariable("id") battleId: String,
        @PathVariable("player") rawPlayer: String,
        @RequestBody shotJson: ShotJson,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        LOGGER.info("POST /${request.requestURI}/$battleId/fleets/$rawPlayer/shot")
        val player = toPlayerOrderDomain(battleId, rawPlayer)
        val shot = Shot(battleId, player, shotJson.target)
        val report = shot(shot)
        val reportJson = ShotMapper.toDto(report)
        return ResponseEntity.ok(reportJson)
    }


    private fun shot(shot: Shot): ShotReport {
        try {
            return battleUseCaseManager.shot(shot)
        } catch (ex: BattleNotFoundException) {
            throw RestProblemException(
                status = HttpStatus.NOT_FOUND,
                title = "Cannot found battle with id '${ex.battleId}'",
                type = "/errors/not-found",
                instance = "/battles/${shot.battleId}/fleets/${shot.shootingPlayer}/shot",
            )
        } catch (ex: BattleEndedException) {
            throw RestProblemException(
                status = HttpStatus.BAD_REQUEST,
                title = "Battle with id '${ex.battleId}' is already ended",
                type = "/errors/invalid-state",
                instance = "/battles/${shot.battleId}/fleets/${shot.shootingPlayer}/shot",
            )
        } catch (ex:NotPlayerTurnException) {
            throw RestProblemException(
                status = HttpStatus.BAD_REQUEST,
                title = "It is not the turn of player '${ex.badPlayer}' in battle with id '${ex.battleId}'",
                type = "/errors/invalid-state",
                instance = "/battles/${shot.battleId}/fleets/${shot.shootingPlayer}/shot",
            )
        }
    }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(BattleController::class.java)
    }
}