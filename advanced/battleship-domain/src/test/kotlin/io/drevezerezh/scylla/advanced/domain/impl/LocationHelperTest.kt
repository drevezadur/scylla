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

import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.A0
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.A1
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.A2
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.A3
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.B0
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.B1
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.B2
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.B3
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.C0
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.C1
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.D0
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.D1
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.J0
import io.drevezerezh.scylla.advanced.domain.data.LocationTestData.J9
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation.COLUMN
import io.drevezerezh.scylla.advanced.lang.GridOrientation.ROW
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LocationHelperTest {

    @ParameterizedTest
    @ValueSource(ints = [-3, 0, 11, 20])
    fun `computeSegment shall fail when length is out of value`(length: Int) {
        assertThatThrownBy {
            LocationHelper.computeSegment(A0, ROW, length)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `computeSegment shall fail when segment is out of grid on row`() {
        assertThatThrownBy {
            LocationHelper.computeSegment(J0, ROW, 5)
        }.isInstanceOf(OutOfGridException::class.java)
    }

    @Test
    fun `computeSegment shall fail when segment is out of grid on column`() {
        assertThatThrownBy {
            LocationHelper.computeSegment(J9, COLUMN, 5)
        }.isInstanceOf(OutOfGridException::class.java)
    }


    @Test
    fun `computeSegment shall create segment on row`() {
        val expectedSegment = listOf(A0, B0, C0, D0)

        assertThat(LocationHelper.computeSegment(A0, ROW, 4))
            .isEqualTo(expectedSegment)
    }


    @Test
    fun `computeSegment shall create segment on column`() {
        val expectedSegment = listOf(A0, A1, A2, A3)

        assertThat(LocationHelper.computeSegment(A0, COLUMN, 4))
            .isEqualTo(expectedSegment)
    }


    @Test
    fun `isIntersection shall return false when both segments are empty`() {
        val firstSegment = emptyList<GridLocation>()
        val secondSegment = emptyList<GridLocation>()

        assertThat(
            LocationHelper.isIntersection(firstSegment, secondSegment)
        ).isFalse()
    }


    @Test
    fun `isIntersection shall return false when first segment is empty`() {
        val firstSegment = emptyList<GridLocation>()
        val secondSegment = listOf(A0, A1, A2, A3)

        assertThat(
            LocationHelper.isIntersection(firstSegment, secondSegment)
        ).isFalse()
    }


    @Test
    fun `isIntersection shall return false when second segment is empty`() {
        val firstSegment = listOf(A0, A1, A2, A3)
        val secondSegment = emptyList<GridLocation>()

        assertThat(
            LocationHelper.isIntersection(firstSegment, secondSegment)
        ).isFalse()
    }


    @Test
    fun `isIntersection shall return false when segments are not crossing`() {
        val firstSegment = listOf(A0, A1, A2, A3)
        val secondSegment = listOf(B0, B1, B2, B3)

        assertThat(
            LocationHelper.isIntersection(firstSegment, secondSegment)
        ).isFalse()
    }


    @Test
    fun `isIntersection shall return true when segments are crossing`() {
        val firstSegment = listOf(A0, A1, A2, A3)
        val secondSegment = listOf(A1, B1, C1, D1)

        assertThat(
            LocationHelper.isIntersection(firstSegment, secondSegment)
        ).isTrue()
    }
}