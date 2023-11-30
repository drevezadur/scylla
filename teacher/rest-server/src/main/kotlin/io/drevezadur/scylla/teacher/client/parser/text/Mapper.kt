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

import io.drevezadur.scylla.teacher.restserver.lang.GridOrientation
import io.drevezadur.scylla.teacher.restserver.lang.GridOrientation.COLUMN
import io.drevezadur.scylla.teacher.restserver.lang.GridOrientation.ROW
import io.drevezadur.scylla.teacher.restserver.lang.Location
import java.util.regex.Pattern

object Mapper {

    fun toLocation(token: String): Location {
        val matcher = LOCATION_PATTERN.matcher(token)
        if (!matcher.matches())
            throw IllegalArgumentException("Invalid location '$token'")

        val x = token[0] - '0'
        val y = token[1] - '0'
        return Location(x, y)
    }

    private val LOCATION_PATTERN = Pattern.compile("[0-9]{2}")


    fun toOrientation(token: String): GridOrientation {
        return when (token) {
            "ROW" -> ROW
            "COLUMN" -> COLUMN
            else -> throw IllegalArgumentException("Invalid orientation value : '$token'")
        }
    }
}