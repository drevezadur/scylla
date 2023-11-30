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

package io.drevezadur.scylla.teacher.client.parser.text

import io.drevezadur.scylla.teacher.client.command.CommandConsole
import io.drevezadur.scylla.teacher.client.command.GameCommandException
import io.drevezadur.scylla.teacher.client.parser.InstructionInterpreter
import io.drevezadur.scylla.teacher.restserver.lang.*
import io.drevezadur.scylla.teacher.restserver.service.model.*
import java.util.*

class InstructionInterpreterImpl(
    private val commandConsole: CommandConsole
) : InstructionInterpreter {

    private var player1: PlayerPojo = NO_USER
    private var player2: PlayerPojo = NO_USER
    private var battleId = INVALID_ID

    private var shooter: PlayerPojo = player1

    override fun createPlayer(name: String) {
        commandConsole.createPlayer(name)
    }

    override fun createBattle(player1Name: String, player2Name: String) {
        player1 = getPlayer(player1Name)
        player2 = getPlayer(player2Name)
        battleId = commandConsole.createBattle(BattleCreationPojo(player1.uuid, player2.uuid))
        shooter = player1
    }

    private fun getPlayer(name: String): PlayerPojo {
        return commandConsole.getAllPlayers().firstOrNull { it.name == name }
            ?: throw GameCommandException("Cannot found player with name '$name'")
    }

    override fun deploy(playerName: String, shipType: ShipType, location: Location, orientation: GridOrientation) {
        val playerId: UUID = getPlayerId(playerName)
        commandConsole.deployShip(battleId, playerId, ShipDeploymentBody(shipType, location.x, location.y, orientation))
    }

    private fun getPlayerId(name: String): UUID {
        return when (name) {
            player1.name -> player1.uuid
            player2.name -> player2.uuid
            else -> throw GameCommandException("Cannot found player with name '$name'")
        }
    }

    override fun shot(target: Location) {
        val report: ShotReportPojo = commandConsole.fireFromFleet(battleId, shooter.uuid, target)
        when (report.shotResult) {
            ShotResult.MISSED -> println("\tShot missed")
            ShotResult.HIT -> println("\tShot hit a ship")
            ShotResult.SUNK -> println("\tShot sunk a ship")
            ShotResult.ALREADY_SHOT -> println("\tShot already performed")
        }
        if (report.winner) {
            println("\tBattle is over, winner is ${shooter.name}")
        } else {
            switchShooter()
        }
    }

    private fun switchShooter() {
        shooter = if (shooter == player1) player2 else player1
    }

    override fun fleetStatus(playerName: String) {
        val playerId = getPlayerId(playerName)
        val fleet = commandConsole.findFleetById(battleId, playerId)
            ?: throw GameCommandException("Cannot found fleet of player $playerName in battle $battleId")

        print("Status: ")
        when (fleet.status) {
            FleetStatus.NOT_DEPLOYED -> println("not deployed")
            FleetStatus.UNHARMED -> println("deployed, all ships are unharmed")
            FleetStatus.DAMAGED -> println("deployed, some ships are damaged nor sunk")
            FleetStatus.SUNK -> println("deployed, all ships are sunk")
        }
        if (fleet.status == FleetStatus.DAMAGED)
            printFleetContent(playerId)
    }

    private fun printFleetContent(playerId: UUID) {
        val ships = commandConsole.getAllShipsInFleet(battleId, playerId)
        ships.forEach { printShip(it) }
    }

    private fun printShip(ship: ShipPojo) {
        val hitCount = ship.hits.size
        val notHitCount = ship.type.size - hitCount

        println("Ship: ${ship.type}")
        println("\tStatus: ${ship.status}")
        println("\tHit count: $hitCount")
        println("\tNot hit count: $notHitCount")
    }


    override fun battleStatus() {
        if (battleId == INVALID_ID) {
            println("Battle not created")
            return
        }

        val battle = commandConsole.findBattleById(battleId)
        if (battle == null) {
            println("Battle not found")
            return
        }

        when (battle.status) {
            BattleStatus.DEPLOYMENT -> printDeploymentBattle(battle)
            BattleStatus.DEPLOYED -> printDeployedBattle(battle)
            BattleStatus.RUNNING -> printRunningBattle(battle)
            BattleStatus.FINISHED -> printFinishedBattle(battle)
        }
    }

    private fun printDeploymentBattle(battle: BattlePojo) {
        println("Phase: deployment")
    }

    private fun printDeployedBattle(battle: BattlePojo) {
        println("Phase: deployed")
        printShootingPlayer(battle)
    }

    private fun printShootingPlayer(battle: BattlePojo) {
        val playerName = getShooterName(battle)
        println("\tShooter: $playerName")
    }

    private fun getShooterName(battle: BattlePojo): String {
        return getPlayerName(battle.shootingPlayer)
    }

    private fun getPlayerName(playerId: UUID): String {
        return if (playerId == player1.uuid) player1.name else player2.name
    }

    private fun printRunningBattle(battle: BattlePojo) {
        println("Phase: running")
        printShootingPlayer(battle)
    }

    private fun printFinishedBattle(battle: BattlePojo) {
        println("Phase: finished")
        printWinner(battle)
    }

    private fun printWinner(battle: BattlePojo) {
        val playerName = getWinnerName(battle)
        println("\tWinner: $playerName")
    }

    private fun getWinnerName(battle: BattlePojo): String {
        return getPlayerName(battle.shootingPlayer)
    }


    companion object {
        private val INVALID_ID = UUID.randomUUID()
        private val NO_USER = PlayerPojo(INVALID_ID, "No User")
    }
}