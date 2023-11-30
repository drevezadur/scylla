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

import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.service.model.*
import java.util.*

interface CommandConsole {

    fun createPlayer(name: String): UUID

    fun deletePlayer(id: UUID)

    fun getAllPlayers(): List<PlayerPojo>

    fun findPlayerById(id: UUID): PlayerPojo?

    fun getAllBattles(): List<BattlePojo>

    fun findBattleById(battleId: UUID): BattlePojo?

    fun createBattle(body: BattleCreationPojo): UUID

    fun deleteBattle(battleId: UUID)


    fun findFleetById(battleId: UUID, playerId: UUID): FleetPojo?

    fun fireFromFleet(battleId: UUID, playerId: UUID, location: Location): ShotReportPojo

    fun deployShip(battleId: UUID, playerId: UUID, content: ShipDeploymentBody)

    fun getAllShipsInFleet(battleId: UUID, playerId: UUID): List<ShipPojo>

    fun findShipById(battleId: UUID, playerId: UUID, type: ShipType): ShipPojo?

}