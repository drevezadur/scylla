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

import io.drevezadur.scylla.teacher.restserver.service.model.BattleCreationPojo
import io.drevezadur.scylla.teacher.restserver.service.model.BattlePojo
import java.util.*

interface BattleService {

    fun getAll(): List<BattlePojo>

    fun findById(battleId: UUID): BattlePojo?

    fun create(body: BattleCreationPojo): UUID

    fun delete(battleId: UUID)
}