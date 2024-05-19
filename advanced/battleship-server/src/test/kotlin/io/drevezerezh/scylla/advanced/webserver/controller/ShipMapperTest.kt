package io.drevezerezh.scylla.advanced.webserver.controller

import io.drevezerezh.scylla.advanced.domain.api.ship.ShipDeployment
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.SECOND
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation.COLUMN
import io.drevezerezh.scylla.advanced.lang.ShipType.DESTROYER
import io.drevezerezh.scylla.advanced.webserver.controller.dto.ShipDeploymentJson
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource


class ShipMapperTest {

    @ParameterizedTest
    @ValueSource(strings = ["", "a", "firsts"])
    fun `toDomainBattlePlayer shall fail to convert unexpected value to battle player`(badValue: String) {
        assertThatThrownBy {
            ShipMapper.toDomainBattlePlayer(badValue)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }


    @ParameterizedTest
    @CsvSource(
        value = ["first,FIRST", "FIRST,FIRST", "firST,FIRST",
            "second,SECOND", "SECOND,SECOND"]
    )
    fun `toDomainBattlePlayer shall success to convert valid DTO values`(
        dtoValue: String,
        expectedValue: BattlePlayer
    ) {
        assertThat(
            ShipMapper.toDomainBattlePlayer(dtoValue)
        ).isEqualTo(expectedValue)
    }


    @Test
    fun `toDomain shall return a ship deployment DTO`() {
        assertThat(
            ShipMapper.toDomain(
                "xxx",
                SECOND,
                ShipDeploymentJson(DESTROYER, C7, COLUMN)
            )
        ).isEqualTo(
            ShipDeployment(
                "xxx",
                SECOND,
                DESTROYER,
                C7,
                COLUMN
            )
        )
    }

    companion object {
        val C7: GridLocation = GridLocation.of("C7")
    }
}