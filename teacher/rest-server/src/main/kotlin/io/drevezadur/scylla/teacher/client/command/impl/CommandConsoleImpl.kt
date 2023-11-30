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

package io.drevezadur.scylla.teacher.client.command.impl

import io.drevezadur.scylla.teacher.client.command.CommandConsole
import io.drevezadur.scylla.teacher.client.command.impl.service.*
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.service.model.*
import java.util.*

class CommandConsoleImpl(
    private val playerService: PlayerService,
    private val battleService: BattleService,
    private val fleetService: FleetService,
    private val shipService: ShipService,
    private val shotService: ShotService
) : CommandConsole {

    override fun createPlayer(name: String): UUID {
        return playerService.create(name)
    }

    override fun deletePlayer(id: UUID) {
        playerService.delete(id)
    }

    override fun getAllPlayers(): List<PlayerPojo> {
        return playerService.getAll()
    }

    override fun findPlayerById(id: UUID): PlayerPojo? {
        return playerService.findById(id)
    }

    override fun getAllBattles(): List<BattlePojo> {
        return battleService.getAll()
    }

    override fun findBattleById(battleId: UUID): BattlePojo? {
        return battleService.findById(battleId)
    }

    override fun createBattle(body: BattleCreationPojo): UUID {
        return battleService.create(body)
    }

    override fun deleteBattle(battleId: UUID) {
        return battleService.delete(battleId)
    }

    override fun findFleetById(battleId: UUID, playerId: UUID): FleetPojo? {
        return fleetService.findById(battleId, playerId)
    }

    override fun deployShip(battleId: UUID, playerId: UUID, content: ShipDeploymentBody) {
        return shipService.deploy(battleId, playerId, content)
    }

    override fun getAllShipsInFleet(battleId: UUID, playerId: UUID): List<ShipPojo> {
        return shipService.getAllInFleet(battleId, playerId)
    }

    override fun findShipById(battleId: UUID, playerId: UUID, type: ShipType): ShipPojo? {
        return shipService.findById(battleId, playerId, type)
    }

    override fun fireFromFleet(battleId: UUID, playerId: UUID, location: Location): ShotReportPojo {
        return shotService.shotFromFleetAt(battleId, playerId, location)
    }
}