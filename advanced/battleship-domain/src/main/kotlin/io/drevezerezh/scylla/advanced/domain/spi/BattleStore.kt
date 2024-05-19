/*
 * Copyright (c) 2024 gofannon.xyz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.drevezerezh.scylla.advanced.domain.spi

import io.drevezerezh.scylla.advanced.domain.api.battle.Battle
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleNotFoundException

interface BattleStore : ItemStore<String,Battle>{

//    /**
//     * Save a battle into the store
//     * @param battle the battle to store
//     */
//    fun save(battle: Battle)
//
//    /**
//     * Save a list of battles into the store
//     * @param battles the battles to store
//     */
//    fun saveAll(vararg battles: Battle)

//    /**
//     * Delete a battle
//     *
//     * Does nothing when battle does not exist
//     *
//     * @param battleId the identifier of the battle
//     * @return true if the battle was existing, false otherwise
//     */
//    fun deleteById(battleId: String): Boolean
//
//    /**
//     * Delete all stored battles
//     */
//    fun deleteAll()

//    /**
//     * Check if a battle is stored
//     * @param battleId the battle identifier
//     * @return true when the battle is stored, false otherwise
//     */
//    fun contains(battleId: String): Boolean

    /**
     * Get a stored battle from its identifier
     * @param id the identifier of the battle
     * @return the stored battle
     * @throws BattleNotFoundException when the battle is not in the store
     */
    @Throws(BattleNotFoundException::class)
    override fun getById(id: String): Battle

//    /**
//     * Get all battles
//     * @return the list of the battles in the store
//     */
//    fun getAll(): List<Battle>
}