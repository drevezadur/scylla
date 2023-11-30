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
import io.drevezadur.scylla.teacher.client.command.CommandConsoleFactory
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.web.client.RestTemplate

object ScenarioRunnerFactory {

    fun createScenarioRunner(testRestTemplate: TestRestTemplate, port: Int): ScenarioRunner {
        return createScenarioRunner(testRestTemplate.restTemplate, port)
    }

    fun createScenarioRunner(restTemplate: RestTemplate, port: Int): ScenarioRunner {
        val console = CommandConsoleFactory.createRestCommandConsole(restTemplate, port)
        return createScenarioRunner(console)
    }

    fun createScenarioRunner(console: CommandConsole): ScenarioRunner {
        return ScenarioRunnerImpl(console)
    }
}