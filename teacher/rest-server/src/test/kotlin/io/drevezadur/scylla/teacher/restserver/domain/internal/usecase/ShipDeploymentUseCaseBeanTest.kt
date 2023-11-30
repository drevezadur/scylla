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

package io.drevezadur.scylla.teacher.restserver.domain.internal.usecase

import io.drevezadur.scylla.teacher.restserver.domain.usecase.ShipDeploymentUseCase
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.BattleStore
import io.drevezadur.scylla.teacher.restserver.domain.store.FleetStore
import io.drevezadur.scylla.teacher.restserver.domain.store.ShipStore
import io.drevezadur.scylla.teacher.restserver.lang.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*


@ExtendWith(MockKExtension::class)
class ShipDeploymentUseCaseBeanTest {

    @MockK
    lateinit var battleStore: BattleStore

    @MockK
    lateinit var fleetStore: FleetStore

    @MockK
    lateinit var shipStore: ShipStore


    private lateinit var shipDeploymentUseCase: ShipDeploymentUseCase

    @BeforeEach
    fun setUp() {
        shipDeploymentUseCase = ShipDeploymentUseCaseBean(battleStore, fleetStore, shipStore)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `shipDeployed() shall do nothing when fleet is not complete`() {
        val ships = listOf(
            ShipDEntity(
                battleId,
                player1Id,
                ShipType.SUBMARINE,
                Location(3, 3),
                GridOrientation.ROW
            )
        )

        every { shipStore.getAllShipsInFleet(battleId, player1Id) }
            .returns(ships)


        shipDeploymentUseCase.shipDeployed(battleId, player1Id, ShipType.SUBMARINE)


        verify(exactly = 1) { shipStore.getAllShipsInFleet(battleId, player1Id) }
    }

    @Test
    fun `shipDeployed() shall mark fleet deployed and nothing more when other fleet not deployed`() {
        val ships = allShips()

        val fleet1 = FleetDEntity(
            battleId,
            player1Id,
            FleetStatus.NOT_DEPLOYED
        )
        val fleet2 = FleetDEntity(
            battleId,
            player1Id,
            FleetStatus.NOT_DEPLOYED
        )

        every { shipStore.getAllShipsInFleet(battleId, player1Id) }
            .returns(ships)

        every { fleetStore.getFleetByBattleAndPlayer(battleId, player1Id) }
            .returns(fleet1)

        every { fleetStore.getAllFleets(battleId) }
            .returns(listOf(fleet1, fleet2))

        every { fleetStore.save(fleet1) }
            .answers { Any() }


        shipDeploymentUseCase.shipDeployed(battleId, player1Id, ShipType.SUBMARINE)


        assertThat(
            fleet1.status
        ).isEqualTo(FleetStatus.UNHARMED)

        verify(exactly = 1) { shipStore.getAllShipsInFleet(battleId, player1Id) }
        verify(exactly = 1) { fleetStore.getFleetByBattleAndPlayer(battleId, player1Id) }
        verify(exactly = 1) { fleetStore.getAllFleets(battleId) }
    }

    @Test
    fun `shipDeployed() shall mark fleet deployed and mark battle as deployed`() {
        val ships = allShips()

        val fleet1 = FleetDEntity(battleId, player1Id, FleetStatus.NOT_DEPLOYED)

        val fleet2 = FleetDEntity(battleId, player1Id, FleetStatus.UNHARMED)

        val battle = BattleDEntity(battleId, player1Id, player2Id, BattleStatus.DEPLOYMENT, shooterPlayerId = player1Id)

        every { shipStore.getAllShipsInFleet(battleId, player1Id) }.returns(ships)
        every { fleetStore.getFleetByBattleAndPlayer(battleId, player1Id) }.returns(fleet1)
        every { fleetStore.getAllFleets(battleId) }.returns(listOf(fleet1, fleet2))
        every { fleetStore.save(fleet1) }.answers { Any() }
        every { battleStore.getById(battleId) }.returns(battle)
        every { battleStore.save(battle) }.answers { Any() }


        shipDeploymentUseCase.shipDeployed(battleId, player1Id, ShipType.SUBMARINE)


        assertThat(fleet1.status)
            .isEqualTo(FleetStatus.UNHARMED)

        assertThat(battle.status)
            .isEqualTo(BattleStatus.DEPLOYED)

        verify(exactly = 1) { shipStore.getAllShipsInFleet(battleId, player1Id) }
        verify(exactly = 1) { fleetStore.getFleetByBattleAndPlayer(battleId, player1Id) }
        verify(exactly = 1) { fleetStore.getAllFleets(battleId) }
        verify(exactly = 1) { battleStore.getById(battleId) }
        verify(exactly = 1) { battleStore.save(battle) }
    }


    companion object {
        val battleId: UUID = UUID.randomUUID()
        val player1Id: UUID = UUID.randomUUID()
        val player2Id: UUID = UUID.randomUUID()

        fun allShips(): List<ShipDEntity> {
            return listOf(
                ShipDEntity(
                    battleId,
                    player1Id,
                    ShipType.CRUISER,
                    Location(0, 0),
                    GridOrientation.ROW
                ),
                ShipDEntity(
                    battleId,
                    player1Id,
                    ShipType.SUBMARINE,
                    Location(0, 1),
                    GridOrientation.ROW
                ),
                ShipDEntity(
                    battleId,
                    player1Id,
                    ShipType.BATTLESHIP,
                    Location(0, 2),
                    GridOrientation.ROW
                ),
                ShipDEntity(
                    battleId,
                    player1Id,
                    ShipType.CARRIER,
                    Location(0, 3),
                    GridOrientation.ROW
                ),
                ShipDEntity(
                    battleId,
                    player1Id,
                    ShipType.DESTROYER,
                    Location(0, 4),
                    GridOrientation.ROW
                ),
            )
        }
    }
}