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

package io.drevezerezh.scylla.advanced.persistance

import io.drevezerezh.scylla.advanced.domain.api.player.Player
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerNotFoundException
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MemoryPlayerStoreBeanTest {

    private lateinit var store: MemoryPlayerStoreBean

    @BeforeEach
    fun setUp() {
        store = MemoryPlayerStoreBean()
    }


    @Test
    fun `save shall create a new player`() {
        store.save(JOHN)

        assertThat(store.getById(JOHN.id))
            .isEqualTo(JOHN)
    }

    @Test
    fun `save shall update existing player`() {
        store.save(JOHN)

        val expectedPlayer = Player(JOHN.id, "Walter")
        store.save(expectedPlayer)


        assertThat(store.getById(JOHN.id))
            .isEqualTo(expectedPlayer)
    }


    @Test
    fun saveAll() {
        store.saveAll(JOHN, JANE, WALTER)

        assertThat(store.getAll())
            .containsOnly(JOHN, JANE, WALTER)
    }


    @Test
    fun `deleteById shall do nothing when player does not exist`() {
        assertThat(store.deleteById(JOHN.id))
            .isFalse()
    }

    @Test
    fun `deleteById shall delete the player when it exists`() {
        store.save(JOHN)

        assertThat(store.deleteById(JOHN.id))
            .isTrue()
    }


    @Test
    fun deleteAll() {
        store.saveAll(JOHN, JANE)

        store.deleteAll()

        assertThat(store.getAll())
            .isEmpty()
    }


    @Test
    fun `contains shall return false when player not in store`() {
        assertThat(store.contains(JOHN.id))
            .isFalse()
    }

    @Test
    fun `contains shall return true when player is in store`() {
        store.save(JOHN)

        assertThat(store.contains(JOHN.id))
            .isTrue()
    }

    @Test
    fun `getById shall fail when a player does not exist`() {
        assertThatThrownBy {
            store.getById("unknown")
        }.isInstanceOf(
            PlayerNotFoundException::class.java
        ).extracting("playerId")
            .isEqualTo("unknown")
    }

    @Test
    fun `getById shall return the player when it exists`() {
        store.save(JOHN)

        assertThat(store.getById(JOHN.id))
            .isEqualTo(JOHN)
    }

    @Test
    fun `getAll shall return empty list when no players`() {
        assertThat(store.getAll())
            .isEmpty()
    }

    @Test
    fun `getAll shall return all stored players`() {
        store.save(JOHN)
        store.save(JANE)

        assertThat(store.getAll())
            .containsOnly(JOHN, JANE)
    }

    @Test
    fun `exportToJson shall work when no content`() {
        assertThat(store.exportToJson())
            .isEqualTo("[]")
    }

    @Test
    fun `exportToJson shall work when several players are registerd`() {
        store.save(JOHN)
        store.save(JANE)
        val exportedContent = store.exportToJson()

        val store2 = MemoryPlayerStoreBean()
        store2.importJson(exportedContent)

        assertThat(store2.getAll())
            .contains(JOHN, JANE)
    }

    @Test
    fun `importJson shall accept empty list`() {
        store.importJson("[]")

        assertThat(store.getAll())
            .isEmpty()
    }

    @Test
    fun `importJson shall accept filled list`() {
        store.importJson(
            """
            [
              { "id": "${JOHN.id}", "name": "${JOHN.name}"},
              { "id": "${JANE.id}", "name": "${JANE.name}"}
            ]
        """.trimIndent()
        )

        assertThat(store.getAll())
            .contains(JOHN, JANE)
    }


    companion object {
        private val JOHN = Player("01", "John")
        private val JANE = Player("02", "Jane")
        private val WALTER = Player("03", "Walter")
    }
}