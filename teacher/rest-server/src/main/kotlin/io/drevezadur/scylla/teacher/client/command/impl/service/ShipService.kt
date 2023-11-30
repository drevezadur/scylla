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

package io.drevezadur.scylla.teacher.client.command.impl.service

import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.service.model.ShipDeploymentBody
import io.drevezadur.scylla.teacher.restserver.service.model.ShipPojo
import java.util.*

interface ShipService {

    fun deploy(battleId: UUID, playerId: UUID, content: ShipDeploymentBody)

    fun getAllInFleet(battleId: UUID, playerId: UUID): List<ShipPojo>

    fun findById(battleId: UUID, playerId: UUID, type: ShipType): ShipPojo?

}