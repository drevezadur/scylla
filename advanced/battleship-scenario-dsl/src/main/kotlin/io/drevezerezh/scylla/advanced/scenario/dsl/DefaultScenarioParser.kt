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

import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarLexer
import io.drevezerezh.scylla.advanced.scenario.dsl.grammar.ScenarioGrammarParser
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.TokenStream

/**
 * Default and simple scenario parser implementation
 *
 * It is based on the [ScenarioGrammarLexer] and [ScenarioGrammarParser].
 */
class DefaultScenarioParser : ScenarioParser {
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
        val charStream: CharStream =  CharStreams.fromString(scenario)
        val lexer = ScenarioGrammarLexer(charStream)
        val tokenStream: TokenStream = CommonTokenStream(lexer)

        val parser = ScenarioGrammarParser(tokenStream)
        parser.addParseListener(rootListener)
        parser.scenario()
    }
}