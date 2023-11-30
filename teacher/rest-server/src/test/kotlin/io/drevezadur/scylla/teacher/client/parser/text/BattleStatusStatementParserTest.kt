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

import io.drevezadur.scylla.teacher.client.parser.BattleStatusInstruction
import io.drevezadur.scylla.teacher.client.parser.InvalidStatementException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class BattleStatusStatementParserTest {

    private val statementParser = BattleStatusStatementParser()

    @Test
    fun `parse shall return null when not starting by statement keyword`() {
        assertThat(statementParser.parse("anotherStatement"))
            .isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["battleStatus x"])
    fun `parse shall not accept invalid statements`(statement: String) {
        assertThatThrownBy {
            statementParser.parse(statement)
        }.isInstanceOf(InvalidStatementException::class.java)
    }

    @Test
    fun `parse shall accept valid statement`() {
        assertThat(statementParser.parse("battleStatus"))
            .isInstanceOf(BattleStatusInstruction::class.java)
    }
}