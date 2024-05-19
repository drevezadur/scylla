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
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.player.NamedPlayerNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.usecase.BattleUseCaseManager
import io.drevezerezh.scylla.advanced.domain.data.BattleTestData.BATTLE_ID
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.JANE
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.JOHN
import io.drevezerezh.scylla.advanced.domain.data.TimeTestData.T1
import io.drevezerezh.scylla.advanced.domain.data.TimeTestData.T2
import io.drevezerezh.scylla.advanced.domain.data.TimeTestData.T3
import io.drevezerezh.scylla.advanced.domain.data.TimeTestData.T4
import io.drevezerezh.scylla.advanced.domain.impl.IdProvider
import io.drevezerezh.scylla.advanced.domain.impl.TimeProvider
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.SECOND
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

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class BattleUseCaseManagerBeanTest {

    @MockK
    lateinit var idProvider: IdProvider

    @MockK
    lateinit var timeProvider: TimeProvider

    private lateinit var context: UseCaseContext
    private lateinit var battleUseCaseManager: BattleUseCaseManager

    @BeforeEach
    fun setUp() {
        context = UseCaseContext(idProvider, timeProvider)
        battleUseCaseManager = context.battleUseCaseManager
    }


    @Test
    fun `create shall fail when first player does not exist`() {
        context.players(JANE)

        val creation = BattleCreation(JOHN.name, JANE.name)
        assertThatThrownBy { battleUseCaseManager.create(creation) }
            .isInstanceOf(NamedPlayerNotFoundException::class.java)
            .extracting("playerName")
            .isEqualTo(JOHN.name)
    }


    @Test
    fun `create shall fail when second player does not exist`() {
        context.players(JOHN)

        val creation = BattleCreation(JOHN.name, JANE.name)
        assertThatThrownBy { battleUseCaseManager.create(creation) }
            .isInstanceOf(NamedPlayerNotFoundException::class.java)
            .extracting("playerName")
            .isEqualTo(JANE.name)
    }


    @Test
    fun `create shall create a new battle with specified time`() {
        every { idProvider.createId() } returns BATTLE_ID
        context.players(JOHN, JANE)


        val createdBattle = Battle(BATTLE_ID, T1, player1Id = JOHN.id, player2Id = JANE.id)

        assertThat(
            battleUseCaseManager.create(BattleCreation(JOHN.name, JANE.name, T1))
        ).isEqualTo(createdBattle)

        verify { idProvider.createId() }
    }


    @Test
    fun `create shall create a new battle with time not specified`() {
        context.players(JOHN, JANE)

        every { idProvider.createId() } returns BATTLE_ID
        every { timeProvider.nowAsLocalDateTime() } returns T2

        val expectedBattle = Battle(BATTLE_ID, T2, player1Id = JOHN.id, player2Id = JANE.id)

        assertThat(
            battleUseCaseManager.create(BattleCreation(JOHN.name, JANE.name))
        ).isEqualTo(
            expectedBattle
        )

        assertThat(context.isStored(expectedBattle))
            .isTrue()


        verify { idProvider.createId() }
        verify { timeProvider.nowAsLocalDateTime() }
    }


    @Test
    fun `getById shall fail when battle does not exist`() {
        assertThatThrownBy {
            battleUseCaseManager.getById(BATTLE_ID)
        }.isInstanceOf(BattleNotFoundException::class.java)
            .extracting("battleId")
            .isEqualTo(BATTLE_ID)
    }

    @Test
    fun `getById shall success when battle exists`() {
        context.battles(BATTLE_1)

        assertThat(battleUseCaseManager.getById(BATTLE_1.id))
            .isEqualTo(BATTLE_1)
    }


    @Test
    fun `delete shall fail when battle does not exist`() {
        assertThat(battleUseCaseManager.delete(BATTLE_ID))
            .isFalse()
    }

    @Test
    fun delete() {
        context.battles(BATTLE_1)

        assertThat(battleUseCaseManager.delete(BATTLE_1.id))
            .isTrue()
    }


    @Test
    fun `getAll shall return empty list when no battle`() {
        assertThat(battleUseCaseManager.getAll())
            .isEmpty()
    }


    @Test
    fun getAll() {
        context.battles(BATTLE_1, BATTLE_2)

        assertThat(battleUseCaseManager.getAll())
            .containsOnly(BATTLE_1, BATTLE_2)
    }

    companion object {
        private val BATTLE_1 = Battle("b1", T1, T2, null, "p1", "p2", SECOND, BattleStatus.FIGHTING)
        private val BATTLE_2 = Battle("b2", T2, T3, T4, "p3", "p4", status = BattleStatus.FINISHED)
    }
}