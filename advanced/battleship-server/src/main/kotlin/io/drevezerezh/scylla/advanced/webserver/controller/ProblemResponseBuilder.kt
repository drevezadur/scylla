package io.drevezerezh.scylla.advanced.webserver.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.util.ResourceUtils.toURI
import java.time.Instant

class ProblemResponseBuilder(
    httpStatusCode: Int
) {

    private val problemDetail = ProblemDetail.forStatus(httpStatusCode)

    constructor(httpStatus: HttpStatus) : this(httpStatus.value())

    fun title(title: String): ProblemResponseBuilder {
        problemDetail.title = title
        return this
    }

    fun detail(ex: Exception): ProblemResponseBuilder {
        problemDetail.detail = ex.message
        return this
    }

    fun type(uri: String): ProblemResponseBuilder {
        problemDetail.type = toURI(uri)
        return this
    }

    fun instance(instance: String): ProblemResponseBuilder {
        problemDetail.instance = toURI(instance)
        return this
    }

    fun timestamp(timestamp: Instant): ProblemResponseBuilder {
        problemDetail.setProperty("timestamp", timestamp)
        return this
    }

    fun timestamp(): ProblemResponseBuilder = timestamp(Instant.now())

    fun attributes(vararg names: String): ProblemResponseBuilder {
        problemDetail.setProperty("attributes", names.toSet())
        return this
    }

    fun attributes( names: Set<String>): ProblemResponseBuilder {
        problemDetail.setProperty("attributes", names)
        return this
    }

    fun build(): ResponseEntity<ProblemDetail> {
        return ResponseEntity.status(problemDetail.status)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problemDetail)
    }
}