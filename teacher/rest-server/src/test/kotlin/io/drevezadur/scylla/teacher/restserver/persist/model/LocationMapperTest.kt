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

package io.drevezadur.scylla.teacher.restserver.persist.model

import io.drevezadur.scylla.teacher.restserver.lang.Location
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class LocationMapperTest {


    @Test
    fun `toString(Location) shall convert correctly`() {
        assertThat(
            LocationMapper.toString(Location(3, 7))
        ).isEqualTo("37")
    }

    @Test
    fun `toString(List of Location) shall accept empty list`() {
        assertThat(
            LocationMapper.toString(emptyList())
        ).isEqualTo("")
    }

    @Test
    fun `toString(List of Location) shall accept single element list`() {
        assertThat(
            LocationMapper.toString(listOf(Location(3, 5)))
        ).isEqualTo("35")
    }

    @Test
    fun `toString(List of Location) shall accept severable element list`() {
        assertThat(
            LocationMapper.toString(
                listOf(
                    Location(3, 5),
                    Location(9, 2),
                    Location(1, 1)
                )
            )
        ).isEqualTo("359211")
    }


    @ParameterizedTest
    @ValueSource(strings = ["a3", "3G", "3F47"])
    fun `toLocations shall not accept string with non digital values`(string: String) {
        assertThatThrownBy { LocationMapper.toLocations(string) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["1", "347", "4018528"])
    fun `toLocations shall not accept string with odd length`(string: String) {
        assertThatThrownBy { LocationMapper.toLocations(string) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `toLocations shall handle valid location string`() {
        assertThat(
            LocationMapper.toLocations("427391")
        ).isEqualTo(
            mutableListOf(
                Location(4, 2),
                Location(7, 3),
                Location(9, 1)
            )
        )
    }

    @Test
    fun `toLocation shall not accept empty string`() {
        assertThatThrownBy { LocationMapper.toLocation("") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `toLocation shall not accept string greater than 2`() {
        assertThatThrownBy { LocationMapper.toLocation("123") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["A1", "2B", "de"])
    fun `toLocation shall not accept string with not `(string: String) {
        assertThatThrownBy { LocationMapper.toLocation(string) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @CsvSource(value = ["34,3,4", "87, 8,7", "19,1,9"])
    fun toLocation(string: String, x: Int, y: Int) {
        assertThat(LocationMapper.toLocation(string))
            .extracting("x", "y")
            .containsExactly(x, y)
    }
}