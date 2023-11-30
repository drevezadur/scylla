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
import io.drevezadur.scylla.teacher.client.command.impl.service.ShotService
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.service.model.LocationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.ShotReportPojo
import io.drevezadur.scylla.teacher.utils.json.HttpJsonHelper.createWriteHttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import java.util.*

class HttpShotServiceImpl(
    private val restTemplate: RestTemplate,
    rootUrl: String
) : ShotService {

    private val url = "$rootUrl/battles"

    override fun shotFromFleetAt(battleId: UUID, playerId: UUID, location: Location): ShotReportPojo {
        val body = LocationPojo(location.x, location.y)
        val header = createWriteHttpEntity(body)
        val response = restTemplate.exchange(
            "$url/$battleId/players/$playerId/fleet/shot",
            HttpMethod.POST,
            header,
            String::class.java
        )

        if (response.statusCode != HttpStatus.OK)
            throw GameCommandException("Fail to shot $battleId $playerId '$body' : ${response.statusCode} ${response.body}")

        return jacksonObjectMapper().readValue(response.body!!, ShotReportPojo::class.java)
    }
}