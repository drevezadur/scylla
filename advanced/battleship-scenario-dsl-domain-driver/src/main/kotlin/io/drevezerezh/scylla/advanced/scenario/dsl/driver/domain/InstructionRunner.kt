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

package io.drevezerezh.scylla.advanced.scenario.dsl.driver.domain

import io.drevezerezh.scylla.advanced.domain.api.battle.*
import io.drevezerezh.scylla.advanced.domain.api.player.*
import io.drevezerezh.scylla.advanced.domain.api.ship.*
import io.drevezerezh.scylla.advanced.domain.api.shot.Shot
import io.drevezerezh.scylla.advanced.domain.api.usecase.*
import io.drevezerezh.scylla.advanced.lang.*
import io.drevezerezh.scylla.advanced.scenario.dsl.*
import org.slf4j.LoggerFactory

internal class InstructionRunner(
    private val playerUseCaseManager: PlayerUseCaseManager,
    private val battleUseCaseManager: BattleUseCaseManager,
) : ScenarioInstructionListener {

    private val logger = LoggerFactory.getLogger(InstructionRunner::class.java)

    private var line: Int = -1
    private var firstPlayer: Player? = null
    private var secondPlayer: Player? = null
    private var battleId: String? = null
    private var battleEnded: Boolean = false

    override fun onCreatePlayerInstruction(line: Int, instruction: PlayerCreationInstruction) {
        initContext(line)

        try {

            val playerCreation = PlayerCreation(instruction.playerName)
            val player = playerUseCaseManager.create(playerCreation)
            attributePlayer(player)

        } catch (ex: PlayerAlreadyExistException) {
            fail("Fail to create player at $line. Player already exists. Name: '${instruction.playerName}'", cause = ex)
        } catch (ex: InvalidPlayerAttributeException) {
            fail("Fail to create player at $line. Invalid player name. Name: '${instruction.playerName}'", cause = ex)
        } catch (ex: BattleEndedException) {
            fail("Fail to create player at $line. The battle is already ended.", ex)
        }
    }

    private fun initContext(line: Int) {
        this.line = line
    }


    private fun attributePlayer(player: Player) {
        if (firstPlayer == null) {
            firstPlayer = player
        } else if (secondPlayer == null) {
            secondPlayer = player
        } else {
            fail("Fail to create player at $line. All required players already defined.")
        }
    }


    private fun fail(logMessage: String, cause: RuntimeException? = null): Nothing {
        if (cause != null)
            logger.error(logMessage, cause)
        else
            logger.error(logMessage)

        throw ScenarioExecutionFailedException(line, logMessage, cause)
    }


    override fun onStartBattleInstruction(line: Int, instruction: BattleStartingInstruction) {
        this.initContext(line)
        checkNoDefinedBattle()

        try {

            val battleCreation = BattleCreation(instruction.player1Name, instruction.player2Name)
            val battle = battleUseCaseManager.create(battleCreation)
            this.battleId = battle.id

        } catch (ex: PlayerNotFoundException) {
            fail("Fail to create battle at $line. Player not found. Id: '${ex.playerId}'", cause = ex)
        }
    }

    private fun checkNoDefinedBattle() {
        if (battleId != null) {
            fail("Fail to create battle at $line. A battle is already defined.")
        }
    }

    override fun onShipDeploymentInstruction(line: Int, instruction: ShipDeploymentInstruction) {
        initContext(line)
        checkBattleDefined { "Fail to deploy ship at $line. No battle is not defined." }
        val playerOrder =
            getPlayerOrder(instruction.playerName) { "Fail to deploy ship at $line. Cannot find player. Name: ${instruction.playerName}" }

        try {

            val shipDeployment = ShipDeployment(
                battleId!!,
                playerOrder,
                instruction.shipType,
                instruction.location,
                instruction.orientation
            )
            battleUseCaseManager.deployShip(shipDeployment)

        } catch (ex: ShipAlreadyDeployedException) {
            fail(
                "Fail to deploy ship at $line. The ship has already been deployed. Ship type: ${instruction.shipType}",
                ex
            )
        } catch (ex: ShipOutOfGridException) {
            fail(
                "Fail to deploy ship at $line. The ship location is out of the grid. Origin: ${instruction.location}, Orientation: ${instruction.orientation}",
                ex
            )
        } catch (ex: ShipOverlapException) {
            fail(
                "Fail to deploy ship at $line. The ship overlaps another ship. Origin: ${instruction.location}, Orientation: ${instruction.orientation}",
                ex
            )
        } catch (ex: BattleEndedException) {
            fail("Fail to deploy ship at $line. The battle is already ended.", ex)
        }
    }

    private fun checkBattleDefined(logMessage: () -> String) {
        if (battleId == null) {
            logger.error(logMessage.invoke())
            throw ScenarioExecutionFailedException(this.line, "Battle not defined")
        }
    }

    private fun getPlayerOrder(playerName: String, logMessage: () -> String): BattlePlayer {
        return if (isSamePlayer(playerName, firstPlayer))
            BattlePlayer.FIRST
        else if (isSamePlayer(playerName, secondPlayer))
            BattlePlayer.SECOND
        else {
            fail(logMessage.invoke())
        }
    }


    override fun onShotInstruction(line: Int, instruction: ShotInstruction) {
        initContext(line)
        checkBattleDefined { "Fail to shot at $line. No battle defined." }
        val playerOrder =
            getPlayerOrder(instruction.playerName) { "Fail to shot at $line. Cannot find player with name ${instruction.playerName}" }
        val location = toGridLocation(instruction.location)

        try {

            val shot = Shot(battleId!!, playerOrder, location)
            val report = battleUseCaseManager.shot(shot)
            if (report.victorious)
                battleEnded = true

        } catch (ex: BattleEndedException) {
            fail("Fail to shot at $line. The battle is already ended.", ex)
        }
    }

    private fun toGridLocation(location: String): GridLocation {
        try {
            return GridLocation.of(location)
        } catch (ex: IllegalArgumentException) {
            fail("Fail to shot at $line. Invalid target location. Location: $location")
        }
    }

    companion object {
        private fun isSamePlayer(playerName: String, player: Player?): Boolean {
            return player != null && player.name == playerName
        }
    }
}