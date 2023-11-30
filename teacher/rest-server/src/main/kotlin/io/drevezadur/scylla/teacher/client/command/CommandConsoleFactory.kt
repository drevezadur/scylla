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

package io.drevezadur.scylla.teacher.client.command

import io.drevezadur.scylla.teacher.client.command.impl.CommandConsoleImpl
import io.drevezadur.scylla.teacher.client.command.impl.service.*
import io.drevezadur.scylla.teacher.client.command.impl.service.http.*
import io.drevezadur.scylla.teacher.client.command.impl.service.usecase.*
import io.drevezadur.scylla.teacher.restserver.domain.usecase.*
import org.springframework.context.ApplicationContext
import org.springframework.web.client.RestTemplate

object CommandConsoleFactory {

    fun createRestCommandConsole(restTemplate: RestTemplate, port: Int): CommandConsole {
        val baseUrl = "http://localhost:$port"
        val playerService: PlayerService = HttpPlayerServiceImpl(restTemplate, baseUrl)
        val battleService: BattleService = HttpBattleServiceImpl(restTemplate, baseUrl)
        val fleetService: FleetService = HttpFleetServiceImpl(restTemplate, baseUrl)
        val shipService: ShipService = HttpShipServiceImpl(restTemplate, baseUrl)
        val shotService: ShotService = HttpShotServiceImpl(restTemplate, baseUrl)

        return CommandConsoleImpl(playerService, battleService, fleetService, shipService, shotService)
    }

    fun createUseCaseCommandConsole(appContext: ApplicationContext): CommandConsole {
        val playerService: PlayerService =
            UcPlayerServiceImpl(appContext.getBean(PlayerUseCaseManager::class.java))
        val battleService: BattleService =
            UcBattleServiceImpl(appContext.getBean(BattleUseCaseManager::class.java))
        val fleetService: FleetService =
            UcFleetServiceImpl(appContext.getBean(FleetUseCaseManager::class.java))
        val shipService: ShipService = UcShipServiceImpl(appContext.getBean(ShipUseCaseManager::class.java))
        val shotService: ShotService = UcShotServiceImpl(appContext.getBean(ShotUseCaseManager::class.java))

        return CommandConsoleImpl(playerService, battleService, fleetService, shipService, shotService)
    }

}