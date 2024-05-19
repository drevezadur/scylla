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

package io.drevezerezh.scylla.advanced.persistance

import com.fasterxml.jackson.core.type.TypeReference
import io.drevezerezh.scylla.advanced.domain.api.player.NamedPlayerNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.player.Player
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerAlreadyExistException
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerNotFoundException
import io.drevezerezh.scylla.advanced.domain.spi.PlayerStore
import org.springframework.stereotype.Component

@Component
class MemoryPlayerStoreBean : MemoryItemStore<String, Player, PlayerPJson>(), PlayerStore {

    override fun extractDomainId(domain: Player): String = domain.id

    override fun extractPersistanceId(persistance: PlayerPJson): String = persistance.id

    override fun toPersistance(domain: Player): PlayerPJson = PlayerMapper.toPersistance(domain)

    override fun toDomain(persistance: PlayerPJson): Player = PlayerMapper.toDomain(persistance)

    override fun itemNotFound(id: String): Nothing = throw PlayerNotFoundException(id)

    override fun getTypeReferenceList(): TypeReference<List<PlayerPJson>> =
        object : TypeReference<List<PlayerPJson>>() {}

    override fun hasSameDomainKey(original: Player, persisted: PlayerPJson): Boolean {
        return original.name == persisted.name
    }

    override fun failToSave(itemToSave: Player, duplicatedDomainKeyItems: Set<Player>): Nothing {
        throw PlayerAlreadyExistException(itemToSave.id, setOf("name"))
    }

    override fun getByName(name: String): Player {
        return getAll { it.name == name }.firstOrNull() ?: throw NamedPlayerNotFoundException(name)
    }

    override fun containsName(name: String): Boolean {
        return getAll { it.name == name }.isNotEmpty()
    }
}