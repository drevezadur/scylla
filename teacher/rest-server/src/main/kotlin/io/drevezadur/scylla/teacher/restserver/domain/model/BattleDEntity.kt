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

package io.drevezadur.scylla.teacher.restserver.domain.model

import io.drevezadur.scylla.teacher.restserver.lang.BattleStatus
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID

class BattleDEntity(
    val id: BattleUUID,
    val player1Id: PlayerUUID,
    val player2Id: PlayerUUID,
    var status: BattleStatus,
    var shooterPlayerId: PlayerUUID = player1Id,
    var winnerId: PlayerUUID? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BattleDEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun switchShooterPlayer() {
        shooterPlayerId = if (shooterPlayerId == player1Id)
            player2Id
        else
            player1Id
    }

    fun containsPlayer(playerId: PlayerUUID): Boolean {
        return playerId == player1Id || playerId == player2Id
    }
}