package io.drevezerezh.scylla.advanced.scenario.dsl

import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarBaseListener
import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarLexer
import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStream
import java.util.function.Consumer

internal class DefaultScenarioParser : ScenarioParser {
    private val listeners: MutableList<ScenarioInstructionListener> = ArrayList()
    private val rootListener = RootBattleshipScenarioDslListener(listeners)

    override fun addListener(listener: ScenarioInstructionListener) {
        if (!listeners.contains(listener))
            listeners.add(listener)
    }

    override fun removeListener(listener: ScenarioInstructionListener) {
        listeners.remove(listener)
    }

    override fun parse(scenario: String) {
        val charStream: CharStream = ANTLRInputStream(scenario)
        val lexer = ScenarioGrammarLexer(charStream)
        val tokenStream: TokenStream = CommonTokenStream(lexer)

        val parser = ScenarioGrammarParser(tokenStream)
        parser.addParseListener(rootListener)
        parser.scenario()
    }

    private class RootBattleshipScenarioDslListener(private val listeners: List<ScenarioInstructionListener>) :
        ScenarioGrammarBaseListener() {
        override fun exitCreatePlayer(ctx: ScenarioGrammarParser.CreatePlayerContext) {
            val instruction = createInstruction(ctx)

            val line = ctx.getStart().line
            fireInstruction(line, instruction)
        }

        private fun fireInstruction(line: Int, instruction: PlayerCreationInstruction) {
            listeners.forEach(Consumer { it: ScenarioInstructionListener -> it.onCreatePlayerInstruction(line, instruction) })
        }


        override fun exitDeployShip(ctx: ScenarioGrammarParser.DeployShipContext) {
            val instruction = createInstruction(ctx)

            val line = ctx.getStart().line
            fireInstruction(line, instruction)
        }

        private fun fireInstruction(line: Int, instruction: ShipDeploymentInstruction) {
            listeners.forEach(Consumer { it: ScenarioInstructionListener -> it.onShipDeploymentInstruction(line, instruction) })
        }


        override fun exitShot(ctx: ScenarioGrammarParser.ShotContext) {
            val instruction = createShotInstruction(ctx)

            val line = ctx.getStart().line
            fireInstruction(line, instruction)
        }

        private fun fireInstruction(line: Int, instruction: ShotInstruction) {
            listeners.forEach(Consumer { it: ScenarioInstructionListener -> it.onShotInstruction(line, instruction) })
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
        }
    }
}