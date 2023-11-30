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

import io.drevezadur.scylla.teacher.client.parser.CreatePlayerInstruction
import io.drevezadur.scylla.teacher.client.parser.Instruction
import io.drevezadur.scylla.teacher.client.parser.InvalidStatementException
import io.drevezadur.scylla.teacher.client.parser.StatementParser
import java.util.regex.Pattern

class CreatePlayerStatementParser : StatementParser {

    override fun parse(statement: String): Instruction? {
        if (!statement.startsWith("createPlayer"))
            return null

        val matcher = INSTRUCTION_PATTERN.matcher(statement)
        if (!matcher.matches())
            throw InvalidStatementException("Invalid createPlayer statement : $statement")

        val playerName = matcher.group(1)
        return CreatePlayerInstruction(playerName)
    }

    companion object {
        private val INSTRUCTION_PATTERN = Pattern.compile("createPlayer ([a-zA-Z0-9]{1,20})")
    }
}