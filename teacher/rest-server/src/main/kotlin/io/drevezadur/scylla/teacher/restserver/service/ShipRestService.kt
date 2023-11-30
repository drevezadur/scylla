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

import io.drevezadur.scylla.teacher.restserver.domain.model.ShipDEntity
import io.drevezadur.scylla.teacher.restserver.domain.usecase.ShipUseCaseManager
import io.drevezadur.scylla.teacher.restserver.service.model.ShipDeploymentBody
import io.drevezadur.scylla.teacher.restserver.service.model.ShipMapping
import io.drevezadur.scylla.teacher.restserver.service.model.ShipMapping.toShipCreation
import io.drevezadur.scylla.teacher.restserver.service.model.ShipPojo
import io.drevezadur.scylla.teacher.restserver.service.pathcontext.PathContextFactory
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.createdAt
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/battles")
class ShipRestService(
    @Autowired
    private val shipUseCaseManager: ShipUseCaseManager,
    @Autowired
    private val contextConverterFactory: PathContextFactory,
) {
    /**
     * Deploy a ship in a fleet
     */
    @PostMapping(
        value = ["/{battleId}/players/{playerId}/fleet/ships"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun deployShip(
        request: HttpServletRequest,
        @PathVariable("battleId") battleIdRaw: String,
        @PathVariable("playerId") playerIdRaw: String,
        @RequestBody shipDeployment: ShipDeploymentBody
    ): ResponseEntity<out Any> {
        val command = contextConverterFactory.createBattlePlayerContext(battleIdRaw, playerIdRaw)

        val shipCreation = toShipCreation(
            command.battleId,
            command.playerId,
            shipDeployment
        )
        val ship = shipUseCaseManager.deploy(shipCreation)

        return createdAt(request.requestURI + "/${ship.type}")
    }

    @GetMapping(
        value = ["/{battleId}/players/{playerId}/fleet/ships"],
        produces = ["application/json"]
    )
    fun getAllShipsInFleet(
        request: HttpServletRequest,
        @PathVariable("battleId") battleIdRaw: String,
        @PathVariable("playerId") playerIdRaw: String
    ): ResponseEntity<out Any> {
        val command = contextConverterFactory.createBattlePlayerContext(battleIdRaw, playerIdRaw)
        val shipList = shipUseCaseManager.getAllShipsInFleet(command.battleId, command.playerId)
        return ok(shipList)
    }

    private fun ok(shipList: List<ShipDEntity>): ResponseEntity<List<ShipPojo>> {
        val shipPojoList = shipList.map(ShipMapping::toPojo)
        return ResponseEntity.ok(shipPojoList)
    }

    @GetMapping(
        value = ["/{battleId}/players/{playerId}/fleet/ships/{shipType}"],
        produces = ["application/json"]
    )
    fun getShip(
        request: HttpServletRequest,
        @PathVariable("battleId") battleIdRaw: String,
        @PathVariable("playerId") playerIdRaw: String,
        @PathVariable("shipType") shipTypeRaw: String
    ): ResponseEntity<out Any> {
        val command = contextConverterFactory.createShipContext(battleIdRaw, playerIdRaw, shipTypeRaw)
        val ship = shipUseCaseManager.getShip(command.battleId, command.playerId, command.shipType)
        return ok(ship)
    }

    private fun ok(ship: ShipDEntity): ResponseEntity<ShipPojo> {
        val shipPojo = ShipMapping.toPojo(ship)
        return ResponseEntity.ok(shipPojo)
    }
}