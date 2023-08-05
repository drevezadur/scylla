/*
 * Copyright (c) 2023. gofannon.io
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

package io.gofannon.scylla.homework.domain

import io.gofannon.scylla.homework.lang.GameStatus
import io.gofannon.scylla.homework.lang.Player

/**
 * Game is the root access to all services and information related to the game
 */
interface Game {

    /**
     * Get the game status
     *
     * The actions located in the services depends on this status.
     * For instance, a player cannot fire when the game is over.
     *
     * @return the status of the game
     */
    fun getStatus(): GameStatus

    /**
     * Get the winner of the battle
     * @return the winner, null when the game is not over
     */
    fun getWinner(): Player?

    /**
     * Get the loser of the battle
     * @return the loser, null when the game is not over
     */
    fun getLoser(): Player?

    /**
     * Get the board of a player from its id
     * @param player the identifier of the player
     * @return the board of the requested player
     */
    fun getPlayerBoard(player: Player): PlayerBoard

     /**
     * Get the number of the turn
     * @return an incremental number
     */
    fun getTurnNumber(): Int

}