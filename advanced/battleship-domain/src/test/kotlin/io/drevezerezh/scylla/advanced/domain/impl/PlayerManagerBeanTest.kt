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

import io.drevezerezh.scylla.advanced.domain.api.player.*
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.JANE
import io.drevezerezh.scylla.advanced.domain.data.PlayerTestData.JOHN
import io.drevezerezh.scylla.advanced.domain.spi.PlayerStore
import io.drevezerezh.scylla.advanced.domain.spi.PlayerStoreStub
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
class PlayerManagerBeanTest {

    @MockK
    private lateinit var idProvider: IdProvider

    private lateinit var playerStore: PlayerStore

    private lateinit var playerManagerBean: PlayerManagerBean


    @BeforeEach
    fun setUp() {
        playerStore = PlayerStoreStub()
        playerManagerBean = PlayerManagerBean(idProvider, playerStore)
    }


    @Test
    fun `createPlayer shall fail when name is too short`() {
        assertThatThrownBy {
            playerManagerBean.createPlayer(PlayerCreation("a"))
        }.isInstanceOf(InvalidPlayerAttributeException::class.java)
            .extracting("playerId", "attributeNames")
            .contains("in-creation", setOf("name"))
    }

    @Test
    fun `createPlayer shall fail when name is too long`() {
        assertThatThrownBy {
            playerManagerBean.createPlayer(PlayerCreation(TOO_LONG_NAME))
        }.isInstanceOf(InvalidPlayerAttributeException::class.java)
            .extracting("playerId", "attributeNames")
            .contains("in-creation", setOf("name"))
    }

    @Test
    fun `createPlayer shall fail when name already exists`() {
        every { idProvider.createId() } returns JOHN.id
        playerStore.save(Player("player-x", JOHN.name))

        assertThatThrownBy {
            playerManagerBean.createPlayer(PlayerCreation(JOHN.name))
        }.isInstanceOf(PlayerAlreadyExistException::class.java)
            .extracting("playerId", "attributeNames")
            .contains(JOHN.id, setOf("name"))

        verify(exactly = 1) { idProvider.createId() }
    }

    @Test
    fun `createPlayer shall create a player when it does not already exist`() {
        every { idProvider.createId() } returns JOHN.id

        assertThat(playerManagerBean.createPlayer(PlayerCreation(JOHN.name)))
            .isEqualTo(JOHN)

        assertThat(playerStore.getById(JOHN.id))
            .isEqualTo(JOHN)

        verify(exactly = 1) { idProvider.createId() }
    }

    @Test
    fun `containsPlayer shall return true when player is in store`() {
        playerStore.save(JOHN)

        assertThat(playerManagerBean.containsPlayer(JOHN.id))
            .isTrue()
    }

    @Test
    fun `containsPlayer shall return false when player is not in store`() {
        assertThat(playerManagerBean.containsPlayer(JOHN.id))
            .isFalse()
    }


    @Test
    fun `getPlayerById shall fail when player not in store`() {
        assertThatThrownBy {
            playerManagerBean.getPlayerById(JOHN.id)
        }.isInstanceOf(PlayerNotFoundException::class.java)
            .extracting("playerId")
            .isEqualTo(JOHN.id)
    }

    @Test
    fun `getPlayerById shall return the player when it exists in the store`() {
        playerStore.save(JOHN)

        assertThat(playerManagerBean.getPlayerById(JOHN.id))
            .isEqualTo(JOHN)
    }


    @Test
    fun `getAllPlayers shall return all players in store`() {
        playerStore.saveAll(JOHN, JANE)

        assertThat(playerManagerBean.getAllPlayers())
            .containsOnly(JOHN, JANE)
    }


    @Test
    fun `update shall fail when name is too short`() {
        assertThatThrownBy {
            playerManagerBean.update(JOHN.id, PlayerUpdate(name = "a"))
        }.isInstanceOf(InvalidPlayerAttributeException::class.java)
            .extracting("playerId", "attributeNames")
            .containsOnly(JOHN.id, setOf("name"))
    }


    @Test
    fun `update shall fail when name is too long`() {
        assertThatThrownBy {
            playerManagerBean.update(JOHN.id, PlayerUpdate(TOO_LONG_NAME))
        }.isInstanceOf(InvalidPlayerAttributeException::class.java)
            .extracting("playerId", "attributeNames")
            .containsOnly(JOHN.id, setOf("name"))
    }


    @Test
    fun `update shall fail when name already exist in another record`() {
        playerStore.saveAll(JOHN, JANE)

        assertThatThrownBy {
            playerManagerBean.update(JOHN.id, PlayerUpdate(JANE.name))
        }.isInstanceOf(PlayerAlreadyExistException::class.java)
            .extracting("playerId", "attributeNames")
            .containsOnly(JOHN.id, setOf("name"))

        assertThat(playerStore.getById(JOHN.id))
            .isEqualTo(JOHN)
        assertThat(playerStore.getById(JANE.id))
            .isEqualTo(JANE)
    }

    @Test
    fun `update shall do nothing when name is the same`() {
        playerStore.save(JOHN)

        playerManagerBean.update(JOHN.id, PlayerUpdate(JOHN.name))
    }

    @Test
    fun `update shall do nothing when name is null`() {
        playerStore.save(JOHN)

        playerManagerBean.update(JOHN.id, PlayerUpdate())
    }


    @Test
    fun `update shall change the name when it is valid`() {
        playerStore.save(JOHN)

        val expectedPlayer = Player(JOHN.id, "Walter")

        assertThat(playerManagerBean.update(JOHN.id, PlayerUpdate(expectedPlayer.name)))
            .isEqualTo(expectedPlayer)

        assertThat(playerStore.getById(JOHN.id))
            .isEqualTo(expectedPlayer)
    }


    @Test
    fun `deletePlayer shall do nothing when player is missing`() {
        assertThat( playerManagerBean.deletePlayer(JOHN.id))
            .isFalse()
    }


    @Test
    fun `deletePlayer shall delete the player when it exists`() {
        playerStore.save(JOHN)

        assertThat( playerManagerBean.deletePlayer(JOHN.id))
            .isTrue()

        assertThat(playerStore.contains(JOHN.id))
            .isFalse()
    }

    companion object {
        private const val TOO_LONG_NAME = "This is a very too long name"
    }
}