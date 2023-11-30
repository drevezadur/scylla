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

import io.drevezadur.scylla.teacher.client.parser.DeployShipInstruction
import io.drevezadur.scylla.teacher.client.parser.Instruction
import io.drevezadur.scylla.teacher.client.parser.InvalidStatementException
import io.drevezadur.scylla.teacher.client.parser.StatementParser
import io.drevezadur.scylla.teacher.restserver.lang.GridOrientation
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import java.util.regex.Pattern

class DeployShipStatementParser : StatementParser {

    override fun parse(statement: String): Instruction? {
        if (!statement.startsWith("deployShip"))
            return null

        val matcher = INSTRUCTION_PATTERN.matcher(statement)
        if (!matcher.matches())
            throw InvalidStatementException("Invalid deployShip statement : $statement")

        val playerName = matcher.group(1)
        val shipType = toShipType(matcher.group(2))
        val location = toLocation(matcher.group(3))
        val orientation = toOrientation(matcher.group(4))
        return DeployShipInstruction(
            playerName,
            shipType,
            location,
            orientation
        )
    }

    companion object {
        private val INSTRUCTION_PATTERN =
            Pattern.compile("deployShip[ \\t]+([a-zA-Z0-9]{1,20})[ \\t]+([A-Z]{1,15})[ \\t]+([0-9]{2})[ \\t]+(ROW|COLUMN)")

        private fun toShipType(token: String): ShipType {
            try {
                return ShipType.valueOf(token)
            } catch (ex: IllegalArgumentException) {
                throw InvalidStatementException("Invalid deployShip statement, unknown ship type '$token'")
            }
        }

        private fun toLocation(token: String): Location {
            try {
                return Mapper.toLocation(token)
            } catch (ex: IllegalArgumentException) {
                throw InvalidStatementException("Invalid deployShip statement, invalid location '$token'")
            }
        }

        private fun toOrientation(token: String): GridOrientation {
            try {
                return Mapper.toOrientation(token)
            } catch (ex: IllegalArgumentException) {
                throw InvalidStatementException("Invalid deployShip statement, invalid orientation '$token'")
            }
        }
    }
}