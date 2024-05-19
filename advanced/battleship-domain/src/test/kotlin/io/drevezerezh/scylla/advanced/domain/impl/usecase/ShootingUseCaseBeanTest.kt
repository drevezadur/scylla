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
import io.drevezerezh.scylla.advanced.domain.api.battle.NotPlayerTurnException
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipId
import io.drevezerezh.scylla.advanced.domain.api.shot.Shot
import io.drevezerezh.scylla.advanced.domain.api.shot.ShotReport
import io.drevezerezh.scylla.advanced.lang.ShotResult
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.A0
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.A4
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.B0
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.B4
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.J9
import io.drevezerezh.scylla.advanced.domain.data.UnknownTestData.UNKNOWN_ID
import io.drevezerezh.scylla.advanced.lang.*
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.FIRST
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.SECOND
import io.drevezerezh.scylla.advanced.lang.ShipType.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ShootingUseCaseBeanTest {

    private lateinit var context: UseCaseContext
    private lateinit var shootUseCase: ShootingUseCase
    private lateinit var battleId: String

    @BeforeEach
    fun setUp() {
        context = UseCaseContext()
        shootUseCase = context.useCaseFactory.createShootingUseCase()
        context.scenario(
            """
            create player John
            create player Jane
            start battle with John and Jane

            John deploy Carrier at A0 on row
            John deploy Battleship at A1 on row
            John deploy Cruiser at A2 on row
            John deploy Submarine at A3 on row
            John deploy Destroyer at A4 on row

            Jane deploy Carrier at A0 on row
            Jane deploy Battleship at A1 on row
            Jane deploy Cruiser at A2 on row
            Jane deploy Submarine at A3 on row
            Jane deploy Destroyer at A4 on row
        """.trimIndent()
        )

        battleId = context.firstBattle().id
    }


    @Test
    fun `shoot shall fail when battle does not exist`() {
        assertThatThrownBy {
            shootUseCase.shoot(Shot(UNKNOWN_ID, FIRST, A0))
        }.isInstanceOf(BattleNotFoundException::class.java)
            .extracting("battleId")
            .isEqualTo(UNKNOWN_ID)
    }


    @Test
    fun `shoot shall fail when battle has deployment status`() {
        setBattleStatus(BattleStatus.DEPLOYMENT)

        assertThatThrownBy {
            shootUseCase.shoot(Shot(battleId, FIRST, A0))
        }.isInstanceOf(IllegalBattleStatusException::class.java)
            .extracting("battleId", "expectedStatus", "actualStatus")
            .containsExactly(battleId, BattleStatus.FIGHTING, BattleStatus.DEPLOYMENT)
    }


    @Test
    fun `shoot shall fail when battle has finished status`() {
        setBattleStatus(BattleStatus.FINISHED)

        assertThatThrownBy {
            shootUseCase.shoot(Shot(battleId, FIRST, A0))
        }.isInstanceOf(IllegalBattleStatusException::class.java)
            .extracting("battleId", "expectedStatus", "actualStatus")
            .containsExactly(battleId, BattleStatus.FIGHTING, BattleStatus.FINISHED)
    }


    private fun setBattleStatus(battleStatus: BattleStatus) {
        val updated = context.firstBattle().copy(status = battleStatus)
        context.battleStore.save(updated)
    }


    @Test
    fun `shoot shall fail when not player turn`() {
        assertThatThrownBy {
            shootUseCase.shoot(Shot(battleId, SECOND, A0))
        }.isInstanceOf(NotPlayerTurnException::class.java)
            .extracting("battleId", "badPlayer")
            .containsExactly(battleId, SECOND)
    }

    @Test
    fun `shoot shall support when shoot twice at the same location`() {
        setPlayerHits(FIRST, setOf(A0, J9))

        assertThat(shootUseCase.shoot(Shot(battleId, FIRST, A0)))
            .isEqualTo(ShotReport(ShotResult.ALREADY_SHOT))

        assertThat(shootUseCase.shoot(Shot(battleId, FIRST, J9)))
            .isEqualTo(ShotReport(ShotResult.ALREADY_SHOT))

        assertThat(context.firstBattle())
            .extracting("status")
            .isEqualTo(BattleStatus.FIGHTING)
    }

    private fun setPlayerHits(player: BattlePlayer, shots: Set<GridLocation>) {
        val fleetId = FleetId(battleId, player)
        val fleet = context.fleetStore.getById(fleetId)
        val updatedFleet = fleet.copy(shots = shots)
        context.fleetStore.save(updatedFleet)
    }


    @Test
    fun `shoot shall support when shot is missed`() {
        assertThat(shootUseCase.shoot(Shot(battleId, FIRST, J9)))
            .isEqualTo(ShotReport(ShotResult.MISSED))

        assertThat(context.fleet(SECOND))
            .extracting("status")
            .isEqualTo(FleetStatus.OPERATIONAL)

        assertThat(context.firstBattle())
            .extracting("status")
            .isEqualTo(BattleStatus.FIGHTING)
    }


    @Test
    fun `shoot shall support when shoot on unharmed ship`() {
        assertThat(shootUseCase.shoot(Shot(battleId, FIRST, A0)))
            .isEqualTo(ShotReport(ShotResult.HIT))

        assertThat(context.ship(SECOND, CARRIER))
            .extracting("status", "hits")
            .containsExactly(ShipStatus.DAMAGED, setOf(A0))

        assertThat(context.fleet(SECOND))
            .extracting("status")
            .isEqualTo(FleetStatus.DAMAGED)

        assertThat(context.firstBattle())
            .extracting("status")
            .isEqualTo(BattleStatus.FIGHTING)
    }


    @Test
    fun `shoot shall support when shoot on damaged ship `() {
        setShipHits(SECOND, CARRIER, A0)

        assertThat(shootUseCase.shoot(Shot(battleId, FIRST, B0)))
            .isEqualTo(ShotReport(ShotResult.HIT))

        assertThat(context.ship(SECOND, CARRIER))
            .extracting("status", "hits")
            .containsExactly(ShipStatus.DAMAGED, setOf(A0, B0))

        assertThat(context.fleet(SECOND))
            .extracting("status")
            .isEqualTo(FleetStatus.DAMAGED)

        assertThat(context.firstBattle())
            .extracting("status")
            .isEqualTo(BattleStatus.FIGHTING)
    }


    @Test
    fun `shoot shall support when shoot on damaged ship that sunk`() {
        setShipHits(SECOND, DESTROYER, A4)

        assertThat(shootUseCase.shoot(Shot(battleId, FIRST, B4)))
            .isEqualTo(ShotReport(ShotResult.SUNK))

        assertThat(context.ship(SECOND, DESTROYER))
            .extracting("status", "hits")
            .containsExactly(ShipStatus.SUNK, setOf(A4, B4))

        assertThat(context.fleet(SECOND))
            .extracting("status")
            .isEqualTo(FleetStatus.DAMAGED)

        assertThat(context.firstBattle())
            .extracting("status")
            .isEqualTo(BattleStatus.FIGHTING)
    }


    @Test
    fun `shoot shall support when shoot on last fleet ship that sunk`() {
        markAllSunk(SECOND, CARRIER, BATTLESHIP, CRUISER, SUBMARINE)
        setShipHits( SECOND, DESTROYER, A4)
        setFleetStatus( SECOND, FleetStatus.DAMAGED)


        assertThat(context.fleet(SECOND).status)
            .isEqualTo(FleetStatus.DAMAGED)

        assertThat(shootUseCase.shoot(Shot(battleId, FIRST, B4)))
            .isEqualTo(ShotReport(ShotResult.SUNK, true))

        assertThat(context.ship(SECOND, DESTROYER))
            .extracting("status", "hits")
            .containsExactly(ShipStatus.SUNK, setOf(A4, B4))

        assertThat(context.fleet(SECOND))
            .extracting("status")
            .isEqualTo(FleetStatus.DESTROYED)

        assertThat(context.firstBattle())
            .extracting("status")
            .isEqualTo(BattleStatus.FINISHED)
    }

    private fun markAllSunk(player: BattlePlayer, vararg shipTypes: ShipType) {
        shipTypes.forEach {
            markSunk(player, it)
        }
    }

    private fun markSunk(player: BattlePlayer, shipType: ShipType) {
        val shipId = ShipId(battleId, player, shipType)
        val ship = context.shipStore.getById(shipId)
        val updatedShip = ship.copy(hits = ship.locations.toSet())
        context.shipStore.save(updatedShip)
    }

    private fun setShipHits(player: BattlePlayer, shipType: ShipType, vararg  hits : GridLocation) {
        val ship = context.ship(player, shipType)
        val updatedShip = ship.copy(hits = hits.toSet())
        context.shipStore.save(updatedShip)
    }


    private fun setFleetStatus(player: BattlePlayer, status : FleetStatus) {
        val fleet = context.fleet(player)
        val updatedFleet = fleet.copy(status = status)
        context.fleetStore.save(updatedFleet)
    }
}