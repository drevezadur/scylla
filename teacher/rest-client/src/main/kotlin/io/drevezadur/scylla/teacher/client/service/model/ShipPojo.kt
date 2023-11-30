package io.drevezadur.scylla.teacher.client.service.model

import io.drevezadur.scylla.teacher.restserver.lang.*

data class ShipPojo (
    val battleId: BattleUUID,
    val playerId: PlayerUUID,
    val type: ShipType,
    val status : ShipStructuralStatus,
    val origin: LocationPojo,
    val orientation: GridOrientation,
    val hits: List<LocationPojo>

)