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

package io.drevezadur.scylla.teacher.restserver.testutils

import io.drevezadur.scylla.teacher.client.command.CommandConsole
import io.drevezadur.scylla.teacher.client.command.CommandConsoleFactory
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContext

object IntegrationContextFactory {


    fun createContext(applicationContext: ApplicationContext, port: Int): IntegrationContext {
        val console: CommandConsole = createCommandConsole(applicationContext, port)
        return IntegrationContext(applicationContext, console)
    }

    private fun createCommandConsole(applicationContext: ApplicationContext, port: Int): CommandConsole {
        val testRestTemplate = applicationContext.getBean(TestRestTemplate::class.java)
        val restTemplate = testRestTemplate.restTemplate
        return CommandConsoleFactory.createRestCommandConsole(restTemplate, port)
    }

    fun createContext(applicationContext: ApplicationContext): IntegrationContext {
        val console = CommandConsoleFactory.createUseCaseCommandConsole(applicationContext)
        return IntegrationContext(applicationContext, console)
    }

}