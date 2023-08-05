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

import io.gofannon.scylla.homework.lang.Location
import io.gofannon.scylla.homework.lang.Player
import io.gofannon.scylla.homework.lang.ShotResult

/**
 * Internal game manager.
 * Used to delegate some actions to the Game and to inform it about some changes a player board
 */
interface GameManager {

    /**
     * Shot the opponent player fleet
     * @param shooter the shooting player
     * @param at the shooting location
     * @return the result of the shot
     */
    fun shot(shooter: Player, at: Location): ShotResult

    /**
     * Informs that all ships of the player are deployed
     * @param player the player whose fleet is deployed
     */
    fun fleetDeployed(player: Player)
}