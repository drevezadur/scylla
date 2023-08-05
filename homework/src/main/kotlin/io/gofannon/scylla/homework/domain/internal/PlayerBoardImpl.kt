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

import io.gofannon.scylla.homework.domain.Ship
import io.gofannon.scylla.homework.lang.*
import io.gofannon.scylla.homework.lang.ShotResult.MISSED

internal class PlayerBoardImpl(
    override val player: Player,
    private val shipFactory: ShipFactory,
    private val gameManager: GameManager
) : MutablePlayerBoard {

    private var playerState: PlayerState = PlayerState.DEPLOYING_FLEET
    private val ships = ArrayList<MutableShip>()
    private var fleetStatus: FleetStatus = FleetStatus.NOT_DEPLOYED

    override fun getPlayerState(): PlayerState = playerState

    override fun deployShip(type: ShipType, from: Location, orientation: GridOrientation) {
        checkNotDeployed(type)

        val ship = shipFactory.createShip(type, from, orientation)
        checkNotOverrideAnotherShip(ship)
        ships.add(ship)

        if (ships.size == ShipType.entries.size) {
            playerState = PlayerState.FLEET_DEPLOYED
            refreshFleetStatus()
            gameManager.fleetDeployed(player)
        }
    }


    private fun checkNotDeployed(type: ShipType) {
        if (ships.map { it.type }.contains(type))
            throw IllegalArgumentException("Ship of type $type is already deployed")
    }

    private fun checkNotOverrideAnotherShip(ship: Ship) {
        if (ships.any(ship::intersects))
            throw IllegalArgumentException("Ship is overriding another ship")
    }

    override fun getNotDeployedShipTypes(): Set<ShipType> {
        val deployedTypes = ships.map { it.type }.toSet()
        val allTypes = ShipType.entries
        return allTypes.minus(deployedTypes).toSet()
    }

    override fun getShips(): List<Ship> = ships

    override fun getShip(type: ShipType): Ship {
        return ships.firstOrNull { it.type == type }
            ?: throw IllegalArgumentException("Ship $type is not deployed")
    }

    override fun fireAt(location: Location): ShotResult {
        if (playerState != PlayerState.FLEET_DEPLOYED && playerState != PlayerState.FIGHTING)
            throw IllegalStateException("Player state shall be in FLEET_DEPLOYED or FIGHTING")
        return gameManager.shot(player, location)
    }

    override fun setPlayerState(state : PlayerState) {
        this.playerState = state
    }

    override fun resolveShot(at: Location): ShotResult {
        val ship = findShipAt(at)
            ?: return MISSED

        val shotResult = ship.hitAt(at)

        if (shotResult.structuralChange)
            refreshFleetStatus()

        return shotResult
    }

    private fun findShipAt(at: Location): MutableShip? {
        return ships.firstOrNull { it.contains(at) }
    }

    private fun refreshFleetStatus() {
        fleetStatus = computeFleetStatus(ships)
    }

    override fun getFleetStatus(): FleetStatus = fleetStatus


    override fun startFighting() {
        if (playerState != PlayerState.FLEET_DEPLOYED)
            throw IllegalStateException("Game fleet situation is $playerState instead of DEPLOYED")

        playerState = PlayerState.FIGHTING
    }

    companion object {
        /**
         * Compute the status of a fleet
         * @param ships the ships composing the fleet
         * @return the status of the fleet
         */
        fun computeFleetStatus(ships: List<Ship>): FleetStatus {
            if (ships.size != ShipType.entries.size)
                return FleetStatus.NOT_DEPLOYED

            val allStatus = ships.map { it.status }.toSet()
            if (allStatus.size > 1)
                return FleetStatus.DAMAGED

            return when (allStatus.first()) {
                ShipStructuralStatus.UNHARMED -> FleetStatus.UNHARMED
                ShipStructuralStatus.DESTROYED -> FleetStatus.SUNK
                else -> FleetStatus.DAMAGED
            }
        }
    }
}