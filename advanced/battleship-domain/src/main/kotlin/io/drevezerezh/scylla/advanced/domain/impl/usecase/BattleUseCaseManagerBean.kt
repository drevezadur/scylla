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

package io.drevezerezh.scylla.advanced.domain.impl.usecase

import io.drevezerezh.scylla.advanced.domain.api.battle.Battle
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleCreation
import io.drevezerezh.scylla.advanced.domain.api.fleet.Fleet
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipDeployment
import io.drevezerezh.scylla.advanced.domain.api.shot.Shot
import io.drevezerezh.scylla.advanced.domain.api.shot.ShotReport
import io.drevezerezh.scylla.advanced.domain.api.usecase.BattleUseCaseManager
import io.drevezerezh.scylla.advanced.domain.impl.BattleManager
import io.drevezerezh.scylla.advanced.domain.impl.FleetManager
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
internal class BattleUseCaseManagerBean(
    private val battleManager: BattleManager,
    private val fleetManager: FleetManager,
    private val useCaseFactory: UseCaseFactory
) : BattleUseCaseManager {

    override fun create(creation: BattleCreation): Battle {
        LOGGER.info("create battle")
        val battle = battleManager.createBattle(creation)
        fleetManager.createFleet(FleetId(battle.id, BattlePlayer.FIRST))
        fleetManager.createFleet(FleetId(battle.id, BattlePlayer.SECOND))
        return battle
    }

    override fun getById(battleId: String): Battle {
        LOGGER.info("get battle $battleId")
        return battleManager.getBattleById(battleId)
    }

    override fun delete(battleId: String): Boolean {
        LOGGER.info("delete battle $battleId")
        return battleManager.deleteBattle(battleId)
    }

    override fun getAll(): List<Battle> {
        LOGGER.info("get all battles")
        return battleManager.getAllBattles()
    }

    override fun deployShip(deployment: ShipDeployment) {
        LOGGER.info("deploy ship. Battle ${deployment.battleId}, Player ${deployment.player}, Ship ${deployment.shipType}")
        val useCase = useCaseFactory.createDeployShipUseCase()
        useCase.deployShip(deployment)
    }


    override fun shot(shot: Shot): ShotReport {
        LOGGER.info("shot. Battle ${shot.battleId}, Player ${shot.shootingPlayer}, Location ${shot.targetLocation}")
        val useCase = useCaseFactory.createShootingUseCase()
        return useCase.shoot(shot)
    }


    override fun getFleet(fleetId: FleetId): Fleet {
        LOGGER.info("get fleet. Battle ${fleetId.battleId}, Player ${fleetId.player}")
        return fleetManager.getFleet(fleetId)
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(BattleUseCaseManager::class.java)
    }
}