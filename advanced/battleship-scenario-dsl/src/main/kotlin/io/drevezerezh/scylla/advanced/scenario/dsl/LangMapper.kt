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

package io.drevezerezh.scylla.advanced.scenario.dsl

import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation
import io.drevezerezh.scylla.advanced.lang.ShipType
import org.antlr.v4.runtime.tree.TerminalNode

object LangMapper {
    /**
     * Convert a node to [GridOrientation]
     * @param node a terminal node representing a grid orientation
     * @throws IllegalArgumentException when the node does not contain a valid grid orientation
     */
    fun toLangOrientation(node: TerminalNode): GridOrientation {
        val orientationAsText = node.text
        return toLangOrientation(orientationAsText)
    }

    /**
     * Convert DSL token value into [GridOrientation]
     * @param value the token value expressed in text format
     * @throws IllegalArgumentException when the token value is not a valid grid orientation
     */
    fun toLangOrientation(value: String): GridOrientation {
        return when (value.lowercase()) {
            "row" -> GridOrientation.ROW
            "column" -> GridOrientation.COLUMN
            else -> throw IllegalArgumentException("value '$value' is not a valid orientation value")
        }
    }

    /**
     * Convert a node to [ShipType]
     * @param node a terminal node representing a ship type
     * @throws IllegalArgumentException when the node does not contain a valid ship type
     */
    fun toLangShipType(node: TerminalNode): ShipType {
        val shipTypeAsText = node.text
        return toLangShipType(shipTypeAsText)
    }

    /**
     * Convert DSL token value into [ShipType]
     * @param value the token value expressed in text format
     * @throws IllegalArgumentException when the token value is not a valid ship type
     */
    fun toLangShipType(value: String): ShipType {
        return when (value.lowercase()) {
            "carrier" -> ShipType.CARRIER
            "battleship" -> ShipType.BATTLESHIP
            "cruiser" -> ShipType.CRUISER
            "submarine" -> ShipType.SUBMARINE
            "destroyer" -> ShipType.DESTROYER
            else -> throw IllegalArgumentException("value '$value' is not a valid ship type value")
        }
    }


    /**
     * Convert a node to [GridLocation]
     * @param node a terminal node representing a grid location
     * @throws IllegalArgumentException when the node does not contain a valid grid location
     */
    fun toLangLocation(node: TerminalNode): GridLocation {
        val locationAsText = node.text
        return GridLocation.of(locationAsText)
    }
}
