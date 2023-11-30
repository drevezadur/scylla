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

package io.drevezadur.scylla.teacher.restserver.service.util

import io.drevezadur.scylla.teacher.restserver.domain.*
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.toBattleNotFoundResponse
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.toFleetNotFoundResponse
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.toPlayerNotFoundResponse
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.toShipCollisionResponse
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.toShipNotFoundResponse
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper.toUnexpectedBattleStatusResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [RestException::class])
    protected fun handleRestException(ex: RestException, request: WebRequest): ResponseEntity<Any> {
        return handleExceptionInternal(ex, ex.body, HttpHeaders(), ex.httpCode, request)!!
    }

    @ExceptionHandler(value = [BattleNotFoundException::class])
    protected fun handleBattleNotFound(
        ex: BattleNotFoundException,
        request: WebRequest
    ): ResponseEntity<HttpErrorContent> {
        return toBattleNotFoundResponse(ex.battleId)
    }

    @ExceptionHandler(value = [FleetNotFoundException::class])
    protected fun handleFleetNotFound(
        ex: FleetNotFoundException,
        request: WebRequest
    ): ResponseEntity<HttpErrorContent> {
        return toFleetNotFoundResponse(ex.battleId, ex.playerId)
    }

    @ExceptionHandler(value = [ShipNotFoundException::class])
    protected fun handleShipNotFound(
        ex: ShipNotFoundException,
        request: WebRequest
    ): ResponseEntity<HttpErrorContent> {
        return toShipNotFoundResponse(ex.battleId, ex.playerId, ex.type)
    }

    @ExceptionHandler(value = [ShipCollisionException::class])
    protected fun handleShipCollision(
        ex: ShipCollisionException,
        request: WebRequest
    ): ResponseEntity<HttpErrorContent> {
        return toShipCollisionResponse(ex.battleId, ex.playerId, ex.type, ex.location)
    }

    @ExceptionHandler(value = [UnexpectedBattleStatusException::class])
    protected fun handleUnexpectedBattleStatus(
        ex: UnexpectedBattleStatusException,
        request: WebRequest
    ): ResponseEntity<HttpErrorContent> {
        return toUnexpectedBattleStatusResponse(ex.battleId, ex.currentStatus, ex.expectedStatus)
    }

    @ExceptionHandler(value = [PlayerNotFoundException::class])
    protected fun handlePlayerNotFound(
        ex: PlayerNotFoundException,
        request: WebRequest
    ): ResponseEntity<HttpErrorContent> {
        return toPlayerNotFoundResponse("${ex.playerId}")
    }
}