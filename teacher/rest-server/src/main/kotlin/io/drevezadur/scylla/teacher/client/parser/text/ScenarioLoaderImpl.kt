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

import io.drevezadur.scylla.teacher.client.parser.*
import java.util.regex.Pattern

class ScenarioLoaderImpl : ScenarioLoader {

    private val statementParserList: List<StatementParser> = listOf(
        DoNothingStatementParser,
        CreatePlayerStatementParser(),
        CreateBattleStatementParser(),
        DeployShipStatementParser(),
        ShotStatementParser(),
        BattleStatusStatementParser(),
        FleetStatusStatementParser()
    )


    override fun loadStatements(scenarioStatements: String): Scenario {
        val instructions = scenarioStatements.split(SCENARIO_SEPARATOR)
            .asSequence()
            .map { it.trim() }
            .map(::cleanStatement)
            .filterNot(::isIgnorable)
            .map(::toInstruction)
            .toList()
        return ScenarioImpl(instructions)
    }


    private fun toInstruction(statement: String): Instruction {
        statementParserList.forEach {
            val instruction = it.parse(statement)
            if (instruction != null)
                return instruction
        }
        throw UnsupportedStatementException("Cannot convert statement '$statement' to instruction")
    }

    companion object {
        private val SCENARIO_SEPARATOR: Pattern = Pattern.compile("\r?\n")

        private fun isIgnorable(statement: String): Boolean {
            return statement.isBlank() || statement.startsWith('#')
        }

        private fun cleanStatement(rawStatement: String): String {
            return rawStatement.substringBefore('#').trim()
        }
    }
}