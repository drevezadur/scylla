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
import io.drevezadur.scylla.teacher.client.parser.Instruction
import io.drevezadur.scylla.teacher.client.parser.InvalidStatementException
import io.drevezadur.scylla.teacher.client.parser.StatementParser

class BattleStatusStatementParser : StatementParser {

    override fun parse(statement: String): Instruction? {
        if (!statement.startsWith("battleStatus"))
            return null

        if (statement != "battleStatus")
            throw InvalidStatementException("Invalid battleStatus statement : '$statement'")

        return BattleStatusInstruction()
    }
}