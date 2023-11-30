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

package io.drevezadur.scylla.teacher.client

import io.drevezadur.scylla.teacher.client.command.CommandConsole
import io.drevezadur.scylla.teacher.client.parser.Scenario
import io.drevezadur.scylla.teacher.client.parser.text.InstructionInterpreterImpl
import io.drevezadur.scylla.teacher.client.parser.text.ScenarioLoaderImpl
import java.io.InputStreamReader

class ScenarioRunnerImpl(
    commandConsole: CommandConsole
) : ScenarioRunner {

    private val instructionInterpreter = InstructionInterpreterImpl(commandConsole)

    override fun execute(statements: String) {
        val scenario = convertToScenario(statements)
        execute(scenario)
    }

    override fun executeFromResource(resource: String) {
        val statements = loadStatements(resource)
        val scenario = convertToScenario(statements)
        execute(scenario)
    }

    private fun loadStatements(resource: String): String {
        return ScenarioRunnerImpl::class.java.getResourceAsStream(resource)!!.use {
            InputStreamReader(it, Charsets.UTF_8).readText()
        }
    }

    private fun convertToScenario(statements: String): Scenario {
        val loader = ScenarioLoaderImpl()
        return loader.loadStatements(statements)
    }

    private fun execute(scenario: Scenario) {
        scenario.execute(instructionInterpreter)
    }
}