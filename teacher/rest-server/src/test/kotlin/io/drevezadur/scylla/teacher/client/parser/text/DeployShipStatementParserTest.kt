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

package io.drevezadur.scylla.teacher.client.parser.text

import io.drevezadur.scylla.teacher.client.parser.InvalidStatementException
import io.drevezadur.scylla.teacher.restserver.lang.GridOrientation
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class DeployShipStatementParserTest {

    private val statementParser = DeployShipStatementParser()


    @Test
    fun `parse shall return null when not starting by statement keyword`() {
        Assertions.assertThat(statementParser.parse("anotherStatement"))
            .isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["deployShip", "deployShip Jane", "deployShip Jane CARRIER", "deployShip Jane CARRIER 35", "deployShip Jane CARRIER 35 UNKNOWN", "deployShip Jane CARRIER 35 ROW OTHER"])
    fun `parse shall not accept invalid statements`(statement: String) {
        Assertions.assertThatThrownBy {
            statementParser.parse(statement)
        }.isInstanceOf(InvalidStatementException::class.java)
    }

    @Test
    fun `parse shall accept valid statement`() {
        Assertions.assertThat(statementParser.parse("deployShip Jane CARRIER 35 ROW"))
            .extracting("playerName", "shipType", "location", "orientation")
            .containsExactly("Jane", ShipType.CARRIER, Location(3, 5), GridOrientation.ROW)
    }
}