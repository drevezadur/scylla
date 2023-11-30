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

package io.drevezadur.scylla.teacher.restserver.persist.store

import io.drevezadur.scylla.teacher.restserver.common.UuidProvider
import io.drevezadur.scylla.teacher.restserver.domain.EntitySaveException
import io.drevezadur.scylla.teacher.restserver.domain.PlayerNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.PlayerDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.PlayerStore
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID
import io.drevezadur.scylla.teacher.restserver.persist.PlayerRepository
import io.drevezadur.scylla.teacher.restserver.persist.model.PlayerPEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class PlayerStoreBean(
    private val repository: PlayerRepository,
    private val uuidProvider: UuidProvider
) : PlayerStore {

    override fun create(name: String): PlayerDEntity {
        val user = PlayerPEntity(uuidProvider.create(), name)
        try {

            val createdUserP = repository.save(user)
            return toPlayer(createdUserP)

        } catch (ex: DataIntegrityViolationException) {
            analyseCreationFailure(ex, user)
        }
    }

    private fun toPlayer(userP: PlayerPEntity): PlayerDEntity {
        return PlayerDEntity(userP.id!!, userP.name)
    }

    private fun analyseCreationFailure(ex: DataIntegrityViolationException, user: PlayerPEntity): Nothing {
        val sameNameUser = repository.findByName(user.name)
        if (sameNameUser != null)
            throw EntitySaveException(
                "USER_SAVE_NAME_ALREADY_EXIST",
                arguments = listOf(sameNameUser.name),
                message = "User with name ${sameNameUser.name} already exists", ex
            )

        throw EntitySaveException(
            "USER_SAVE_FAILURE",
            message = "Fail to save user",
            cause = ex
        )
    }


    override fun findById(id: PlayerUUID): PlayerDEntity? {
        val persisted = repository.findById(id).getOrNull() ?: return null
        return toPlayer(persisted)
    }

    override fun getById(id: PlayerUUID): PlayerDEntity {
        val persisted =
            repository.findById(id).getOrNull() ?: throw PlayerNotFoundException(id, "Cannot found player $id")
        return toPlayer(persisted)
    }

    override fun getAll(): List<PlayerDEntity> {
        return repository.findAll().map(this::toPlayer)
    }

    override fun delete(id: PlayerUUID) {
        repository.deleteById(id)
    }
}