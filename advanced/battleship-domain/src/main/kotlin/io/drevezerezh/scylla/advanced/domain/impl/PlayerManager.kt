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

package io.drevezerezh.scylla.advanced.domain.impl

import io.drevezerezh.scylla.advanced.domain.api.player.*

interface PlayerManager {

    /**
     * Create a new player
     * @param playerCreation the information for creating the player
     * @return the created player
     * @throws PlayerAlreadyExistException if the name of the player already exist
     * @throws InvalidPlayerAttributeException if the name attribute isinvalid
     */
    @Throws(PlayerAlreadyExistException::class, InvalidPlayerAttributeException::class)
    fun createPlayer(playerCreation: PlayerCreation): Player

    /**
     * Check if a player exists or not
     * @param playerId the player identifier
     * @return true if a player with such id exists, false otherwise
     */
    fun containsPlayer(playerId: String): Boolean

    /**
     * Get a player from its identifier
     * @param playerId the identifier of the player
     * @return the player
     * @throws PlayerNotFoundException when the id does not match an existing player
     */
    @Throws(PlayerNotFoundException::class)
    fun getPlayerById(playerId: String): Player

    /**
     * Get all the players
     * @return all the players
     */
    fun getAllPlayers(): List<Player>

    /**
     * Update a player
     * @param id the id of the player to update
     * @param update the updates to apply on the player
     * @return the updated player
     * @throws PlayerNotFoundException when the player id does not match an existing player
     * @throws InvalidPlayerAttributeException when an attribute of the update structure is invalid
     */
    @Throws(PlayerNotFoundException::class, InvalidPlayerAttributeException::class)
    fun update(id: String, update: PlayerUpdate): Player

    /**
     * Delete a player by its identifier
     *
     * Do nothing when players does not exist.
     *
     * @param playerId the id of the player to delete
     * @return true when the player was existing, false otherwise
     */
    fun deletePlayer(playerId: String): Boolean
}