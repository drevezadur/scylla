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

import io.drevezerezh.scylla.advanced.lang.GridLocation.Companion.of
import io.drevezerezh.scylla.advanced.lang.GridOrientation
import io.drevezerezh.scylla.advanced.lang.ShipType
import io.drevezerezh.scylla.advanced.scenario.dsl.LangMapper.toLangOrientation
import io.drevezerezh.scylla.advanced.scenario.dsl.LangMapper.toLangShipType
import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarBaseListener
import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarLexer
import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarListener
import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarParser
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStream
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class ParsingDslTest {
    private val createdPlayerNameList: MutableList<String> = ArrayList()
    private val deployInstructionList: MutableList<ShipDeploymentInstruction> = ArrayList()
    private val shotInstructionList: MutableList<ShotInstruction> = ArrayList()

    @AfterEach
    fun tearDown() {
        createdPlayerNameList.clear()
        deployInstructionList.clear()
    }


    @Test
    fun createSinglePlayer() {
        val scenario = """
                create player John
                """.trimIndent()
        val listener: ScenarioGrammarListener = object : ScenarioGrammarBaseListener() {

            override fun exitCreatePlayer(ctx: ScenarioGrammarParser.CreatePlayerContext) {
                val playerName = ctx.PLAYER_NAME().text
                createdPlayerNameList.add(playerName)
            }
        }

        parseScenario(scenario, listener)

        Assertions.assertThat(createdPlayerNameList)
            .containsOnly("John")
    }


    @Test
    fun createSeveralPlayers() {
        val scenario = """
                create player John
                create player Jane
                """.trimIndent()
        val listener: ScenarioGrammarListener = object : ScenarioGrammarBaseListener() {
            override fun exitCreatePlayer(ctx: ScenarioGrammarParser.CreatePlayerContext) {
                val playerName = ctx.PLAYER_NAME().text
                createdPlayerNameList.add(playerName)
            }
        }

        parseScenario(scenario, listener)

        Assertions.assertThat(createdPlayerNameList)
            .containsOnly("John", "Jane")
    }


    private fun parseScenario(scenario: String, listener: ScenarioGrammarListener) {
        val charStream: CharStream = CharStreams.fromString(scenario)
        val lexer = ScenarioGrammarLexer(charStream)
        val tokenStream: TokenStream = CommonTokenStream(lexer)

        val parser = ScenarioGrammarParser(tokenStream)
        parser.addParseListener(listener)
        parser.scenario()
    }


    @Test
    fun deployShip() {
        val scenario = """
                create player John
                create player Jane
                start battle with John and Jane
                John deploy Carrier at A0 on row
                Jane deploy Battleship at D5 on column
                """.trimIndent()
        val listener: ScenarioGrammarListener = object : ScenarioGrammarBaseListener() {
            override fun exitDeployShip(ctx: ScenarioGrammarParser.DeployShipContext) {
                val playerName = ctx.PLAYER_NAME().text
                val ship = toLangShipType(ctx.SHIP().text)
                val location = of(ctx.LOCATION().text)
                val orientation = toLangOrientation(ctx.ORIENTATION().text)
                val instruction = ShipDeploymentInstruction(
                    playerName, ship, location, orientation
                )
                deployInstructionList.add(instruction)
            }
        }

        parseScenario(scenario, listener)

        Assertions.assertThat(deployInstructionList)
            .containsExactly(
                ShipDeploymentInstruction("John", ShipType.CARRIER, of("A0"), GridOrientation.ROW),
                ShipDeploymentInstruction("Jane", ShipType.BATTLESHIP, of("D5"), GridOrientation.COLUMN)
            )
    }


    @Test
    fun shot() {
        val scenario = """
                create player John
                create player Jane
                start battle with John and Jane
                John shot at A7
                Jane shot at D3
                """.trimIndent()
        val listener: ScenarioGrammarListener = object : ScenarioGrammarBaseListener() {
            override fun exitShot(ctx: ScenarioGrammarParser.ShotContext) {
                val playerName = ctx.PLAYER_NAME().text
                val location = ctx.LOCATION().text
                val instruction = ShotInstruction(playerName, location)
                shotInstructionList.add(instruction)
            }
        }

        parseScenario(scenario, listener)

        Assertions.assertThat(shotInstructionList)
            .containsExactly(
                ShotInstruction("John", "A7"),
                ShotInstruction("Jane", "D3")
            )
    }
}
