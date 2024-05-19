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

import io.drevezerezh.scylla.advanced.domain.api.player.Player
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerNotFoundException

interface PlayerStore : ItemStore<String, Player> {

    /**
     * Get a stored player from its identifier
     * @param id the player identifier
     * @return the player
     * @throws PlayerNotFoundException when the player is not in the store
     */
    @Throws(PlayerNotFoundException::class)
    override fun getById(id: String): Player

    /**
     * Get a stored player from its name
     * @param name the player name
     * @return the player
     * @throws PlayerNotFoundException when the player is not in the store
     */
    @Throws(PlayerNotFoundException::class)
    fun getByName(name: String): Player

    /**
     * Check if player name exists in store
     * @param name the player name
     * @return true when a player with this name exists, false otherwise
     */
    fun containsName(name: String): Boolean
}