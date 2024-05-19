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

package io.drevezerezh.scylla.advanced.scenario.dsl

import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarBaseListener
import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarParser
import org.slf4j.LoggerFactory

/**
 * a listener that propagates the events to other listeners
 */
class RootBattleshipScenarioDslListener(
    private val listeners: List<ScenarioInstructionListener>
) : ScenarioGrammarBaseListener() {

    private val logger = LoggerFactory.getLogger(RootBattleshipScenarioDslListener::class.java)

    override fun exitCreatePlayer(ctx: ScenarioGrammarParser.CreatePlayerContext) {
        val instruction = createInstruction(ctx)
        val line = ctx.getStart().line
        fire { it.onCreatePlayerInstruction(line, instruction) }
    }


    private fun fire(action: (ScenarioInstructionListener) -> Unit) {
        for (listener in listeners) {
            runSafely(listener, action)
        }
    }


    private fun runSafely(listener: ScenarioInstructionListener, action: (ScenarioInstructionListener) -> Unit) {
        try {
            action(listener)
        } catch (ex: RuntimeException) {
            logger.error("Failed fire action on listener", ex)
            println("ERROR: ${ex.message}")
        }
    }


    override fun exitDeployShip(ctx: ScenarioGrammarParser.DeployShipContext) {
        val instruction = createInstruction(ctx)
        val line = ctx.getStart().line
        fire { it.onShipDeploymentInstruction(line, instruction) }
    }


    override fun exitShot(ctx: ScenarioGrammarParser.ShotContext) {
        val instruction = createShotInstruction(ctx)
        val line = ctx.getStart().line
        fire { it.onShotInstruction(line, instruction) }
    }


    override fun exitStartBattle(ctx: ScenarioGrammarParser.StartBattleContext) {
        val instruction = createBattleStartingInstruction(ctx)
        val line = ctx.getStart().line
        fire { it.onStartBattleInstruction(line, instruction) }
    }


    companion object {
        private fun createInstruction(ctx: ScenarioGrammarParser.CreatePlayerContext): PlayerCreationInstruction {
            val playerName = ctx.PLAYER_NAME().text
            return PlayerCreationInstruction(playerName)
        }

        private fun createInstruction(ctx: ScenarioGrammarParser.DeployShipContext): ShipDeploymentInstruction {
            val playerName = ctx.PLAYER_NAME().text
            val shipType = LangMapper.toLangShipType(ctx.SHIP())
            val location = LangMapper.toLangLocation(ctx.LOCATION())
            val orientation = LangMapper.toLangOrientation(ctx.ORIENTATION())
            return ShipDeploymentInstruction(playerName, shipType, location, orientation)
        }

        private fun createShotInstruction(ctx: ScenarioGrammarParser.ShotContext): ShotInstruction {
            val playerName = ctx.PLAYER_NAME().text
            val location = ctx.LOCATION().text
            return ShotInstruction(playerName, location)
        }

        private fun createBattleStartingInstruction(ctx: ScenarioGrammarParser.StartBattleContext): BattleStartingInstruction {
            val player1Name = ctx.PLAYER_NAME(0).text
            val player2Name = ctx.PLAYER_NAME(1).text
            return BattleStartingInstruction(player1Name, player2Name)
        }
    }
}