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

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hello")
class HelloRestService {

    @GetMapping(produces = ["text/plain"])
    fun getHello(): String {
        return "Hello, world !"
    }

    @GetMapping(produces = ["application/xml"])
    fun getHelloXml(): String {
        return "<xml>Hello, world !</xml>"
    }

    @GetMapping(produces = ["application/json"])
    fun getHelloJson(): String {
        return """
            {
                "message" : "Hello, world !"
            }
        """.trimIndent()
    }
}