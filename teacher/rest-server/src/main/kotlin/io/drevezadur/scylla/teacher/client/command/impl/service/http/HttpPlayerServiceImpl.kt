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

package io.drevezadur.scylla.teacher.client.command.impl.service.http

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.drevezadur.scylla.teacher.client.command.GameCommandException
import io.drevezadur.scylla.teacher.client.command.impl.service.PlayerService
import io.drevezadur.scylla.teacher.restserver.service.model.PlayerCreationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.PlayerPojo
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper.createReadHttpEntity
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper.createWriteHttpEntity
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import java.util.*

class HttpPlayerServiceImpl(
    private val restTemplate: RestTemplate,
    rootUrl: String
) : PlayerService {

    private val url = "$rootUrl/players"

    override fun create(name: String): UUID {
        val pojo = PlayerCreationPojo(name)

        val header = createWriteHttpEntity(pojo)
        val response = restTemplate.exchange(url, HttpMethod.POST, header, String::class.java)

        if (response.statusCode != HttpStatus.CREATED)
            throw GameCommandException("Fail to create user '$name'")

        val location = response.headers["Location"]!![0]
        val uuidRaw = location.substring("/players/".length)
        return UUID.fromString(uuidRaw)
    }


    override fun delete(id: UUID) {
        val header = createReadHttpEntity()

        val response = restTemplate.exchange("$url/$id", HttpMethod.DELETE, header, String::class.java)
        if (response.statusCode != HttpStatus.NO_CONTENT)
            throw GameCommandException("Fail to delete user '$id'")
    }


    override fun getAll(): List<PlayerPojo> {
        val header = createReadHttpEntity()

        val paramTypeReference: ParameterizedTypeReference<List<PlayerPojo>> =
            object : ParameterizedTypeReference<List<PlayerPojo>>() {}
        val response = restTemplate.exchange(url, HttpMethod.GET, header, paramTypeReference)

        if (response.statusCode != HttpStatus.OK)
            throw GameCommandException("Fail to get all users")

        return response.body!!
    }

    override fun findById(id: UUID): PlayerPojo? {
        val header = createReadHttpEntity()

        val response = restTemplate.exchange("$url/${id}", HttpMethod.GET, header, String::class.java)

        return if (response.statusCode == HttpStatus.OK)
            jacksonObjectMapper().readValue(response.body!!, PlayerPojo::class.java)
        else
            null
    }
}