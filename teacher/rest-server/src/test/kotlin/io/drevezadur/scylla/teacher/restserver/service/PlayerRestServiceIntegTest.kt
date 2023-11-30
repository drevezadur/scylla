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

import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.PlayerPEntity
import io.drevezadur.scylla.teacher.restserver.service.model.PlayerCreationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.PlayerPojo
import io.drevezadur.scylla.teacher.restserver.service.util.Attribute
import io.drevezadur.scylla.teacher.restserver.service.util.ResponseHelper
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import java.util.*


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlayerRestServiceIntegTest {

    @Value(value = "\${local.server.port}")
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var playerRepository: PlayerRepository

    lateinit var url: String

    @BeforeEach
    fun setUp() {
        url = "http://localhost:$port/players"
    }

    @AfterEach
    fun tearDown() {
        deleteAllUsers()
    }

    private fun deleteAllUsers() {
        playerRepository.deleteAll()
    }


    @Test
    fun `getAll shall return empty list when no users`() {
        val header = HttpJsonHelper.createReadHttpEntity()

        val paramTypeReference: ParameterizedTypeReference<List<PlayerPojo>> =
            object : ParameterizedTypeReference<List<PlayerPojo>>() {}
        val response = restTemplate.exchange(url, HttpMethod.GET, header, paramTypeReference)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)
        assertThat(response.body)
            .isEmpty()
    }


    @Test
    fun `getAll shall return the list of users`() {
        createPlayers()

        val header = HttpJsonHelper.createReadHttpEntity()

        val paramTypeReference: ParameterizedTypeReference<List<PlayerPojo>> =
            object : ParameterizedTypeReference<List<PlayerPojo>>() {}
        val response = restTemplate.exchange(url, HttpMethod.GET, header, paramTypeReference)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)
        assertThat(response.body)
            .extracting("uuid", "name")
            .contains(
                Tuple(uuid1, "John"),
                Tuple(uuid2, "Jane")
            )
    }

    private fun createPlayers() {
        playerRepository.save(PlayerPEntity(uuid1, "John"))
        playerRepository.save(PlayerPEntity(uuid2, "Jane"))
    }


    @Test
    fun `getById shall return not found when user does not exist`() {
        val header = HttpJsonHelper.createReadHttpEntity()

        val response = restTemplate.exchange("$url/$unknown", HttpMethod.GET, header, String::class.java)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.NOT_FOUND)

        val errorContent = ResponseHelper.toHttpErrorContent(response.body!!)

        assertThat(errorContent.errorCode)
            .isEqualTo("PLAYER_NOT_FOUND")
        assertThat(errorContent.attributes)
            .hasSize(1)
            .containsExactly(
                Attribute("playerId", "$unknown")
            )
    }

    @Test
    fun `getById shall return the user when it exists`() {
        createPlayers()
        val header = HttpJsonHelper.createReadHttpEntity()

        val paramTypeReference: ParameterizedTypeReference<PlayerPojo> =
            object : ParameterizedTypeReference<PlayerPojo>() {}
        val response = restTemplate.exchange("$url/$uuid1", HttpMethod.GET, header, paramTypeReference)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.OK)
        assertThat(response.body)
            .extracting("uuid", "name")
            .containsExactly(uuid1, "John")
    }

    @Test
    fun `create shall create a new user`() {
        val header = createHttpHeader2(PlayerCreationPojo("Walter"))

        val response = restTemplate.exchange(url, HttpMethod.POST, header, String::class.java)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.CREATED)

        val locations = response.headers["Location"]!!
        assertThat(locations[0])
            .startsWith("/players/")
    }

    private fun createHttpHeader2(pojo: PlayerCreationPojo): HttpEntity<PlayerCreationPojo> {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        return HttpEntity<PlayerCreationPojo>(pojo, headers)
    }

    @Test
    fun `delete shall do nothing when user not exists`() {
        val header = HttpJsonHelper.createReadHttpEntity()

        val response = restTemplate.exchange("$url/$unknown", HttpMethod.DELETE, header, String::class.java)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `delete shall delete user when user exists`() {
        createPlayers()
        val header = HttpJsonHelper.createReadHttpEntity()

        val response = restTemplate.exchange("$url/$uuid2", HttpMethod.DELETE, header, String::class.java)

        assertThat(response.statusCode)
            .isEqualTo(HttpStatus.NO_CONTENT)

        assertThat(playerRepository.count())
            .isEqualTo(1)
    }

    companion object {
        val uuid1: UUID = UUID.randomUUID()
        val uuid2: UUID = UUID.randomUUID()
        val unknown: UUID = UUID.randomUUID()
    }
}