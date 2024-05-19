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

import io.drevezerezh.scylla.advanced.domain.api.usecase.BattleUseCaseManager
import io.drevezerezh.scylla.advanced.domain.api.usecase.PlayerUseCaseManager
import io.drevezerezh.scylla.advanced.scenario.dsl.ScenarioParser
import io.drevezerezh.scylla.advanced.scenario.dsl.ScenarioParserFactory

class ScenarioRunner(
    private val playerUseCaseManager: PlayerUseCaseManager,
    private val battleUseCaseManager: BattleUseCaseManager
) {

    private val factory = ScenarioParserFactory()

    fun run(scenario: String) {
        val scenarioParser = createParser()
        scenarioParser.parse(scenario)
    }


    private fun createParser(): ScenarioParser {
        val instructionRunner = InstructionRunner(playerUseCaseManager, battleUseCaseManager)
        val scenarioParser = factory.create()
        scenarioParser.addListener(instructionRunner)
        return scenarioParser
    }
}