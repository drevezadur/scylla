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

import io.drevezerezh.scylla.advanced.domain.api.player.*
import io.drevezerezh.scylla.advanced.domain.spi.PlayerStore
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PlayerManagerBean(
    private val idProvider: IdProvider,
    private val playerStore: PlayerStore
) : PlayerManager {

    override fun createPlayer(playerCreation: PlayerCreation): Player {
        PlayerValidation.checkValidity(playerCreation)
        val id = idProvider.createId()
        val player = Player(id, playerCreation.name)
        playerStore.save(player)
        return player
    }

    override fun containsPlayer(playerId: String): Boolean {
        return playerStore.contains(playerId)
    }

    override fun getPlayerById(playerId: String): Player {
        return playerStore.getById(playerId)
    }

    override fun getAllPlayers(): List<Player> {
        return playerStore.getAll()
    }

    override fun update(id: String, update: PlayerUpdate): Player {
        if( update.isEmpty())
            return playerStore.getById(id)

        PlayerValidation.checkValidity(id, update)

        val previousPlayer = playerStore.getById(id)
        if( previousPlayer.name == update.name)
            return previousPlayer

        val nextPlayer = Player(
            id,
            update.name ?: previousPlayer.name
        )

        playerStore.save(nextPlayer)
        return nextPlayer
    }

    override fun deletePlayer(playerId: String) : Boolean{
        LOGGER.info("deletePlayer($playerId)")
        return playerStore.deleteById(playerId)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PlayerManagerBean::class.java)
    }
}