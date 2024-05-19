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

import io.drevezerezh.scylla.advanced.domain.api.battle.BattleEndedException
import io.drevezerezh.scylla.advanced.domain.api.player.InvalidPlayerAttributeException
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerAlreadyExistException
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(PlayerAlreadyExistException::class)
    fun handlePlayerAlreadyExist(ex: PlayerAlreadyExistException): ResponseEntity<ProblemDetail> {
        return ProblemResponseBuilder(HttpStatus.BAD_REQUEST)
            .title("Player with id '${ex.playerId}' already exists")
            .detail(ex)
            .type("/errors/already-exist")
            .instance("/players/${ex.playerId}")
            .timestamp()
            .attributes(ex.attributeNames)
            .build()
    }

    @ExceptionHandler(InvalidPlayerAttributeException::class)
    fun handleInvalidPlayerAttribute(ex: InvalidPlayerAttributeException): ResponseEntity<ProblemDetail> {
        return ProblemResponseBuilder(HttpStatus.BAD_REQUEST)
            .title("Illegal attributes for player with id '${ex.playerId}'")
            .detail(ex)
            .type("/errors/invalid-attribute")
            .instance("/players/${ex.playerId}")
            .timestamp()
            .attributes(ex.attributeNames)
            .build()
    }

    @ExceptionHandler(PlayerNotFoundException::class)
    fun handleUnknownPlayer(ex: PlayerNotFoundException): ResponseEntity<ProblemDetail> {
        return ProblemResponseBuilder(HttpStatus.NOT_FOUND)
            .title("Cannot found player with id '${ex.playerId}'")
            .detail(ex)
            .type("/errors/not-found")
            .instance("/players/${ex.playerId}")
            .timestamp()
            .build()
    }

    @ExceptionHandler(InvalidPathVariableException::class)
    fun handleInvalidPathVariable(ex: InvalidPathVariableException): ResponseEntity<ProblemDetail> {
        return ProblemResponseBuilder(HttpStatus.NOT_FOUND)
            .title(ex.title)
            .detail(ex)
            .type("/errors/not-found")
            .instance(ex.instancePath)
            .timestamp()
            .build()
    }


    @ExceptionHandler(BattleEndedException::class)
    fun handleBattleEnded(ex: BattleEndedException): ResponseEntity<ProblemDetail>  {
        return ProblemResponseBuilder(HttpStatus.NOT_FOUND)
            .title("Battle with id '${ex.battleId}' is already ended")
            .detail(ex)
            .type("/errors/not-found")
            .instance("/battles/${ex.battleId}")
            .timestamp()
            .build()
    }


    @ExceptionHandler(RestProblemException::class)
    fun handleRestProblem(ex: RestProblemException): ResponseEntity<ProblemDetail> {
        return ProblemResponseBuilder(ex.httpCode)
            .title(ex.title)
            .detail(ex.cause)
            .type(ex.type)
            .instance(ex.instance)
            .properties(ex.properties)
            .timestamp()
            .build()
    }


    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(ex: Exception): ResponseEntity<ProblemDetail> {
        val responseStatusAnnotation = ex.javaClass.getAnnotation(ResponseStatus::class.java)
        return responseStatusAnnotation?.run { toUnexpectedErrorResponse(code, reason) }
            ?: toUnexpectedErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, DEFAULT_MESSAGE)
    }


    companion object {
        private const val DEFAULT_MESSAGE: String = "Oups, an unexpected problem has been detected"

        private fun toUnexpectedErrorResponse(
            httpStatusCode: HttpStatus,
            reason: String?
        ): ResponseEntity<ProblemDetail> {
            return toUnexpectedErrorResponse(httpStatusCode.value(), reason)
        }

        private fun toUnexpectedErrorResponse(httpStatusCode: Int, reason: String?): ResponseEntity<ProblemDetail> {
            val problemDetail = ProblemDetail.forStatus(httpStatusCode)
            problemDetail.title = "Unexpected error"
            problemDetail.detail = reason ?: DEFAULT_MESSAGE
            return toResponse(problemDetail)
        }

        private fun toResponse(problemDetail: ProblemDetail): ResponseEntity<ProblemDetail> {
            return ResponseEntity.status(problemDetail.status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail)
        }
    }
}