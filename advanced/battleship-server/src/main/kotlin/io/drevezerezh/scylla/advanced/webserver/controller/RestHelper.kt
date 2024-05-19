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

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

/**
 * Set of utilities methods related tp Set
 */
object RestHelper {

    /**
     * Create an HTTP creation response, ie: 201 + empty body and header containing location
     * @param request the initial request
     * @param  path the relative path to the created entity
     * @return a resource creation response
     */
    fun createCreationResponse(request: HttpServletRequest, path: String): ResponseEntity<Void> {
        val uri = toCreationUri(request, path)
        return ResponseEntity.created(uri).build()
    }

    private fun toCreationUri(request: HttpServletRequest, path: String): URI {
        return ServletUriComponentsBuilder.fromRequest(request)
            .path(path)
            .build()
            .toUri()
    }
}