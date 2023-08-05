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

package io.gofannon.scylla.homework.domain.internal

import io.gofannon.scylla.homework.domain.Game

/**
 * A game provider with internal scope
 */
class InternalGameProvider {

    private val shipFactory: ShipFactory = DefaultShipFactory()
    private val fleetBoardFactory = DefaultPlayerBoardFactory(shipFactory)
    private val gameFactory = DefaultGameFactory(fleetBoardFactory)
    private var game: Game = gameFactory.createGame()

    /**
     * Get the current game
     * @return the current game
     */
    fun get(): Game {
        return game
    }

    /**
     * Reset the game by creating a new [Game] instance
     * @return the new game
     */
    fun reset(): Game {
        game = gameFactory.createGame()
        return game
    }
}