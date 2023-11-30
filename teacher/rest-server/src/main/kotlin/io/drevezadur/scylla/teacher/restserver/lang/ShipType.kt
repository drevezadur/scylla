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

package io.drevezadur.scylla.teacher.restserver.lang

/**
 * Type of ships
 */
enum class ShipType(
    val size: Int
) {
    /**
     * Carrier with size of 5
     */
    CARRIER(5),

    /**
     * Battleship with size of 4
     */
    BATTLESHIP(4),

    /**
     * Cruiser with size of 3
     */
    CRUISER(3),

    /**
     * Submarine with size of 3
     */
    SUBMARINE(3),

    /**
     * Destroyer with size of 2
     */
    DESTROYER(2);
}
