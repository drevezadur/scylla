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

package io.drevezerezh.scylla.advanced.domain.impl.usecase

import io.drevezerezh.scylla.advanced.domain.api.battle.Battle
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleUpdate
import io.drevezerezh.scylla.advanced.domain.api.battle.IllegalBattleStatusException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipDeployment
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetUpdate
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.ship.*
import io.drevezerezh.scylla.advanced.domain.impl.*
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.BattleStatus
import io.drevezerezh.scylla.advanced.lang.FleetStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DeployShipUseCaseBean(
    private val playerManager: PlayerManager,
    private val battleManager: BattleManager,
    private val fleetManager: FleetManager,
    private val shipManager: ShipManager
) : DeployShipUseCase {

    private val logger: Logger = LoggerFactory.getLogger(DeployShipUseCaseBean::class.java)

    private lateinit var deployment: ShipDeployment
    private lateinit var battle: Battle
    private lateinit var ship: Ship

    override fun deployShip(deployment: ShipDeployment) {
        initialize(deployment)

        loadBattle()
        checkBattleInDeployment()

        checkPlayerExist()

        createShip()
        checkShipNotAlreadyDeployed()
        checkShipNotOverlapping()
        saveShip()

        markBattleAsFightingIfNecessary()
    }


    private fun initialize(deployment: ShipDeployment) {
        this.deployment = deployment
    }


    private fun loadBattle() {
        battle = battleManager.getBattleById(deployment.battleId)
    }


    private fun checkBattleInDeployment() {
        if (battle.status != BattleStatus.DEPLOYMENT) {
            val message = "Cannot deploy a ship when status is not ${BattleStatus.DEPLOYMENT}"
            logger.error(message)
            throw IllegalBattleStatusException(battle.id, BattleStatus.DEPLOYMENT, battle.status, message)
        }
    }


    private fun checkPlayerExist() {
        val playerId = battle.getPlayerId(deployment.player)
        if (!playerManager.containsPlayer(playerId)) {
            throw PlayerNotFoundException(playerId, "Cannot found player with id $playerId")
        }
    }


    private fun createShip() {
        try {
            ship = Ship(
                battle.id,
                deployment.player,
                deployment.shipType,
                deployment.location,
                deployment.orientation
            )
        } catch (ex: OutOfGridException) {
            logger.error("Ship is out of grid")
            throw ShipOutOfGridException(deployment.shipType, deployment.location, deployment.orientation)
        }
    }


    private fun checkShipNotAlreadyDeployed() {
        if (shipManager.containsShip(ship.id)) {
            val message =
                "Ship is already deployed. BattleId: ${ship.battleId}, player: ${ship.player}, ship: ${ship.type}"
            logger.error(message)
            throw ShipAlreadyDeployedException(ship.id, message)
        }
    }


    private fun checkShipNotOverlapping() {
        for (fleetShip in shipManager.getFleetShips(ship.fleetId)) {
            if (ship.intersect(fleetShip)) {
                val message = "Ship is overlapping another ship. ${ship.id} is overlapping ${fleetShip.id}."
                logger.error(message)
                throw ShipOverlapException(ship.type, fleetShip.type, message)
            }
        }
    }


    private fun saveShip() {
        shipManager.create(ship)
    }


    private fun markBattleAsFightingIfNecessary() {
        val firstFleetDeployed = markFleetDeployedIfNecessary(BattlePlayer.FIRST)
        val secondFleetDeployed = markFleetDeployedIfNecessary(BattlePlayer.SECOND)

        if (firstFleetDeployed && secondFleetDeployed) {
            val update = BattleUpdate().status(BattleStatus.FIGHTING)
            battleManager.update(battle.id, update)
        }
    }


    private fun markFleetDeployedIfNecessary(player: BattlePlayer): Boolean {
        val fleetId = battle.getFleetId(player)
        val fleetDeployed = shipManager.isFleetDeployed(fleetId)
        if (fleetDeployed) {
            markFleetDeployedIfNecessary(fleetId)
        }
        return fleetDeployed
    }


    private fun markFleetDeployedIfNecessary(fleetId: FleetId) {
        val fleet = fleetManager.getFleet(fleetId)
        if (fleet.status == FleetStatus.NOT_DEPLOYED) {
            val update = FleetUpdate().status(FleetStatus.OPERATIONAL)
            val updatedFleet = update.updateFleet(fleet)
            fleetManager.updateFleet(updatedFleet)
        }
    }
}