package io.drevezadur.scylla.teacher.client.service.model

import java.util.*


data class BattlePojo(
    val id : UUID,
    val player1Id : UUID,
    val player2Id : UUID,
    val shootingPlayer: UUID,
    val status: BattleStatus,
    val winner : UUID? = null
)
