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

package io.drevezerezh.scylla.advanced.domain.impl.usecase

import io.drevezerezh.scylla.advanced.domain.api.player.Player
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerCreation
import io.drevezerezh.scylla.advanced.domain.impl.PlayerManager
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerUpdate
import io.drevezerezh.scylla.advanced.domain.api.usecase.PlayerUseCaseManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlayerUseCaseManagerBean(
    @Autowired
    private val playerManager: PlayerManager
) : PlayerUseCaseManager {

    override fun create(creation: PlayerCreation): Player {
        return playerManager.createPlayer(creation)
    }

    override fun update(playerId: String, update: PlayerUpdate): Player {
        return playerManager.update(playerId, update)
    }

    override fun delete(playerId: String): Boolean {
        return playerManager.deletePlayer(playerId)
    }

    override fun getAll(): List<Player> {
        return playerManager.getAllPlayers()
    }

    override fun getById(playerId: String): Player {
        return playerManager.getPlayerById(playerId)
    }
}