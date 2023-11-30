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

package io.drevezadur.scylla.teacher.restserver.domain.store

import io.drevezadur.scylla.teacher.restserver.domain.model.BattleCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleDEntity
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID

interface BattleStore {

    /**
     * Create a new battle
     * @param creation the input information required for creation
     * @return the created entity
     */
    fun create(creation: BattleCreation): BattleDEntity

    /**
     * Find a battle from its identifier
     * @param id the identifier ofr the battle
     * @return the
     */
    fun findById(id: BattleUUID): BattleDEntity?

    fun getById(id: BattleUUID): BattleDEntity

    fun getAll(): List<BattleDEntity>

    fun delete(id: BattleUUID)

    fun save(battle: BattleDEntity)
}