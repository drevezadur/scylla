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

import io.drevezerezh.scylla.advanced.domain.impl.*
import org.springframework.stereotype.Service

@Service
class UseCaseFactoryBean(
    private val playerManager: PlayerManager,
    private val battleManager: BattleManager,
    private val fleetManager: FleetManager,
    private val shipManager: ShipManager,
    private val timeProvider: TimeProvider
) : UseCaseFactory{

    override fun createDeployShipUseCase(): DeployShipUseCase {
        return DeployShipUseCaseBean(playerManager, battleManager, fleetManager, shipManager)
    }

    override fun createShootingUseCase(): ShootingUseCase {
        return ShootingUseCaseBean(battleManager, fleetManager, shipManager, timeProvider)
    }
}