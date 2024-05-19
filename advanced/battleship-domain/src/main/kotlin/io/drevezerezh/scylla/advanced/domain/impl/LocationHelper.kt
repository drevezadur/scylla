/*
 * Copyright (c) 2024 gofannon.xyz
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

package io.drevezerezh.scylla.advanced.domain.impl

import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation

object LocationHelper {

    /**
     * Compute each location of a segment
     * @param origin the origin of the segment
     * @param orientation the orientation of the segment
     * @param length the length of the segment, it shall be greater than 0
     * @return the ordered list of the locations in the segment. The first location is the origin argument
     * @throws IllegalArgumentException when length is invalid
     * @throws OutOfGridException when at least one location in the segment is out of the grid
     */
    @Throws(IllegalArgumentException::class, OutOfGridException::class)
    fun computeSegment(origin: GridLocation, orientation: GridOrientation, length: Int): List<GridLocation> {
        if (length <= 0 || length > 10)
            throw IllegalArgumentException("length must be greater than or equal to 0 and lower or equal to 10")

        val x = origin.x
        val y = origin.y

        try {
            return if (orientation == GridOrientation.ROW) {
                val maxX = x + length
                (x..<maxX).map { GridLocation(it, y) }
            } else {
                val maxY = y + length
                (y..<maxY).map { GridLocation(x, it) }
            }
        } catch (ex: IllegalArgumentException) {
            throw OutOfGridException("Segment is out of grid", ex)
        }
    }

    /**
     * Check if there is an intersection between two segments
     * @param segment1 the first segment
     * @param segment2 the second segment
     * @return true when there is an intersection between the two segments, false otherwise
     */
    fun isIntersection(segment1: List<GridLocation>, segment2: List<GridLocation>): Boolean {
        for( loc1 in segment1) {
            for( loc2 in segment2) {
                if( loc1 == loc2)
                    return true
            }
        }
        return false
    }
}