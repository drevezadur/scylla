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

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.drevezadur.scylla.teacher.client.command.GameCommandException
import io.drevezadur.scylla.teacher.client.command.impl.service.ShipService
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.service.model.ShipDeploymentBody
import io.drevezadur.scylla.teacher.restserver.service.model.ShipPojo
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import java.util.*

class HttpShipServiceImpl(
    private val restTemplate: RestTemplate,
    rootUrl: String
) : ShipService {

    private val url = "$rootUrl/battles"


    override fun deploy(battleId: UUID, playerId: UUID, content: ShipDeploymentBody) {

        val header = HttpJsonHelper.createWriteHttpEntity(content)
        val fullUrl = "$url/$battleId/players/$playerId/fleet/ships"
        val response = restTemplate.exchange(fullUrl, HttpMethod.POST, header, String::class.java)

        if (response.statusCode != HttpStatus.CREATED)
            throw GameCommandException("Fail to create ship '$content' : ${response.statusCode} ${response.body}")

        val location = response.headers["Location"]!![0]
        val expectedDeploymentUrl = "/battles/$battleId/players/$playerId/fleet/ships/${content.type}"
        if (!location.equals(expectedDeploymentUrl))
            throw GameCommandException("Fail to deploy ship $expectedDeploymentUrl. Actual=$location")
    }

    override fun getAllInFleet(battleId: UUID, playerId: UUID): List<ShipPojo> {
        val header = HttpJsonHelper.createReadHttpEntity()

        val response = restTemplate.exchange(
            "$url/$battleId/players/$playerId/fleet/ships",
            HttpMethod.GET,
            header,
            String::class.java
        )

        if (response.statusCode != HttpStatus.OK)
            throw RuntimeException("Fail to get all ships of player $playerId in battle $battleId")

        val paramTypeReference: TypeReference<List<ShipPojo>> = object : TypeReference<List<ShipPojo>>() {}
        return jacksonObjectMapper().readValue(response.body!!, paramTypeReference)
        //return objectMapper.readValue(response.body!!, paramTypeReference)
    }

    override fun findById(battleId: UUID, playerId: UUID, type: ShipType): ShipPojo? {
        val header = HttpJsonHelper.createReadHttpEntity()

        val paramTypeReference: ParameterizedTypeReference<ShipPojo> =
            object : ParameterizedTypeReference<ShipPojo>() {}
        val response = restTemplate.exchange(
            "$url/$battleId/players/$playerId/fleet/ships/$type",
            HttpMethod.GET,
            header,
            paramTypeReference
        )

        return if (response.statusCode == HttpStatus.OK)
            response.body!!
        else
            null
    }
}