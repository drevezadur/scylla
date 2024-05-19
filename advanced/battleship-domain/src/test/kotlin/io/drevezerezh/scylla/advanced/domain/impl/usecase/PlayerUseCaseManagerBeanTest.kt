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

import io.drevezerezh.scylla.advanced.domain.api.player.*
import io.drevezerezh.scylla.advanced.domain.api.usecase.PlayerUseCaseManager
import io.drevezerezh.scylla.advanced.domain.impl.IdProvider
import io.drevezerezh.scylla.advanced.domain.impl.TimeProvider
import io.drevezerezh.scylla.advanced.domain.spi.PlayerStore
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.JOHN
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.JANE
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
class PlayerUseCaseManagerBeanTest {

    @MockK
    lateinit var idProvider: IdProvider

    @MockK
    lateinit var timeProvider: TimeProvider


    private lateinit var context: UseCaseContext
    private lateinit var playerStore: PlayerStore
    private lateinit var playerUseCase: PlayerUseCaseManager


    @BeforeEach
    fun setUp() {
        context = UseCaseContext(idProvider, timeProvider)
        playerStore = context.playerStore
        playerUseCase = context.playerUseCaseManager
    }


    @Test
    fun `create shall fail when name is empty`() {
        assertThatThrownBy {

            playerUseCase.create(PlayerCreation(""))

        }.isInstanceOf(InvalidPlayerAttributeException::class.java)
            .extracting("attributeNames")
            .isEqualTo(setOf("name"))
    }


    @Test
    fun `create shall fail when name already exists`() {
        every { idProvider.createId() }.returns("1")
        context.players(JOHN)

        assertThatThrownBy {
            playerUseCase.create(PlayerCreation("John"))
        }.isInstanceOf(PlayerAlreadyExistException::class.java)

        verify { idProvider.createId() }
    }


    @Test
    fun `create shall succeed when name not exists`() {
        every { idProvider.createId() }.returns(JOHN.id)

        assertThat(
            playerUseCase.create(PlayerCreation("John"))
        ).isEqualTo(JOHN)

        assertThat(playerStore.contains(JOHN.id))
            .isTrue()

        verify { idProvider.createId() }
    }


    @Test
    fun `update shall fail when player does not exists`() {
        assertThatThrownBy {
            playerUseCase.update("1", PlayerUpdate("Jane"))
        }.isInstanceOf(PlayerNotFoundException::class.java)
    }


    @Test
    fun `update shall fail when name exists`() {
        context.players(JOHN, JANE)

        assertThatThrownBy {
            playerUseCase.update(JOHN.id, PlayerUpdate(JANE.name))
        }.isInstanceOf(PlayerAlreadyExistException::class.java)
    }


    @Test
    fun `update shall success when name not exists`() {
        context.players(JOHN)

        assertThat(
            playerUseCase.update(JOHN.id, PlayerUpdate("Jane"))
        ).isEqualTo(Player(JOHN.id, "Jane"))

        assertThat(playerStore.getById(JOHN.id))
            .isEqualTo(Player(JOHN.id,"Jane"))
    }


    @Test
    fun `delete shall do nothing when player does not exist`() {
        assertThat(playerUseCase.delete("1"))
            .isFalse
    }

    @Test
    fun `delete shall succeed when player exists`() {
        context.players(JOHN)

        assertThat(playerUseCase.delete(JOHN.id))
            .isTrue

        assertThat(playerStore.getAll())
            .isEmpty()
    }


    @Test
    fun `getAll shall return empty list when no player`() {
        assertThat(playerUseCase.getAll())
            .isEmpty()
    }


    @Test
    fun `getAll shall return all players`() {
        context.players(JOHN, JANE)

        assertThat(playerUseCase.getAll())
            .containsOnly(JOHN, JANE)
    }


    @Test
    fun `getById shall fail when id not found`() {
        assertThatThrownBy {
            playerUseCase.getById(JOHN.id)
        }.isInstanceOf(PlayerNotFoundException::class.java)
    }


    @Test
    fun `getById shall success when id found`() {
        context.players(JOHN)

        assertThat(playerUseCase.getById(JOHN.id))
            .isEqualTo(JOHN)
    }
}