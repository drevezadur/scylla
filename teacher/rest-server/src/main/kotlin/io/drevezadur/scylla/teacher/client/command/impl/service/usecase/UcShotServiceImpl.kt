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

package io.drevezadur.scylla.teacher.client.command.impl.service.usecase

import io.drevezadur.scylla.teacher.client.command.GameCommandException
import io.drevezadur.scylla.teacher.client.command.impl.service.ShotService
import io.drevezadur.scylla.teacher.restserver.domain.usecase.ShotUseCaseManager
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.service.model.ShotReportMapping
import io.drevezadur.scylla.teacher.restserver.service.model.ShotReportPojo
import java.util.*

class UcShotServiceImpl(
    private val shotUseCaseManager: ShotUseCaseManager
) : ShotService {


    override fun shotFromFleetAt(battleId: UUID, playerId: UUID, location: Location): ShotReportPojo {
        try {
            val shotReport = shotUseCaseManager.shotAt(battleId, playerId, location)
            return ShotReportMapping.toPojo(shotReport)
        } catch (ex: RuntimeException) {
            throw GameCommandException("Fail to shot $battleId $playerId $location")
        }
    }
}