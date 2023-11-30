package io.drevezadur.scylla.teacher.client.service.model

data class ShipDeploymentBody(
    val type: ShipType,
    val x: Int,
    val y: Int,
    val orientation: GridOrientation,
)
