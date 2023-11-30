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

package io.drevezadur.scylla.teacher.restserver.service.pathcontext

import io.drevezadur.scylla.teacher.restserver.common.UuidProvider
import io.drevezadur.scylla.teacher.restserver.common.internal.UuidProviderBean
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.drevezadur.scylla.teacher.restserver.service.util.RestException
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

@ExtendWith(MockKExtension::class)
class PathContextFactoryBeanTest {

    private val uuidProvider: UuidProvider = UuidProviderBean()
    private val contextConverterFactory: PathContextFactory = PathContextFactoryBean(uuidProvider)

    @BeforeEach
    fun setUp() {
    }


    @ParameterizedTest
    @ValueSource(strings = ["", "xxx", "a3b5", "e4ac7bed-1867-4cc8-8cf5-aa30ddd86d4x", "e4ac7bed-1867-4cc8-8cf5aa-30dZd86d40"])
    fun `createPlayerCommand() shall not accept invalid user id values`(playerIdRaw: String) {
        assertThatThrownBy {
            contextConverterFactory.createPlayerContext(playerIdRaw)
        }.isInstanceOf(RestException::class.java)
    }


    @Test
    fun createPlayerCommand() {
        assertThat(contextConverterFactory.createPlayerContext(UUID_1_RAW))
            .isEqualTo(PlayerPathContext(uuid1))
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "xxx", "a3b5", "e4ac7bed-1867-4cc8-8cf5-aa30ddd86d4x"])
    fun `createBattleCommand shall not accept invalid battle id values`(battleIdRaw: String) {
        assertThatThrownBy {
            contextConverterFactory.createBattleContext(battleIdRaw)
        }.isInstanceOf(RestException::class.java)
    }

    @Test
    fun createBattleCommand() {
        assertThat(contextConverterFactory.createBattleContext(UUID_1_RAW))
            .isEqualTo(BattlePathContext(uuid1))
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "xxx", "a3b5", "e4ac7bed-1867-4cc8-8cf5-aa30ddd86d4x"])
    fun `createBattlePlayerCommand shall not accept invalid battle id values`(battleIdRaw: String) {
        assertThatThrownBy {
            contextConverterFactory.createBattlePlayerContext(battleIdRaw, UUID_2_RAW)
        }.isInstanceOf(RestException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "xxx", "a3b5", "e4ac7bed-1867-4cc8-8cf5-aa30ddd86d4x", "e4ac7bed-1867-4cc8-8cf5aa-30dZd86d40"])
    fun `createBattlePlayerCommand() shall not accept invalid user id values`(playerIdRaw: String) {
        assertThatThrownBy {
            contextConverterFactory.createBattlePlayerContext(UUID_2_RAW, playerIdRaw)
        }.isInstanceOf(RestException::class.java)
    }

    @Test
    fun createBattlePlayerCommand() {
        assertThat(contextConverterFactory.createBattlePlayerContext(UUID_1_RAW, UUID_2_RAW))
            .isEqualTo(BattlePlayerPathContext(uuid1, uuid2))
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "xxx", "a3b5", "e4ac7bed-1867-4cc8-8cf5-aa30ddd86d4x", "e4ac7bed-1867-4cc8-8cf5aa-30dzd86d40"])
    fun `createShipCommand shall not accept invalid battle id value`(battleIdRaw: String) {
        assertThatThrownBy {
            contextConverterFactory.createShipContext(battleIdRaw, UUID_2_RAW, ShipType.BATTLESHIP.name)
        }.isInstanceOf(RestException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "xxx", "a3b5", "e4ac7bed-1867-4cc8-8cf5-aa30ddd86d4x", "e4ac7bed-1867-4cc8-8cf5aa-30dzd86d40"])
    fun `createShipCommand shall not accept invalid user id value`(playerIdRaw: String) {
        assertThatThrownBy {
            contextConverterFactory.createShipContext(UUID_1_RAW, playerIdRaw, ShipType.BATTLESHIP.name)
        }.isInstanceOf(RestException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "xxx", "BATTLESHIPX", "XXXXX"])
    fun `createShipCommand shall not accept invalid ship type value`(typeRaw: String) {
        assertThatThrownBy {
            contextConverterFactory.createShipContext(UUID_1_RAW, UUID_2_RAW, typeRaw)
        }.isInstanceOf(RestException::class.java)
    }

    @Test
    fun createShipCommand() {
        assertThat(contextConverterFactory.createShipContext(UUID_1_RAW, UUID_2_RAW, ShipType.BATTLESHIP.name))
            .isEqualTo(ShipPathContext(uuid1, uuid2, ShipType.BATTLESHIP))
    }


    companion object {
        const val UUID_1_RAW: String = "f1f28837-2966-4255-9be2-9a04fa1e0941"
        const val UUID_2_RAW: String = "ef27d810-454c-4139-b424-319a022ba859"

        val uuid1: UUID = UUID.fromString(UUID_1_RAW)
        val uuid2: UUID = UUID.fromString(UUID_2_RAW)
    }
}