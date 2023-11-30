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

package io.drevezadur.scylla.teacher.restserver.domain.internal.usecase

import io.drevezadur.scylla.teacher.restserver.domain.PlayerNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.PlayerDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.PlayerStore
import io.drevezadur.scylla.teacher.restserver.domain.usecase.PlayerUseCaseManager
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlayerUseCaseManagerImpl(
    @Autowired
    private val playerStore: PlayerStore
) : PlayerUseCaseManager {

    override fun create(name: String): PlayerDEntity {
        return playerStore.create(name)
    }

    override fun getById(playerId: PlayerUUID): PlayerDEntity {
        return playerStore.findById(playerId) ?: throw PlayerNotFoundException(
            playerId,
            "Cannot found player $playerId"
        )
    }

    override fun findById(playerId: PlayerUUID): PlayerDEntity? {
        return playerStore.findById(playerId)
    }

    override fun delete(playerId: PlayerUUID) {
        playerStore.delete(playerId)
    }

    override fun getAllPlayers(): List<PlayerDEntity> {
        return playerStore.getAll()
    }
}