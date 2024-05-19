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
import io.drevezerezh.scylla.advanced.domain.api.fleet.Fleet
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.player.Player
import io.drevezerezh.scylla.advanced.domain.api.ship.Ship
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipId
import io.drevezerezh.scylla.advanced.domain.api.usecase.BattleUseCaseManager
import io.drevezerezh.scylla.advanced.domain.api.usecase.PlayerUseCaseManager
import io.drevezerezh.scylla.advanced.domain.impl.*
import io.drevezerezh.scylla.advanced.domain.spi.*
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.ShipStatus
import io.drevezerezh.scylla.advanced.lang.ShipType
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.domain.ScenarioRunner
import kotlin.test.fail

class UseCaseContext(
    idProvider: IdProvider = DefaultIdProvider(),
    timeProvider: TimeProvider = DefaultTimeProvider(),

    val playerStore: PlayerStore = PlayerStoreStub(),
    val battleStore: BattleStore = BattleStoreStub(),
    val fleetStore: FleetStore = FleetStoreStub(),
    val shipStore: ShipStore = ShipStoreStub(),

    playerManager: PlayerManager = PlayerManagerBean(idProvider, playerStore),
    val battleManager: BattleManager = BattleManagerBean(idProvider, battleStore, timeProvider, playerStore),
    fleetManager: FleetManager = FleetManagerBean(fleetStore),
    shipManager: ShipManager = ShipManagerBean(shipStore),

    val useCaseFactory: UseCaseFactory = UseCaseFactoryBean(
        playerManager,
        battleManager,
        fleetManager,
        shipManager,
        timeProvider
    ),

    val playerUseCaseManager: PlayerUseCaseManager = PlayerUseCaseManagerBean(playerManager),
    val battleUseCaseManager: BattleUseCaseManager = BattleUseCaseManagerBean(
        battleManager,
        fleetManager,
        useCaseFactory
    ),

    private val scenarioRunner: ScenarioRunner = ScenarioRunner(
        playerUseCaseManager,
        battleUseCaseManager
    )
) {

    fun players(vararg players: Player): UseCaseContext {
        playerStore.saveAll(*players)
        return this
    }

    fun battles(vararg battles: Battle): UseCaseContext {
        battleStore.saveAll(*battles)
        return this
    }

    fun ships(vararg ships: Ship): UseCaseContext {
        shipStore.saveAll(*ships)
        return this
    }

    fun scenario(scenario: String): UseCaseContext {
        scenarioRunner.run(scenario)
        return this
    }

    fun isStored(battle: Battle): Boolean {
        if (battleStore.contains(battle.id))
            return battleStore.getById(battle.id) == battle
        return false
    }

    fun firstBattle(): Battle {
        return battleStore.getAll().first()
    }

    fun ship(player: BattlePlayer, shipType: ShipType): Ship {
        val shipId = ShipId(firstBattle().id, player, shipType)
        return shipStore.getById(shipId)
    }

    fun fleet(player: BattlePlayer): Fleet {
        val fleetId = FleetId(firstBattle().id, player)
        return fleetStore.getById(fleetId)
    }

    fun hasPlayer(name: String): Boolean {
        return playerStore.containsName(name)
    }


    fun player(name: String): Player {
        return playerStore.getByName(name)
    }

    fun shipsHaveStatus(player: BattlePlayer, expectedStatus: ShipStatus, vararg shipTypes: ShipType) {
        val selectedShipTypes: Set<ShipType> = setOf(*shipTypes)
        val fleetId = FleetId(firstBattle().id, player)
        shipStore.getFleet(fleetId)
            .filter { selectedShipTypes.contains(it.type) }
            .forEach {
                if (it.status != expectedStatus)
                    fail("Ship ${it.type} has status ${it.status} instead of $expectedStatus")
            }
    }
}