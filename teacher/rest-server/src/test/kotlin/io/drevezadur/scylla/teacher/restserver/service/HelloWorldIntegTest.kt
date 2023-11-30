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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import java.util.*


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HelloWorldIntegTest {

    @Value(value = "\${local.server.port}")
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    lateinit var url: String

    @BeforeEach
    fun setUp() {
        url = "http://localhost:$port/hello"
    }


    @Test
    @Throws(Exception::class)
    fun helloShouldReturnDefaultMessage() {
        assertThat(
            restTemplate.getForObject("http://localhost:$port/hello", String::class.java)
        ).isEqualTo("Hello, world !")
    }

    @Test
    @Throws(Exception::class)
    fun helloShouldReturnXmlMessage() {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_XML)
        val entity = HttpEntity<String>(headers)

        val response = restTemplate.exchange(url, HttpMethod.GET, entity, String::class.java)

        assertThat(
            response.statusCode
        ).isEqualTo(HttpStatus.OK)

        assertThat(
            response.body!!
        ).isEqualTo("<xml>Hello, world !</xml>")
    }

    @Test
    @Throws(Exception::class)
    fun helloShouldReturnJsonMessage() {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val entity = HttpEntity<String>(headers)

        val response = restTemplate.exchange(url, HttpMethod.GET, entity, String::class.java)

        assertThat(
            response.statusCode
        ).isEqualTo(HttpStatus.OK)

        assertThat(
            response.body!!
        ).contains("\"message\"").contains("\"Hello, world !\"")
    }
}