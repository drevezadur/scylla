/*
 * Copyright (c) 2023. gofannon.io
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

package io.gofannon.scylla.homework.service.internal

import io.gofannon.scylla.homework.lang.GridOrientation
import io.gofannon.scylla.homework.lang.Location
import io.gofannon.scylla.homework.lang.Player
import io.gofannon.scylla.homework.lang.ShipType
import io.gofannon.scylla.homework.service.BadInstructionFormatException
import io.gofannon.scylla.homework.service.UnknownInstructionException
import java.util.regex.Pattern

internal class DefaultCommandFactory : CommandFactory {

    private val deployCommandPattern =
        Pattern.compile("([AB]) deploy (CARRIER|BATTLESHIP|CRUISER|SUBMARINE|DESTROYER) ([0-9]),([0-9]) (ROW|COLUMN)")
    private val fireCommandPattern = Pattern.compile("([AB]) fire ([0-9]),([0-9])")

    override fun createCommand(instruction: String): Command {
        val cleanedInstruction = cleanInstruction(instruction)

        val commentCommand = createIgnoredCommand(cleanedInstruction)
        if (commentCommand != null)
            return commentCommand

        val fireCommand = createFireCommand(cleanedInstruction)
        if (fireCommand != null)
            return fireCommand

        val deployCommand = createDeployCommand(cleanedInstruction)
        if (deployCommand != null)
            return deployCommand

        throw UnknownInstructionException("Unknown instruction '$instruction")
    }

    private fun createIgnoredCommand(instruction: String): Command? {
        if (instruction.isBlank() || instruction.startsWith("#"))
            return IgnoredCommand(instruction)
        return null
    }

    private fun cleanInstruction(rawInstruction: String): String = rawInstruction.trim().trimEnd()


    private fun createDeployCommand(commandString: String): Command? {
        val matcher = deployCommandPattern.matcher(commandString)
        if (!matcher.find())
            return null

        val fleetId = toFleetId(matcher.group(1))
        val shipType = toShipType(matcher.group(2))
        val location = toLocation(matcher.group(3), matcher.group(4))
        val orientation = toOrientation(matcher.group(5))

        return DeployCommand(fleetId, shipType, location, orientation)
    }

    private fun createFireCommand(commandString: String): Command? {
        val matcher = fireCommandPattern.matcher(commandString)
        if (!matcher.find())
            return null

        val fleetId = toFleetId(matcher.group(1))
        val location = toLocation(matcher.group(2), matcher.group(3))

        return FireCommand(fleetId, location)
    }

    companion object {

        private fun toFleetId(string: String): Player {
            return if (string == "A") Player.PLAYER_A else Player.PLAYER_B
        }

        private fun toShipType(string: String): ShipType {
            try {
                return ShipType.valueOf(string)
            } catch (ex: IllegalArgumentException) {
                throw BadInstructionFormatException("Unknown ship type '$string'")
            }
        }

        private fun toLocation(xString: String, yString: String): Location {
            return Location(
                xString.toInt(),
                yString.toInt()
            )
        }

        private fun toOrientation(string: String): GridOrientation {
            try {
                return GridOrientation.valueOf(string)
            } catch (ex: IllegalArgumentException) {
                throw BadInstructionFormatException("Unknown orientation '$string'")
            }
        }
    }
}