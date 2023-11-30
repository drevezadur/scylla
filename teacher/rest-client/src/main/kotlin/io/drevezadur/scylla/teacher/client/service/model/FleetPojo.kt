package io.drevezadur.scylla.teacher.client.service.model

import java.util.*

data class FleetPojo(
    val battleId: UUID,
    val playerId: UUID,
    val status: FleetStatus = FleetStatus.NOT_DEPLOYED,
)
