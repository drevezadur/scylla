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

import io.drevezadur.scylla.teacher.restserver.domain.model.FleetDEntity
import io.drevezadur.scylla.teacher.restserver.domain.usecase.FleetUseCaseManager
import io.drevezadur.scylla.teacher.restserver.service.model.FleetMapping.toPojo
import io.drevezadur.scylla.teacher.restserver.service.model.FleetPojo
import io.drevezadur.scylla.teacher.restserver.service.pathcontext.PathContextFactory
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/battles")
class FleetRestService(
    @Autowired
    private val fleetUseCaseManager: FleetUseCaseManager,
    @Autowired
    private val contextConverterFactory: PathContextFactory
) {
    /**
     * Deploy a ship in a fleet
     */
    @GetMapping(
        value = ["/{battleId}/players/{playerId}/fleet/"],
        produces = ["application/json"]
    )
    fun getFleet(
        request: HttpServletRequest,
        @PathVariable("battleId") battleIdRaw: String,
        @PathVariable("playerId") playerIdRaw: String
    ): ResponseEntity<out Any> {
        val command = contextConverterFactory.createBattlePlayerContext(battleIdRaw, playerIdRaw)
        val fleet = fleetUseCaseManager.getFleet(command.battleId, command.playerId)
        return ok(fleet)
    }

    private fun ok(fleet: FleetDEntity): ResponseEntity<FleetPojo> {
        val fleetPojo = toPojo(fleet)
        return ResponseEntity.ok(fleetPojo)
    }
}
