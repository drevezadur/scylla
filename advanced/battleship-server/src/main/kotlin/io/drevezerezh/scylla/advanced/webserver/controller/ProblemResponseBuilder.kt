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

    fun detail(ex: Throwable?): ProblemResponseBuilder {
        if (ex != null)
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


    fun properties(properties: Map<String, String>?): ProblemResponseBuilder {
        if( properties!=null) {
            problemDetail.properties = properties.entries.associate { Pair(it.key, it.value) }
        }
        return this
    }

    fun attributes(vararg names: String): ProblemResponseBuilder {
        problemDetail.setProperty("attributes", names.toSet())
        return this
    }

    fun attributes(names: Set<String>): ProblemResponseBuilder {
        problemDetail.setProperty("attributes", names)
        return this
    }

    fun build(): ResponseEntity<ProblemDetail> {
        return ResponseEntity.status(problemDetail.status)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(problemDetail)
    }
}