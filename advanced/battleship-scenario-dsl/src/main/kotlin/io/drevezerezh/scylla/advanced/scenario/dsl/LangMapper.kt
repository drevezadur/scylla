package io.drevezerezh.scylla.advanced.scenario.dsl

import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation
import io.drevezerezh.scylla.advanced.lang.ShipType
import org.antlr.v4.runtime.tree.TerminalNode
import java.util.regex.Pattern

object LangMapper {
    fun toLangOrientation(node: TerminalNode): GridOrientation {
        val orientationAsText = node.text
        return toLangOrientation(orientationAsText)
    }

    fun toLangOrientation(value: String): GridOrientation {
        return when (value.lowercase()) {
            "row" -> GridOrientation.ROW
            "column" -> GridOrientation.COLUMN
            else -> throw IllegalArgumentException("value '$value' is not a valid orientation value")
        }
    }

    fun toLangShipType(node: TerminalNode): ShipType {
        val shipTypeAsText = node.text
        return toLangShipType(shipTypeAsText)
    }

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


    fun toLangLocation(node: TerminalNode): GridLocation {
        val locationAsText = node.text
        return toLangLocation(locationAsText)
    }

    private val LOCATION_PATTERN: Pattern = Pattern.compile("[A-Z][0-9]")


    fun toLangLocation(value: String): GridLocation {
        val uppercaseValue = value.uppercase()
        if( ! LOCATION_PATTERN.matcher(uppercaseValue).matches())
            throw IllegalArgumentException("'$value' is not a valid grid location")

        val x = uppercaseValue[0].code - 'A'.code
        val y = uppercaseValue[1].code - '0'.code
        return GridLocation(x, y)
    }
}
