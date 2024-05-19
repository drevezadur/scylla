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
import io.drevezerezh.scylla.advanced.domain.api.battle.NotPlayerTurnException
import io.drevezerezh.scylla.advanced.domain.api.fleet.Fleet
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetUpdate
import io.drevezerezh.scylla.advanced.domain.api.ship.Ship
import io.drevezerezh.scylla.advanced.domain.api.shot.Shot
import io.drevezerezh.scylla.advanced.domain.api.shot.ShotReport
import io.drevezerezh.scylla.advanced.lang.ShotResult
import io.drevezerezh.scylla.advanced.domain.impl.*
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.FIRST
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.SECOND
import io.drevezerezh.scylla.advanced.lang.BattleStatus.FIGHTING
import io.drevezerezh.scylla.advanced.lang.BattleStatus.FINISHED
import io.drevezerezh.scylla.advanced.lang.FleetStatus
import io.drevezerezh.scylla.advanced.lang.ShipStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class ShootingUseCaseBean(
    private val battleManager: BattleManager,
    private val fleetManager: FleetManager,
    private val shipManager: ShipManager,
    private val timeProvider: TimeProvider
) : ShootingUseCase {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private lateinit var shot: Shot

    private lateinit var battle: Battle

    private lateinit var shooterFleetId: FleetId
    private lateinit var shooterFleet: Fleet

    private lateinit var targetedFleetId: FleetId
    private lateinit var targetedFleet: Fleet

    private var battleUpdate = BattleUpdate()
    private var shooterFleetUpdate = FleetUpdate()
    private var targetedFleetUpdate = FleetUpdate()
    private var shipUpdate = ShipUpdate()

    private var shotShip: Ship? = null

    override fun shoot(shot: Shot): ShotReport {
        initialize(shot)
        loadBattle()
        checkBattleReady()
        checkPlayerTurn()
        loadFleets()

        if (isShotAlreadyPerformed())
            return shotAlreadyPerformedReport()

        resolveShot()
        if (isShipHit()) {
            computeDamagesOnShotShip()
            computeDamagesOnTargetFleet()
            if (isTargetedFleetSunk()) {
                markShootFleetVictorious()
            }
        }

        if (notVictoriousHit()) {
            switchBattleToNextStep()
        }

        saveAllChanges()

        return generateShotReport()
    }

    private fun initialize(shot: Shot) {
        this.shot = shot
        initializeAllUpdates()
    }

    private fun initializeAllUpdates() {
        battleUpdate = BattleUpdate()
        shooterFleetUpdate = FleetUpdate()
        targetedFleetUpdate = FleetUpdate()
        shipUpdate = ShipUpdate()
    }

    private fun loadBattle() {
        battle = battleManager.getBattleById(shot.battleId)
    }

    private fun checkBattleReady() {
        if (battle.status != FIGHTING) {
            val message = "Cannot shot when status is not $FIGHTING"
            logger.error(message)
            throw IllegalBattleStatusException(battle.id, FIGHTING, battle.status, message)
        }
    }


    private fun checkPlayerTurn() {
        if (battle.nextPlayer != shot.shootingPlayer) {
            throw NotPlayerTurnException(
                battle.id,
                shot.shootingPlayer,
                "In battle ${battle.id}, the player, the expected playing player is ${battle.nextPlayer}"
            )
        }
    }

    private fun loadFleets() {
        shooterFleetId = shot.shootingFleetId
        targetedFleetId = shooterFleetId.opponent()

        shooterFleet = fleetManager.getFleet(shooterFleetId)
        targetedFleet = fleetManager.getFleet(targetedFleetId)
    }

    private fun isShotAlreadyPerformed(): Boolean {
        return shooterFleet.shots.contains(shot.targetLocation)
    }

    private fun shotAlreadyPerformedReport(): ShotReport {
        return ShotReport(ShotResult.ALREADY_SHOT)
    }

    private fun resolveShot() {
        shooterFleetUpdate.addShot(shot.targetLocation)
        for (targetedShip in shipManager.getFleetShips(targetedFleetId)) {
            if (targetedShip.isAt(shot.targetLocation)) {
                shotShip = targetedShip
                return
            }
        }
        shotShip = null
    }

    private fun isShipHit(): Boolean {
        return shotShip != null
    }


    private fun computeDamagesOnShotShip() {
        shipUpdate.addHit(shot.targetLocation)
    }


    private fun computeDamagesOnTargetFleet() {
        if (targetedFleet.status == FleetStatus.OPERATIONAL) {
            targetedFleetUpdate.status(FleetStatus.DAMAGED)
        } else if (targetedFleet.status == FleetStatus.DAMAGED) {
            if (onlyOneTargetedShip() && isNewHitSunkShip()) {
                targetedFleetUpdate.status(FleetStatus.DESTROYED)
            }
        }

    }

    private fun onlyOneTargetedShip(): Boolean {
        return shipManager.getFleetShips(targetedFleetId)
            .count { it.status != ShipStatus.SUNK } == 1
    }

    private fun isNewHitSunkShip(): Boolean {
        return shotShip!!.hits.size + 1 == shotShip!!.type.size
    }

    private fun isTargetedFleetSunk(): Boolean {
        return targetedFleetUpdate.status() == FleetStatus.DESTROYED
    }

    private fun markShootFleetVictorious() {
        battleUpdate.status(FINISHED)
    }

    private fun notVictoriousHit(): Boolean {
        return battleUpdate.status() != FINISHED
    }


    private fun switchBattleToNextStep() {
        if (battle.nextPlayer == FIRST) {
            battleUpdate.nextPlayer(SECOND)
            battleUpdate.stopTime(timeProvider.nowAsLocalDateTime())
        } else {
            battleUpdate.incrementTurn(true)
            battleUpdate.nextPlayer(FIRST)
        }
    }


    private fun saveAllChanges() {
        if (!shipUpdate.isEmpty()) {
            val updatedShip = shipUpdate.updateShip(shotShip!!)
            shipManager.update(updatedShip)
        }

        val updatedShooterFleet = shooterFleetUpdate.updateFleet(shooterFleet)
        fleetManager.updateFleet(updatedShooterFleet)

        if (!targetedFleetUpdate.isEmpty()) {
            val updatedTargetedFleet = targetedFleetUpdate.updateFleet(targetedFleet)
            fleetManager.updateFleet(updatedTargetedFleet)
        }

        battleManager.update(battle.id, battleUpdate)
    }

    private fun generateShotReport(): ShotReport {
        if (shotShip == null)
            return ShotReport(ShotResult.MISSED)

        if (isNewHitSunkShip()) {
            val victoriousHit = battleUpdate.status() == FINISHED
            return ShotReport(ShotResult.SUNK, victoriousHit)
        }
        return ShotReport(ShotResult.HIT)
    }
}