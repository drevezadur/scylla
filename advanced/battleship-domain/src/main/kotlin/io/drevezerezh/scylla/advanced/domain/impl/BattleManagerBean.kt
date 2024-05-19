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

package io.drevezerezh.scylla.advanced.domain.impl

import io.drevezerezh.scylla.advanced.domain.api.battle.Battle
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleCreation
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleUpdate
import io.drevezerezh.scylla.advanced.domain.api.player.Player
import io.drevezerezh.scylla.advanced.domain.spi.BattleStore
import io.drevezerezh.scylla.advanced.domain.spi.PlayerStore
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BattleManagerBean(
    private val idProvider: IdProvider,
    private val battleStore: BattleStore,
    private val timeProvider: TimeProvider,
    private val playerStore: PlayerStore
) : BattleManager {

    override fun createBattle(battleCreation: BattleCreation): Battle {
        val players = loadPlayers(battleCreation)

        val creation = battleCreation.creation ?: timeProvider.nowAsLocalDateTime()
        val battleId = idProvider.createId()
        val battle = createBattle(battleId, creation, players)

        battleStore.save(battle)
        return battle
    }

    private fun loadPlayers(battleCreation: BattleCreation): List<Player> {
        if (battleCreation.player1Name == battleCreation.player2Name)
            throw IllegalArgumentException("The battle shall be played by two distinct players")

        val player1 = playerStore.getByName(battleCreation.player1Name)
        val player2 = playerStore.getByName(battleCreation.player2Name)
        return mutableListOf(player1, player2)
    }

    private fun createBattle(
        battleId: String,
        creation: LocalDateTime,
        players : List<Player>
    ): Battle {
        return Battle(
            battleId,
            creation,
            player1Id = players[0].id,
            player2Id = players[1].id
        )
    }

    override fun containsBattle(id: String): Boolean {
        return battleStore.contains(id)
    }

    override fun getBattleById(id: String): Battle {
        return battleStore.getById(id)
    }

    override fun getAllBattles(): List<Battle> {
        return battleStore.getAll()
    }

    override fun update(id: String, update: BattleUpdate): Battle {
        val battle = battleStore.getById(id)
        val updatedBattle = update.updateBattle(battle)

        battleStore.save(updatedBattle)
        return updatedBattle
    }


    override fun deleteBattle(id: String): Boolean {
        return battleStore.deleteById(id)
    }
}