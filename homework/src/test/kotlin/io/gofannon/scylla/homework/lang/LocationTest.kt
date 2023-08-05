package io.gofannon.scylla.homework.lang

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class LocationTest {

    @ParameterizedTest
    @CsvSource("10,3", "3,10", "-1,3", "3,-1")
    fun `constructor(Int,Int) shall not accept invalid coordinates`(x: Int, y: Int) {
        assertThatThrownBy {
            Location(x, y)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }


    @ParameterizedTest
    @CsvSource("0,0", "5,5", "9,9", "2,7")
    fun `constructor(Int,Int) shall accept valid coordinates`(x: Int, y: Int) {
        val location = Location(x, y)

        assertThat( location)
            .extracting("x", "y")
            .containsExactly(x,y)
    }

    @ParameterizedTest
    @CsvSource("1,1,1,2,true", "1,1,1,3,false", "2,3,2,4,true", "2,3,2,5,false", "2,2,3,3,false")
    fun isNeighbour(x0: Int, y0: Int, x1: Int, y1: Int, neighbor: Boolean) {
        val location0 = Location(x0, y0)
        val location1 = Location(x1, y1)

        assertThat(
            location0.isNeighbour(location1)
        ).isEqualTo(neighbor)
        assertThat(
            location1.isNeighbour(location0)
        ).isEqualTo(neighbor)
    }

    @ParameterizedTest
    @CsvSource("2,2,2,2,0", "1,1,2,1,1", "2,4,3,4,1")
    fun compareTo(x0: Int, y0: Int, x1: Int, y1: Int, result: Int) {
        val location0 = Location(x0, y0)
        val location1 = Location(x1, y1)

        assertThat(
            location0.compareTo(location1)
        ).isEqualTo(result)
        assertThat(
            location1.compareTo(location0)
        ).isEqualTo(-result)
    }
}