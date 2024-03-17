package io.drevezerezh.scylla.advanced.webserver.controller

import io.drevezerezh.scylla.advanced.domain.api.InvalidPlayerAttributeException
import io.drevezerezh.scylla.advanced.domain.api.PlayerAlreadyExistException
import io.drevezerezh.scylla.advanced.domain.api.PlayerNotFoundException
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

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(ex: Exception): ResponseEntity<ProblemDetail> {
        val responseStatusAnnotation = ex.javaClass.getAnnotation(
            ResponseStatus::class.java
        )
        if (responseStatusAnnotation != null) {
            return toUnexpectedErrorResponse(
                responseStatusAnnotation.code,
                responseStatusAnnotation.reason
            )
        }
        return toUnexpectedErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            DEFAULT_MESSAGE
        )
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