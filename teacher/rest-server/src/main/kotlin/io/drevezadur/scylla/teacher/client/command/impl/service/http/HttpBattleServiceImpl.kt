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
import io.drevezadur.scylla.teacher.client.command.impl.service.BattleService
import io.drevezadur.scylla.teacher.restserver.service.model.BattleCreationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.BattlePojo
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper.createReadHttpEntity
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper.createWriteHttpEntity
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import java.util.*

class HttpBattleServiceImpl(
    private val restTemplate: RestTemplate,
    rootUrl: String
) : BattleService {

    private val url = "$rootUrl/battles"


    override fun create(body: BattleCreationPojo): UUID {
        val header = createWriteHttpEntity(body)
        val response = restTemplate.exchange(url, HttpMethod.POST, header, String::class.java)

        if (response.statusCode != HttpStatus.CREATED)
            throw GameCommandException("Fail to create battle '$body' : ${response.statusCode} ${response.body}")

        val location = response.headers["Location"]!![0]
        val uuidRaw = location.substring("/battles/".length)
        return UUID.fromString(uuidRaw)
    }


    override fun delete(battleId: UUID) {
        val header = createReadHttpEntity()

        val response = restTemplate.exchange("$url/$battleId", HttpMethod.DELETE, header, String::class.java)
        if (response.statusCode != HttpStatus.NO_CONTENT)
            throw GameCommandException("Fail to delete battle '$battleId'")
    }


    override fun getAll(): List<BattlePojo> {
        val header = createReadHttpEntity()

        val paramTypeReference: ParameterizedTypeReference<List<BattlePojo>> =
            object : ParameterizedTypeReference<List<BattlePojo>>() {}
        val response = restTemplate.exchange(url, HttpMethod.GET, header, paramTypeReference)

        if (response.statusCode != HttpStatus.OK)
            throw RuntimeException("Fail to get all battles")

        return response.body!!
    }

    override fun findById(battleId: UUID): BattlePojo? {
        val header = createReadHttpEntity()

        val paramTypeReference: ParameterizedTypeReference<String> =
            object : ParameterizedTypeReference<String>() {}
        val response = restTemplate.exchange("$url/${battleId}", HttpMethod.GET, header, paramTypeReference)


        return if (response.statusCode == HttpStatus.OK)
            jacksonObjectMapper().readValue(response.body!!, BattlePojo::class.java)
        else
            null
    }
}