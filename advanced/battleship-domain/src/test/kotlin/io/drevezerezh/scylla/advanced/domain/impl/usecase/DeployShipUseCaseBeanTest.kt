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
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.ship.*
import io.drevezerezh.scylla.advanced.domain.data.BattleTestData.BATTLE_ID
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.A0
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.A9
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.B0
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.JANE
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.JOHN
import io.drevezerezh.scylla.advanced.domain.data.TimeTestData.T1
import io.drevezerezh.scylla.advanced.domain.data.UnknownTestData.UNKNOWN_ID
import io.drevezerezh.scylla.advanced.domain.impl.IdProvider
import io.drevezerezh.scylla.advanced.domain.impl.TimeProvider
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.FIRST
import io.drevezerezh.scylla.advanced.lang.GridOrientation.COLUMN
import io.drevezerezh.scylla.advanced.lang.GridOrientation.ROW
import io.drevezerezh.scylla.advanced.lang.ShipType.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class DeployShipUseCaseBeanTest {

    @MockK
    lateinit var idProvider: IdProvider

    @MockK
    lateinit var timeProvider: TimeProvider


    private lateinit var context: UseCaseContext
    private lateinit var deployUseCase: DeployShipUseCase

    @BeforeEach
    fun setUp() {
        context = UseCaseContext(idProvider, timeProvider)
        deployUseCase = context.useCaseFactory.createDeployShipUseCase()

        context.players(JOHN, JANE)
            .battles(Battle(BATTLE_ID, T1, null, null, JOHN.id, JANE.id))
    }

    @Test
    fun `deployShip shall fail when battle does not exist`() {
        val deployment = ShipDeployment(UNKNOWN_ID, FIRST, SUBMARINE, A0, ROW)

        assertThatThrownBy { deployUseCase.deployShip(deployment) }
            .isInstanceOf(BattleNotFoundException::class.java)
            .extracting("battleId")
            .isEqualTo(UNKNOWN_ID)
    }

    @Test
    fun `deployShip shall fail when ship is out of grid`() {
        val deployment = ShipDeployment(BATTLE_ID, FIRST, SUBMARINE, A9, COLUMN)

        assertThatThrownBy { deployUseCase.deployShip(deployment) }
            .isInstanceOf(ShipOutOfGridException::class.java)
            .extracting("shipType", "location", "orientation")
            .containsOnly(SUBMARINE, A9, COLUMN)
    }

    @Test
    fun `deployShip shall fail when ship type is already deployed`() {
        context.ships(Ship(BATTLE_ID, FIRST, CRUISER, A0, ROW))

        val deployment = ShipDeployment(BATTLE_ID, FIRST, CRUISER, A9, ROW)

        assertThatThrownBy { deployUseCase.deployShip(deployment) }
            .isInstanceOf(ShipAlreadyDeployedException::class.java)
            .extracting("shipId")
            .isEqualTo(ShipId(BATTLE_ID, FIRST, CRUISER))
    }

    @Test
    fun `deployShip shall fail when ship override another ship`() {
        context.ships(Ship(BATTLE_ID, FIRST, SUBMARINE, A0, ROW))

        val deployment = ShipDeployment(BATTLE_ID, FIRST, CRUISER, B0, ROW)

        assertThatThrownBy { deployUseCase.deployShip(deployment) }
            .isInstanceOf(ShipOverlapException::class.java)
            .extracting("shipType", "overlappedShipType")
            .containsOnly(SUBMARINE, CRUISER)
    }

    @Test
    fun `deployShip shall deploy a new ship`() {
        context.ships(Ship(BATTLE_ID, FIRST, SUBMARINE, A0, ROW))

        val deployment = ShipDeployment(BATTLE_ID, FIRST, DESTROYER, A9, ROW)

        deployUseCase.deployShip(deployment)

        val newShipId = ShipId(BATTLE_ID, FIRST, DESTROYER)
        assertThat(context.shipStore.contains(newShipId))
            .isTrue()
    }
}