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

package io.drevezadur.scylla.teacher.client.service.model

import java.util.regex.Pattern
import kotlin.math.abs


/**
 * A location on a grid
 *
 * @param x the row identifier
 * @param y the column identifier
 */
data class Location(val x: Int, val y: Int) {
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
    fun isNeighbour(location: Location): Boolean {
        return (location.y == y && abs(location.x - x) == 1)
                || (location.x == x && abs(location.y - y) == 1)
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

    }
}