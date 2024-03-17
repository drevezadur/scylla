/*
 * Copyright (c) 2022-2023. gofannon.io
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

package io.drevezerezh.scylla.advanced.lang

import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs


/**
 * A location on a grid
 *
 * @param x the row identifier
 * @param y the column identifier
 */
data class GridLocation(val x: Int, val y: Int) : Comparable<GridLocation> {
    init {
        if (!isValidX(x))
            throw IllegalArgumentException("x ('$x') shall be in 0..9 ")
        if (!isValidY(y))
            throw IllegalArgumentException("y ('$y') shall be in 0..9 ")
    }

    override fun toString(): String {
        return "[$x,$y]"
    }

    /**
     * Check if a location is the neighbour of the current location
     * @param location the compared location
     * @return true when the two locations are neighbours, false otherwise
     */
    fun isNeighbour(location: GridLocation): Boolean {
        return (location.y == y && abs(location.x - x) == 1)
                || (location.x == x && abs(location.y - y) == 1)
    }

    override fun compareTo(other: GridLocation): Int {
        return if (x == other.x)
            other.y - y
        else if (y == other.y)
            other.x - x
        else
            0
    }

    companion object {
        /**
         * Check if an abscisse is in the grid
         * @param x the abscisse to check
         * @return true if the abscisse is valid, false otherwise
         */
        fun isValidX(x: Int): Boolean = x in 0..9

        /**
         * Check if an ordinate is in the grid
         * @param y the ordinate to check
         * @return true if the ordinate is valid, false otherwise
         */
        fun isValidY(y: Int): Boolean = y in 0..9

        private val LOCATION_PATTERN = Pattern.compile("[A-Z][0-9]")
        fun of(value: String): GridLocation {
            val uppercaseValue = value.uppercase()
            if (!LOCATION_PATTERN.matcher(uppercaseValue).matches())
                throw IllegalArgumentException("'$value' is not a valid grid location")

            val x = uppercaseValue[0].code - 'A'.code
            val y = uppercaseValue[1].code - '0'.code
            return GridLocation(x, y)

        }
    }
}