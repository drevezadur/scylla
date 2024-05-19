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

import io.drevezerezh.scylla.advanced.domain.api.battle.BattleNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.battle.IllegalBattleStatusException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipDeployment
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipAlreadyDeployedException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipOutOfGridException
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipOverlapException


/**
 * Use case for deploying a ship
 */
interface DeployShipUseCase {

    /**
     * Deploy a ship in a fleet
     * @param deployment all information about the ship to deploy
     */
    @Throws(
        BattleNotFoundException::class,
        IllegalBattleStatusException::class,
        PlayerNotFoundException::class,
        ShipAlreadyDeployedException::class,
        ShipOutOfGridException::class,
        ShipOverlapException::class
    )
    fun deployShip(deployment: ShipDeployment)
}