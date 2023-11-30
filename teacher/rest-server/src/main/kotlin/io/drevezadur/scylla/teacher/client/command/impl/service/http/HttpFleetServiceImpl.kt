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
import io.drevezadur.scylla.teacher.client.command.impl.service.FleetService
import io.drevezadur.scylla.teacher.restserver.service.model.FleetPojo
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper.createReadHttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import java.util.*

class HttpFleetServiceImpl(
    private val restTemplate: RestTemplate,
    rootUrl: String
) : FleetService {

    private val url = "$rootUrl/battles"

    override fun findById(battleId: UUID, playerId: UUID): FleetPojo? {
        val header = createReadHttpEntity()

        val response = restTemplate.exchange(
            "$url/$battleId/players/$playerId/fleet/",
            HttpMethod.GET,
            header,
            String::class.java
        )

        return if (response.statusCode == HttpStatus.OK)
            jacksonObjectMapper().readValue(response.body!!, FleetPojo::class.java)
        else
            null
    }
}