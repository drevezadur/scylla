package io.drevezerezh.scylla.advanced.scenario.dsl

import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation
import io.drevezerezh.scylla.advanced.lang.ShipType

data class ShipDeploymentInstruction(
    val playerName: String,
    val shipType: ShipType,
    val location: GridLocation,
    val orientation: GridOrientation
)