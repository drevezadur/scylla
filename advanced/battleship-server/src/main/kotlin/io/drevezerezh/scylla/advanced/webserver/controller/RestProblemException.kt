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

class RestProblemException(
    val httpCode: Int,
    val title: String,
    val type: String,
    val instance: String,
    val properties: Map<String, String>? = null,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause) {
    constructor(
        status: HttpStatus,
        title: String,
        type: String,
        instance: String,
        properties: Map<String, String>? = null,
        message: String? = null,
        cause: Throwable? = null
    ) : this(status.value(), title, type, instance, properties, message, cause)
}