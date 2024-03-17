package io.drevezerezh.scylla.advanced.scenario.dsl

import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation
import io.drevezerezh.scylla.advanced.lang.ShipType
import io.drevezerezh.scylla.advanced.scenario.dsl.LangMapper.toLangLocation
import io.drevezerezh.scylla.advanced.scenario.dsl.LangMapper.toLangOrientation
import io.drevezerezh.scylla.advanced.scenario.dsl.LangMapper.toLangShipType
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.antlr.v4.runtime.tree.TerminalNode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(MockKExtension::class)
internal class LangMapperTest {
    @MockK
    lateinit var terminalNode: TerminalNode

    @ParameterizedTest
    @ValueSource(strings = ["Rows", "col", "", "this is an invalid value"])
    fun toLangOrientation_shall_fail_when_invalid_input(value: String) {
        assertThatThrownBy {
            toLangOrientation(value)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @CsvSource("row, ROW", "column, COLUMN", "ColUmn, COLUMN", "rOw, ROW")
    fun toLangOrientation_shall_success_when_valid_input(value: String, expected: GridOrientation) {
        assertThat(
            toLangOrientation(value)
        ).isSameAs(expected)
    }

    @Test
    fun toLangOrientationTerminalNode() {
        every { terminalNode.text } returns "column"

        assertThat(
            toLangOrientation(terminalNode)
        ).isSameAs(GridOrientation.COLUMN)

        verify { terminalNode.text }
    }


    @ParameterizedTest
    @ValueSource(strings = ["cruisers", "", "airborne"])
    fun toLangShipType_failure(value: String) {
        assertThatThrownBy {
            toLangShipType(value)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @CsvSource("cruiser, CRUISER", "SUBMARINE, SUBMARINE")
    fun toLangShipType_success(value: String?, expected: ShipType?) {
        assertThat(
            toLangShipType(value!!)
        ).isSameAs(expected)
    }

    @Test
    fun toLangShipTypeTerminalNode() {
        every { terminalNode.text } returns "carrier"

        assertThat(
            toLangShipType(terminalNode)
        ).isSameAs(ShipType.CARRIER)

        verify { terminalNode.text }
    }


    @Test
    fun toLangLocationTerminalNode() {
        every { terminalNode.text } returns "A7"

        assertThat(
            toLangLocation(terminalNode)
        ).isEqualTo(
            GridLocation(0, 7)
        )

        verify { terminalNode.text }
    }
}