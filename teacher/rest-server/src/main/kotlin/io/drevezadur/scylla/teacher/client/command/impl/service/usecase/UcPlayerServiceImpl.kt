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

package io.drevezadur.scylla.teacher.client.command.impl.service.usecase

import io.drevezadur.scylla.teacher.client.command.GameCommandException
import io.drevezadur.scylla.teacher.client.command.impl.service.PlayerService
import io.drevezadur.scylla.teacher.restserver.domain.usecase.PlayerUseCaseManager
import io.drevezadur.scylla.teacher.restserver.service.model.PlayerMapping
import io.drevezadur.scylla.teacher.restserver.service.model.PlayerPojo
import java.util.*

class UcPlayerServiceImpl(
    private val playerUseCaseManager: PlayerUseCaseManager
) : PlayerService {


    override fun create(name: String): UUID {
        try {
            return playerUseCaseManager.create(name).id
        } catch (ex: RuntimeException) {
            throw GameCommandException("Fail to create user '$name'")
        }
    }


    override fun delete(id: UUID) {
        playerUseCaseManager.delete(id)
    }


    override fun getAll(): List<PlayerPojo> {
        return playerUseCaseManager.getAllPlayers()
            .map(PlayerMapping::toPojo)
    }

    override fun findById(id: UUID): PlayerPojo? {
        val player = playerUseCaseManager.findById(id) ?: return null
        return PlayerMapping.toPojo(player)
    }
}