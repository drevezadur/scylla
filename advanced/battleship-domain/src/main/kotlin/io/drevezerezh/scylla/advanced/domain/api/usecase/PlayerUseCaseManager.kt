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

package io.drevezerezh.scylla.advanced.domain.api.usecase

import io.drevezerezh.scylla.advanced.domain.api.player.*

/**
 * Set of operations to related to the management of players
 */
interface PlayerUseCaseManager {

    /**
     * Create a new player from its name
     * @param creation the player creation information
     * @return the created player
     * @throws PlayerAlreadyExistException when a player with such name exists
     * @throws InvalidPlayerAttributeException when the name argument is invalid
     */
    @Throws(PlayerAlreadyExistException::class, InvalidPlayerAttributeException::class)
    fun create(creation: PlayerCreation): Player

    /**
     * Update an existing player
     * @param playerId the id of the player to update
     * @param update the attributes to update
     * @return the updated player
     * @throws PlayerAlreadyExistException when a player with such name exists
     * @throws InvalidPlayerAttributeException when the name argument is invalid
     */
    @Throws(PlayerAlreadyExistException::class, InvalidPlayerAttributeException::class)
    fun update(playerId: String, update: PlayerUpdate): Player

    /**
     * Delete a player
     * @param playerId the player identifier
     * @return true when the player has been deleted, false otherwise
     */
    fun delete(playerId: String): Boolean

    /**
     * Get all players
     * @return a list of the existing players
     */
    fun getAll(): List<Player>

    /**
     * Get a player from its identifier
     * @param playerId the identifier of the player
     * @return the player
     * @throws PlayerNotFoundException if there is no player associated to the identifier
     */
    @Throws(PlayerNotFoundException::class)
    fun getById(playerId: String): Player
}