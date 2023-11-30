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

import io.drevezadur.scylla.teacher.restserver.domain.usecase.ShotUseCaseManager
import io.drevezadur.scylla.teacher.restserver.service.model.LocationMapping.toDomain
import io.drevezadur.scylla.teacher.restserver.service.model.LocationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.ShotReportMapping.toPojo
import io.drevezadur.scylla.teacher.restserver.service.pathcontext.PathContextFactory
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/battles")
class ShotRestService(
    @Autowired
    private val shotUseCaseManager: ShotUseCaseManager,
    @Autowired
    private val contextConverterFactory: PathContextFactory
) {

    /**
     * Fire at the opponent's fleet
     */
    @PostMapping(
        value = ["/{battleId}/players/{playerId}/fleet/shot"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun fireAtFleet(
        request: HttpServletRequest,
        @PathVariable("battleId") battleIdRaw: String,
        @PathVariable("playerId") playerIdRaw: String,
        @RequestBody targetLocation: LocationPojo
    ): ResponseEntity<out Any> {
        val command = contextConverterFactory.createBattlePlayerContext(battleIdRaw, playerIdRaw)
        val target = toDomain(targetLocation)

        val shotReport = shotUseCaseManager.shotAt(command.battleId, command.playerId, target)

        val shotReportPojo = toPojo(shotReport)
        return ResponseEntity.ok(shotReportPojo)
    }
}
