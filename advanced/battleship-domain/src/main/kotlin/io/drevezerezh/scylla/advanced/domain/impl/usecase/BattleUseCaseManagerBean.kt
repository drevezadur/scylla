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

import io.drevezerezh.scylla.advanced.domain.api.battle.*
import io.drevezerezh.scylla.advanced.domain.api.fleet.Fleet
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipDeployment
import io.drevezerezh.scylla.advanced.domain.api.shot.Shot
import io.drevezerezh.scylla.advanced.domain.api.shot.ShotReport
import io.drevezerezh.scylla.advanced.domain.api.usecase.BattleUseCaseManager
import io.drevezerezh.scylla.advanced.domain.impl.BattleManager
import io.drevezerezh.scylla.advanced.domain.impl.FleetManager
import io.drevezerezh.scylla.advanced.domain.impl.TimeProvider
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.BattleStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
internal class BattleUseCaseManagerBean(
    private val battleManager: BattleManager,
    private val fleetManager: FleetManager,
    private val timeProvider: TimeProvider,
    private val useCaseFactory: UseCaseFactory
    ) : BattleUseCaseManager {

    private val logger: Logger = LoggerFactory.getLogger(BattleUseCaseManager::class.java)

    override fun create(creation: BattleCreation): Battle {
        logger.info("create battle")
        val battle = battleManager.createBattle(creation)
        fleetManager.createFleet(FleetId(battle.id, BattlePlayer.FIRST))
        fleetManager.createFleet(FleetId(battle.id, BattlePlayer.SECOND))
        return battle
    }

    override fun getById(battleId: String): Battle {
        logger.info("get battle $battleId")
        return battleManager.getBattleById(battleId)
    }

    override fun delete(battleId: String): Boolean {
        logger.info("delete battle $battleId")
        return battleManager.deleteBattle(battleId)
    }

    override fun getAll(): List<Battle> {
        logger.info("get all battles")
        return battleManager.getAllBattles()
    }

    override fun deployShip(deployment: ShipDeployment) {
        logger.info("deploy ship. Battle ${deployment.battleId}, Player ${deployment.player}, Ship ${deployment.shipType}")
        val useCase = useCaseFactory.createDeployShipUseCase()
        useCase.deployShip(deployment)
    }


    override fun shot(shot: Shot): ShotReport {
        logger.info("shot. Battle ${shot.battleId}, Player ${shot.shootingPlayer}, Location ${shot.targetLocation}")
        val useCase = useCaseFactory.createShootingUseCase()
        return useCase.shoot(shot)
    }


    override fun getFleet(fleetId: FleetId): Fleet {
        logger.info("get fleet. Battle ${fleetId.battleId}, Player ${fleetId.player}")
        return fleetManager.getFleet(fleetId)
    }

    override fun endBattle(battleId: String): Boolean {
        logger.info("end battle $battleId")
        val battle = battleManager.getBattleById(battleId)

        if (battle.status == BattleStatus.FINISHED) {
            logger.warn("Battle $battleId already finished!")
            return false
        }

        val update = BattleUpdate().status(BattleStatus.FINISHED).stopTime(timeProvider.nowAsLocalDateTime())
        battleManager.update(battleId, update)
        return true
    }
}