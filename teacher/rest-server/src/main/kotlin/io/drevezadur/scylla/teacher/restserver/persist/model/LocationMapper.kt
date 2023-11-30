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

package io.drevezadur.scylla.teacher.restserver.persist.model

import io.drevezadur.scylla.teacher.restserver.lang.Location
import java.util.regex.Pattern

object LocationMapper {

    /**
     * Convert a collection of locations to a string for storage into database
     * @param locations the collection of locations
     * @return the string representation of the location collection
     */
    fun toString(locations: Collection<Location>): String {
        return locations.joinToString(separator = "") { "${it.x}${it.y}" }
    }

    fun toString(location: Location): String {
        return "${location.x}${location.y}"
    }

    /**
     * Convert a string to a list of locations
     * @param string the string to convert
     * @return the list of locations representing the content of the [string]
     */
    fun toLocations(string: String): List<Location> {
        if (!isValidLocationList(string))
            throw IllegalArgumentException("Value '$string' is not a valid location list")

        return string.chunked(2)
            .map(this::toLocation)
    }

    private fun isValidLocationList(string: String): Boolean {
        return string.length % 2 == 0
                && LOCATION_LIST_PATTERN.matcher(string).matches()
    }

    fun toLocation(string: String): Location {
        if (!isValidLocation(string))
            throw IllegalArgumentException("Value '$string' is not a valid location")
        return Location(string[0] - '0', string[1] - '0')
    }

    private fun isValidLocation(string: String): Boolean {
        return string.length == 2
                && LOCATION_LIST_PATTERN.matcher(string).matches()
    }

    private val LOCATION_LIST_PATTERN: Pattern = Pattern.compile("[0-9]*")
}