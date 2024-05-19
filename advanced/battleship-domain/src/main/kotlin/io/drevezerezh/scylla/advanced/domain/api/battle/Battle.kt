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

package io.drevezerezh.scylla.advanced.domain.api.battle

import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.BattleStatus
import java.time.LocalDateTime

data class Battle(
    val id: String,
    val creation: LocalDateTime,
    val startTime: LocalDateTime? = null,
    val stopTime: LocalDateTime? = null,
    val player1Id: String,
    val player2Id: String,
    val nextPlayer: BattlePlayer = BattlePlayer.FIRST,
    val status: BattleStatus = BattleStatus.DEPLOYMENT,
    val turn: Int = 0
) {

    val winner: BattlePlayer?
        get() {
            return if (status != BattleStatus.FINISHED)
                null
            else
                nextPlayer
        }


    fun getPlayerId(playerOrder: BattlePlayer): String {
        return when (playerOrder) {
            BattlePlayer.FIRST -> player1Id
            BattlePlayer.SECOND -> player2Id
        }
    }


    fun getFleetId(playerOrder: BattlePlayer): FleetId {
        return FleetId(id, playerOrder)
    }
}