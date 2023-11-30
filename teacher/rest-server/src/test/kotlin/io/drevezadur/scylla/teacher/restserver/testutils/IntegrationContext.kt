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

import io.drevezadur.scylla.teacher.client.ScenarioRunner
import io.drevezadur.scylla.teacher.client.ScenarioRunnerFactory
import io.drevezadur.scylla.teacher.client.command.CommandConsole
import io.drevezadur.scylla.teacher.restserver.domain.BattleNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.ShipNotFoundException
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.persist.BattleRepository
import io.drevezadur.scylla.teacher.restserver.persist.FleetRepository
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.persist.ShipRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.PlayerPEntity
import io.drevezadur.scylla.teacher.restserver.service.model.BattlePojo
import io.drevezadur.scylla.teacher.restserver.service.model.FleetPojo
import io.drevezadur.scylla.teacher.restserver.service.model.ShipPojo
import org.assertj.core.api.Assertions.assertThat
import org.springframework.context.ApplicationContext

class IntegrationContext(
    applicationContext: ApplicationContext,
    val console: CommandConsole
) {

    private val scenarioRunner: ScenarioRunner = ScenarioRunnerFactory.createScenarioRunner(console)

    private val playerRepository: PlayerRepository = applicationContext.getBean(PlayerRepository::class.java)
    private val battleRepository: BattleRepository = applicationContext.getBean(BattleRepository::class.java)
    private val fleetRepository: FleetRepository = applicationContext.getBean(FleetRepository::class.java)
    private val shipRepository: ShipRepository = applicationContext.getBean(ShipRepository::class.java)


    /**
     * Delete all stored data in server
     */
    fun resetAllData() {
        shipRepository.deleteAll()
        fleetRepository.deleteAll()
        battleRepository.deleteAll()
        playerRepository.deleteAll()
    }


    fun runScript(script: String) {
        scenarioRunner.execute(script)
    }

    fun runScripts(vararg scripts: String) {
        scripts.forEach(scenarioRunner::execute)
    }

    fun getPlayerId(name: String): PlayerUUID {
        val player = playerRepository.findByName(name)
        assertThat(player).isNotNull
        assertThat(player!!.id).isNotNull
        return player.id!!
    }

    fun createPlayer(id: PlayerUUID, name: String) {
        playerRepository.save(PlayerPEntity(id, name))
    }

    fun getFleetOf(playerName: String): FleetPojo {
        val playerId = getPlayerId(playerName)
        val battleId = getCurrentBattleId()

        return console.findFleetById(battleId, playerId)!!
    }


    fun getCurrentBattleId(): BattleUUID {
        if (battleRepository.count() != 1L)
            throw IllegalStateException("Battle repository shall contain exactly one battle")
        return battleRepository.findAll().first().id!!
    }

    fun getCurrentBattle(): BattlePojo {
        val battleId = getCurrentBattleId()
        return console.findBattleById(battleId) ?: throw BattleNotFoundException(battleId, "Cannot found battle")
    }

    fun getShipOf(playerName: String, shipType: ShipType): ShipPojo {
        val battleId = getCurrentBattleId()
        val playerId = getPlayerId(playerName)
        return console.findShipById(battleId, playerId, shipType) ?: throw ShipNotFoundException(
            battleId,
            playerId,
            shipType,
            "Cannot found ship"
        )
    }
}