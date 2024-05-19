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

import io.drevezerezh.scylla.advanced.domain.api.battle.*
import io.drevezerezh.scylla.advanced.domain.api.fleet.Fleet
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipAlreadyDeployedException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipDeployment
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipOutOfGridException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipOverlapException
import io.drevezerezh.scylla.advanced.domain.api.shot.Shot
import io.drevezerezh.scylla.advanced.domain.api.shot.ShotReport

/**
 * Use cases release to battle management
 */
interface BattleUseCaseManager {

    /**
     * Create a new battle
     * @param creation the information required to create a new battle
     * @return the newly created battle
     * @throws PlayerNotFoundException when a player cannot be found
     */
    @Throws(PlayerNotFoundException::class)
    fun create(creation: BattleCreation): Battle

    /**
     * Get a battle from its unique identifier
     * @param battleId the battle unique identifier
     * @return the battle
     */
    @Throws(BattleNotFoundException::class)
    fun getById(battleId: String): Battle

    /**
     * Delete a battle from its unique identifier
     * @param battleId  the battle unique identifier
     * @return true when the battle has been deleted, false when the battle does not exist
     */
    fun delete(battleId: String): Boolean

    /**
     * Get all battles
     * @return a list of battles
     */
    fun getAll(): List<Battle>


    /**
     * Deploy a new ship
     *
     * @throws ShipAlreadyDeployedException when the ship is already deployed
     * @throws ShipOutOfGridException when the ship out of grid
     * @throws ShipOverlapException when the ship is overlapping another ship
     * @throws BattleEndedException when battle is already ended
     */
    @Throws(
        ShipAlreadyDeployedException::class,
        ShipOutOfGridException::class,
        ShipOverlapException::class,
        BattleEndedException::class
    )
    fun deployShip(deployment: ShipDeployment)

    /**
     * Shot to opponent fleet
     * @param shot the information to target the opponent fleet
     * @return a report about the shot
     * @throws BattleNotFoundException when battle identifier is not registered
     * @throws BattleEndedException when battle is already ended
     * @throws NotPlayerTurnException when it is not the turn of the shooting player
     */
    @Throws(BattleNotFoundException::class, BattleEndedException::class, NotPlayerTurnException::class)
    fun shot(shot: Shot): ShotReport

    /**
     * Get a fleet from its battle identifier and the player
     * @param fleetId the fleet identifier
     * @return the player's fleet
     * @throws BattleNotFoundException when the battle does not exist
     */
    @Throws(BattleNotFoundException::class)
    fun getFleet(fleetId: FleetId): Fleet

    /**
     * End a battle
     *
     * Does nothing when battle is already finished
     * @return true when the battle has been finished, false when the battle was already finished
     */
    @Throws(BattleNotFoundException::class, BattleEndedException::class)
    fun endBattle(battleId: String): Boolean
}