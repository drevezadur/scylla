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

package io.drevezadur.scylla.teacher.restserver.common.internal

import io.drevezadur.scylla.teacher.restserver.common.UuidProvider
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class UuidProviderBeanTest {

    private lateinit var uuidProvider: UuidProvider

    @BeforeEach
    fun setUp() {
        uuidProvider = UuidProviderBean()
    }


    @Test
    fun `create() shall create a UUID`() {
        uuidProvider.create()
    }

    @Test
    fun `fromString(String) shall not accept invalid UUID string`() {
        assertThatThrownBy {
            uuidProvider.fromString("not an UUID")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `fromString(String) shall accept valid UUID`() {
        val uuid = uuidProvider.create()

        assertThat(uuidProvider.fromString(uuid.toString()))
            .isEqualTo(uuid)
    }

    @Test
    fun `fromString(String, lambda) shall not accept invalid UUID string`() {
        assertThatThrownBy {
            uuidProvider.fromString("not an UUID") { throw IllegalArgumentException("Game over! Try again?") }
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Game over! Try again?")
    }


    @Test
    fun `fromString(String, lambda) shall accept valid UUID`() {
        val uuid = uuidProvider.create()

        assertThat(uuidProvider.fromString(uuid.toString()) { throw IllegalArgumentException(it) })
            .isEqualTo(uuid)
    }
}