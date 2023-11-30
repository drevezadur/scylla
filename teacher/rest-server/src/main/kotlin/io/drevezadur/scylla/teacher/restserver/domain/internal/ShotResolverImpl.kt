/*
 * Copyright (c)  2023-2023.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.drevezadur.scylla.teacher.restserver.domain.internal

import io.drevezadur.scylla.teacher.restserver.domain.BattleException
import io.drevezadur.scylla.teacher.restserver.domain.PlayerNotInBattleException
import io.drevezadur.scylla.teacher.restserver.domain.UnexpectedBattleStatusException
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.ShotReport
import io.drevezadur.scylla.teacher.restserver.domain.store.BattleStore
import io.drevezadur.scylla.teacher.restserver.domain.store.FleetStore
import io.drevezadur.scylla.teacher.restserver.domain.store.PlayerStore
import io.drevezadur.scylla.teacher.restserver.domain.store.ShipStore
import io.drevezadur.scylla.teacher.restserver.lang.*

class ShotResolverImpl(
    private val playerStore: PlayerStore,
    private val battleStore: BattleStore,
    private val fleetStore: FleetStore,
    private val shipStore: ShipStore
) : ShotResolver {

    private lateinit var firingFleet: FleetDEntity
    private lateinit var targetFleet: FleetDEntity
    private lateinit var battle: BattleDEntity
    private var targetShip: ShipDEntity? = null

    override fun resolve(battleId: BattleUUID, fromPlayerId: PlayerUUID, targetLocation: Location): ShotReport {
        initBattleAndFleets(battleId, fromPlayerId)
        checkShotAuthorizedInBattle()
        checkFiringFleetAuthorizedToShot()

        val shotResult: ShotResult = resolveShot(targetLocation)
        var winner = false
        if (shotResult == ShotResult.SUNK && targetFleet.status == FleetStatus.SUNK) {
            winner = true
            endBattle(firingFleet.playerId)
        }
        switchShootingFleet()
        saveChanges()

        return ShotReport(shotResult, winner)
    }

    private fun initBattleAndFleets(battleId: BattleUUID, fromPlayerId: PlayerUUID) {
        playerStore.getById(fromPlayerId)
        battle = battleStore.getById(battleId)
        firingFleet = fleetStore.getFleetByBattleAndPlayer(battleId, fromPlayerId)
        targetFleet = fleetStore.getOpponentFleet(battleId, fromPlayerId)
    }

    private fun checkShotAuthorizedInBattle() {
        if (battle.status == BattleStatus.DEPLOYED)
            battle.status = BattleStatus.RUNNING
        else if (battle.status != BattleStatus.RUNNING)
            throw UnexpectedBattleStatusException(
                battle.id,
                battle.status,
                BattleStatus.RUNNING,
                message = "Battle with id '${battle.id}' is not ready to fight"
            )
    }

    private fun checkFiringFleetAuthorizedToShot() {
        if (battle.shooterPlayerId != firingFleet.playerId)
            throw BattleException(
                battle.id,
                firingFleet.playerId,
                "In battle '${battle.id}', it is not the turn of fleet '${firingFleet.playerId}' to fight"
            )
    }


    private fun resolveShot(targetLocation: Location): ShotResult {
        val ships = shipStore.getAllShipsInFleet(battle.id, targetFleet.playerId)
        val shotResult = applyShotOnShips(ships, targetLocation)
        if (shotResult.structuralChange) {
            targetFleet.status = computeFleetStatus(ships)
        }
        return shotResult
    }

    private fun saveChanges() {
        battleStore.save(battle)
        fleetStore.save(targetFleet)
        if (targetShip != null)
            shipStore.save(targetShip!!)
    }

    private fun applyShotOnShips(ships: List<ShipDEntity>, targetLocation: Location): ShotResult {
        ships.forEach {
            val shotResult = applyShotOnShip(it, targetLocation)
            if (shotResult != ShotResult.MISSED)
                return shotResult
        }
        return ShotResult.MISSED
    }

    private fun applyShotOnShip(ship: ShipDEntity, targetLocation: Location): ShotResult {
        if (ship.hits.contains(targetLocation)) {
            return ShotResult.ALREADY_SHOT
        } else if (ship.contains(targetLocation)) {
            ship.addHit(targetLocation)
            targetShip = ship
            return if (ship.status == ShipStructuralStatus.DAMAGED)
                ShotResult.HIT
            else
                ShotResult.SUNK
        }
        return ShotResult.MISSED
    }

    private fun computeFleetStatus(ships: List<ShipDEntity>): FleetStatus {
        val shipStatusSet = ships.map { it.status }.toSet()
        return if (shipStatusSet.size == 1) {
            when (shipStatusSet.first()) {
                ShipStructuralStatus.UNHARMED -> FleetStatus.UNHARMED
                ShipStructuralStatus.DAMAGED -> FleetStatus.DAMAGED
                ShipStructuralStatus.DESTROYED -> FleetStatus.SUNK
            }
        } else {
            FleetStatus.DAMAGED
        }
    }


    private fun endBattle(winPlayerId: PlayerUUID) {
        checkContainPlayer(winPlayerId)
        battle.winnerId = winPlayerId
        battle.status = BattleStatus.FINISHED
    }

    private fun checkContainPlayer(playerId: PlayerUUID) {
        if (playerId != battle.player1Id && playerId != battle.player2Id)
            throw PlayerNotInBattleException(battle.id, playerId)
    }

    private fun switchShootingFleet() {
        battle.switchShooterPlayer()
    }
}
