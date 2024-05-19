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

import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.BattleStatus
import java.time.LocalDateTime

class BattleUpdate {

    private var updateStatus: BattleStatus? = null

    private var updateStartTime: Boolean = false
    private var startTime: LocalDateTime? = null

    private var updateStopTime: Boolean = false
    private var stopTime: LocalDateTime? = null

    private var updateNextPlayer: Boolean = false
    private var nextPlayer: BattlePlayer? = null

    private var incrementTurn: Boolean = false

    fun status(newStatus: BattleStatus): BattleUpdate {
        updateStatus = newStatus
        return this
    }

    fun status(): BattleStatus? {
        return updateStatus
    }

    fun startTime(startTime: LocalDateTime?): BattleUpdate {
        updateStartTime = true
        this.startTime = startTime
        return this
    }

    fun stopTime(stopTime: LocalDateTime?): BattleUpdate {
        updateStopTime = true
        this.stopTime = stopTime
        return this
    }

    fun nextPlayer(nextPlayer: BattlePlayer?): BattleUpdate {
        updateNextPlayer = true
        this.nextPlayer = nextPlayer
        return this
    }

    fun incrementTurn(incrementTurn: Boolean): BattleUpdate {
        this.incrementTurn = incrementTurn
        return this
    }

    fun updateBattle(battle: Battle): Battle {
        val newStatus = computeNewStatus(battle)
        val newStartTime = computeNewStartTime(battle)
        val newStopTime = computeNewStopTime(battle)
        val newNextPlayer = computeNewNextPlayer(battle)
        val newTurn = computeNewTurn(battle)
        return battle.copy(
            status = newStatus,
            startTime = newStartTime,
            stopTime = newStopTime,
            nextPlayer = newNextPlayer,
            turn = newTurn
        )
    }

    private fun computeNewStatus(battle: Battle): BattleStatus {
        return updateStatus ?: battle.status
    }

    private fun computeNewStartTime(battle: Battle): LocalDateTime? {
        return if (updateStartTime)
            startTime
        else
            return battle.startTime
    }

    private fun computeNewStopTime(battle: Battle): LocalDateTime? {
        return if (updateStopTime)
            stopTime
        else
            battle.stopTime
    }

    private fun computeNewNextPlayer(battle: Battle): BattlePlayer {
        return if (updateNextPlayer)
            nextPlayer!!
        else
            battle.nextPlayer
    }

    private fun computeNewTurn(battle: Battle): Int {
        return if (incrementTurn)
            battle.turn + 1
        else
            battle.turn
    }
}