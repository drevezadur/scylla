/*
 * Copyright (c) 2023. gofannon.io
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

package io.gofannon.scylla.homework.service.internal

import io.gofannon.scylla.homework.service.Scenario

/**
 * A simple [ScenarioFactory] implementation
 */
internal class DefaultScenarioFactory(
    private val commandFactory: CommandFactory
) : ScenarioFactory {
    override fun createScenario(instructions: List<String>): Scenario {
        val commands: List<Command> = instructions.map(commandFactory::createCommand)
        return DefaultScenario(commands)
    }
}