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

import io.gofannon.scylla.homework.lang.*

/**
 * The board of a player provides information and actions to play the game
 */
interface PlayerBoard {

    /**
     * The identifier of the player which the board is related to
     */
    val player: Player

    /**
     * Get the status of the current player
     * @return the situation of the player
     */
    fun getPlayerState(): PlayerState

    /**
     * Deploy a ship
     * @param type the type of the ship
     * @param from the origin location of the ship
     * @param orientation the orientation of the ship on the grid
     */
    fun deployShip(type: ShipType, from: Location, orientation: GridOrientation)

    /**
     * Get all the types of the ship not deployed
     * @return a set containing the ship types which are not deployed
     */
    fun getNotDeployedShipTypes(): Set<ShipType>


    /**
     * Get all the ships currently deployed
     */
    fun getShips(): List<Ship>

    fun getFleetStatus(): FleetStatus

    fun fireAt(location: Location): ShotResult

    fun getShip(type: ShipType): Ship
}