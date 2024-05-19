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

package io.drevezerezh.scylla.advanced.domain.impl

import io.drevezerezh.scylla.advanced.domain.api.battle.Battle
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleCreation
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleUpdate
import io.drevezerezh.scylla.advanced.domain.api.player.NamedPlayerNotFoundException
import io.drevezerezh.scylla.advanced.domain.data.BattleTestData.BATTLE_ID
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.JANE
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.JOHN
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.WALTER
import io.drevezerezh.scylla.advanced.domain.data.TimeTestData.T1
import io.drevezerezh.scylla.advanced.domain.data.TimeTestData.T2
import io.drevezerezh.scylla.advanced.domain.impl.usecase.UseCaseContext
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.BattleStatus
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class BattleManagerBeanTest {

    @MockK
    private lateinit var idProvider: IdProvider

    @MockK
    private lateinit var timeProvider: TimeProvider

    private lateinit var battleManager: BattleManager

    private lateinit var context : UseCaseContext

    @BeforeEach
    fun setUp() {
        context = UseCaseContext(idProvider, timeProvider)

        battleManager= context.battleManager
    }


    @Test
    fun `createBattle shall fail when player ids are equal`() {
        assertThatThrownBy {
            battleManager.createBattle(BattleCreation(JOHN.name, JOHN.name))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }


    @Test
    fun `createBattle shall fail when a player id does not match an existing player`() {
        context.players(JOHN)

        assertThatThrownBy {
            battleManager.createBattle(BattleCreation(JOHN.name, WALTER.name))
        }.isInstanceOf(NamedPlayerNotFoundException::class.java)
            .extracting("playerName")
            .isEqualTo(WALTER.name)
    }


    @Test
    fun `createBattle shall generate a default creation date when field is null`() {
        context.players(JOHN, JANE)
        every { timeProvider.nowAsLocalDateTime() } returns BATTLE_1.creation
        every { idProvider.createId() } returns BATTLE_1.id


        assertThat(battleManager.createBattle(BattleCreation(JOHN.name, JANE.name)))
            .isEqualTo(BATTLE_1)

        assertThat(context.isStored(BATTLE_1))
            .isTrue()


        verify { timeProvider.nowAsLocalDateTime() }
        verify { idProvider.createId() }
    }


    @Test
    fun `createBattle shall accept the creation date when field is not null`() {
        context.players(JOHN, JANE)
        every { idProvider.createId() } returns BATTLE_1.id

        assertThat(battleManager.createBattle(BattleCreation(JOHN.name, JANE.name, BATTLE_1.creation)))
            .isEqualTo(BATTLE_1)

        assertThat(context.isStored(BATTLE_1))
            .isTrue()

        verify { idProvider.createId() }
    }


    @Test
    fun `containsBattle shall return true when the battle exists`() {
        context.battles(BATTLE_1)

        assertThat(battleManager.containsBattle(BATTLE_1.id))
            .isTrue()
    }


    @Test
    fun `containsBattle shall return false when the battle does not exist`() {
        assertThat(battleManager.containsBattle(BATTLE_ID))
            .isFalse()
    }


    @Test
    fun `getBattleById shall fail when the battle does not exist`() {
        assertThatThrownBy {
            battleManager.getBattleById(BATTLE_1.id)
        }.isInstanceOf(BattleNotFoundException::class.java)
    }


    @Test
    fun `getBattleById shall return the battle when it exists`() {
        context.battles(BATTLE_1)

        assertThat(battleManager.getBattleById(BATTLE_1.id))
            .isEqualTo(BATTLE_1)
    }


    @Test
    fun `getAllBattles shall return empty list when no battles`() {
        assertThat(battleManager.getAllBattles())
            .isEmpty()
    }


    @Test
    fun `getAllBattles shall return the existing battles`() {
        context.battles(BATTLE_1, BATTLE_2)

        assertThat(battleManager.getAllBattles())
            .containsOnly(BATTLE_1, BATTLE_2)
    }


    @Test
    fun `update shall fail when battle does not exist`() {
        val update = BattleUpdate().status(BattleStatus.FIGHTING)
        assertThatThrownBy { battleManager.update(BATTLE_ID, update) }
            .isInstanceOf(BattleNotFoundException::class.java)
    }


    @ParameterizedTest
    @CsvSource(value = ["DEPLOYMENT, FIGHTING",  "FIGHTING, FINISHED"])
    fun `update shall change the status`(currentStatus: BattleStatus, expectedStatus: BattleStatus) {
        val currentBattle = BATTLE_2.copy(status = currentStatus)
        val expectedBattle = BATTLE_2.copy(status = expectedStatus)
        context.battles(currentBattle)

        val update = BattleUpdate().status(expectedStatus)
        assertThat(battleManager.update(currentBattle.id, update))
            .extracting("id", "status")
            .containsOnly(expectedBattle.id, expectedStatus)

        assertThat(context.isStored(expectedBattle))
            .isTrue()
    }


    @Test
    fun `update shall update startTime if not null`() {
        context.battles(BATTLE_1)
        val expectedBattle = BATTLE_1.copy(startTime = T2)

        val update = BattleUpdate().startTime(T2)
        assertThat(battleManager.update(BATTLE_1.id, update))
            .extracting("startTime")
            .isEqualTo(expectedBattle.startTime)

        assertThat(context.isStored(expectedBattle))
            .isTrue()
    }


    @Test
    fun `update shall update stopTime if not null`() {
        context.battles(BATTLE_1)
        val expectedBattle = BATTLE_1.copy(stopTime = T2)

        val update = BattleUpdate().stopTime(T2)
        assertThat(battleManager.update(BATTLE_1.id, update))
            .extracting("stopTime")
            .isEqualTo(T2)

        assertThat(context.isStored(expectedBattle))
            .isTrue()
    }


    @Test
    fun `update shall update nextPlayer if not null`() {
        context.battles(BATTLE_1)
        val expectedBattle = BATTLE_1.copy(nextPlayer = BattlePlayer.SECOND)

        val update = BattleUpdate().nextPlayer(BattlePlayer.SECOND)
        assertThat(battleManager.update(BATTLE_1.id, update))
            .isEqualTo(expectedBattle)


        assertThat(context.isStored(expectedBattle))
            .isTrue()
    }


    @Test
    fun `update shall upgrade turn when required`() {
        val initialBattle = BATTLE_1.copy(turn = 3)
        val expectedBattle = BATTLE_1.copy(turn = 4)

        context.battles(initialBattle)

        val update = BattleUpdate().incrementTurn(true)
        assertThat(battleManager.update(BATTLE_1.id, update))
            .isEqualTo(expectedBattle)

        assertThat(context.isStored(expectedBattle))
            .isTrue()
    }


    @Test
    fun `deleteBattle shall do nothing when the battle does not exist`() {
        assertThat(battleManager.deleteBattle(BATTLE_ID))
            .isFalse()
    }


    @Test
    fun `deleteBattle shall delete the battle when it exists`() {
        context.battles(BATTLE_1)

        assertThat(battleManager.deleteBattle(BATTLE_1.id))
            .isTrue()

        assertThat(context.isStored(BATTLE_1))
            .isFalse()
    }

    companion object {
        private val BATTLE_1 = Battle(
            id = "battle-01",
            creation = T1,
            player1Id = JOHN.id,
            player2Id = JANE.id
        )
        private val BATTLE_2 = Battle(
            id = "battle-02",
            creation = T2,
            player1Id = WALTER.id,
            player2Id = JANE.id,
            status = BattleStatus.FIGHTING
        )
    }
}