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

import io.gofannon.scylla.homework.domain.internal.DefaultGameFactory
import io.gofannon.scylla.homework.domain.internal.DefaultPlayerBoardFactory
import io.gofannon.scylla.homework.domain.internal.DefaultShipFactory
import io.gofannon.scylla.homework.domain.internal.ShipFactory
import io.gofannon.scylla.homework.lang.GameStatus
import io.gofannon.scylla.homework.service.Scenario
import io.gofannon.scylla.homework.service.ScenarioLoader
import io.gofannon.scylla.homework.service.internal.DefaultCommandFactory
import io.gofannon.scylla.homework.service.internal.DefaultScenarioFactory
import java.nio.file.Path
import kotlin.system.exitProcess


private const val HELP_EXIT_CODE: Int = 1
private const val BAD_ARG_COUNT_EXIT_CODE: Int = 2
private const val BAD_FILE_PATH_EXIT_CODE: Int = 3
private const val BAD_FILE_CONTENT_EXIT_CODE: Int = 4
private const val SCENARIO_EXECUTION_FAILURE_EXIT_CODE: Int = 5

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Scylla Homework shall have a single argument which must be a path to the scenario file.")
        printUsage()
        exitProcess(BAD_ARG_COUNT_EXIT_CODE)
    }

    if (isHelpOption(args[0])) {
        printUsage()
        exitProcess(HELP_EXIT_CODE)
    }

    val path = extractScenarioFilePath(args[0])
    if (path == null) {
        System.err.println("Scylla Homework argument shall be a valid file path to a scenario file.")
        printUsage()
        exitProcess(BAD_FILE_PATH_EXIT_CODE)
    }


    val scenario = loadScenario(path)
    if (scenario == null) {
        System.err.println("Scylla Homework argument shall be a scenario file with a valid content.")
        printUsage()
        exitProcess(BAD_FILE_CONTENT_EXIT_CODE)
    }

    val gameResultMessage = executeScenario(scenario)
    if (gameResultMessage == null) {
        System.err.println("Scylla Homework fails to execute the scenario")
        printUsage()
        exitProcess(SCENARIO_EXECUTION_FAILURE_EXIT_CODE)
    }

    println(gameResultMessage)
}


private fun printUsage() {
    println("Scylla Homework usage :")
    println("Main <scenario-path>")
    println("   <scenario-path> path to an existing scenario file")
}


private fun isHelpOption(option: String): Boolean {
    return setOf("-h", "--help").contains(option)
}

private fun extractScenarioFilePath(pathAsString: String): Path? {
    val path = Path.of(pathAsString)
    val file = path.toFile()

    if (file.isFile && file.exists())
        return path
    return null
}

private fun loadScenario(path: Path): Scenario? {
    return try {

        val scenarioLoader = createScenarioLoader()
        scenarioLoader.loadFromPath(path)

    } catch (ex: RuntimeException) {
        null
    }
}

private fun createScenarioLoader(): ScenarioLoader {
    val commandFactory = DefaultCommandFactory()
    val scenarioFactory = DefaultScenarioFactory(commandFactory)
    return ScenarioLoader(scenarioFactory)
}


private fun executeScenario(scenario: Scenario): String? {
    return try {

        val gameFactory = createGameFactory()
        val game = gameFactory.createGame()
        scenario.execute(game)

        when (game.getStatus()) {
            GameStatus.DEPLOYMENT -> "Game stay in deployment stage"
            GameStatus.DEPLOYED -> "Game stay in deployed stage"
            GameStatus.RUNNING -> "Game stay in fighting stage"
            GameStatus.FINISHED -> "Game is finished and the winner is ${game.getWinner()}"
        }

    } catch (ex: RuntimeException) {
        null
    }
}

private fun createGameFactory(): DefaultGameFactory {
    val shipFactory: ShipFactory = DefaultShipFactory()
    val fleetBoardFactory = DefaultPlayerBoardFactory(shipFactory)
    return DefaultGameFactory(fleetBoardFactory)
}