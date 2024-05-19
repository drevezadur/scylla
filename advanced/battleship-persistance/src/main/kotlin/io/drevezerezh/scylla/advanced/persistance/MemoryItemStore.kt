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
import com.fasterxml.jackson.databind.ObjectMapper
import io.drevezerezh.scylla.advanced.domain.spi.ItemStore
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

abstract class MemoryItemStore<K, V, J> : ItemStore<K, V> {
    private val readWriteLock = ReentrantReadWriteLock()
    private val itemMap = HashMap<K, J>()

    override fun save(item: V) {
        val persisted = toPersistance(item)
        val persistedId = extractDomainId(item)

        var similarKeyPersistedItems: List<J> = emptyList()

        readWriteLock.read {
            similarKeyPersistedItems = itemMap.values
                .filter {
                    persistedId != extractPersistanceId(it)
                            && hasSameDomainKey(item, it)
                }

            if (similarKeyPersistedItems.isEmpty()) {
                readWriteLock.write {
                    itemMap[extractPersistanceId(persisted)] = persisted
                }
            }
        }

        if (similarKeyPersistedItems.isNotEmpty()) {
            val similarDomainItems = similarKeyPersistedItems.map(this::toDomain).toSet()
            failToSave(item, similarDomainItems)
        }
    }

    protected abstract fun extractDomainId(domain: V): K

    protected abstract fun extractPersistanceId(persistance: J): K

    protected abstract fun toPersistance(domain: V): J

    protected open fun hasSameDomainKey(original: V, persisted: J): Boolean {
        return extractDomainId(original) == extractPersistanceId(persisted)
    }

    protected open fun failToSave(itemToSave: V, duplicatedDomainKeyItems: Set<V>): Nothing {
        val id = extractDomainId(itemToSave)
        throw IllegalStateException("Similar items have been detected. Id: $id")
    }

    protected abstract fun toDomain(persistance: J): V

    protected abstract fun itemNotFound(id: K): Nothing

    protected abstract fun getTypeReferenceList(): TypeReference<List<J>>

    override fun deleteById(id: K): Boolean {
        readWriteLock.write {
            return itemMap.remove(id) != null
        }
    }


    override fun deleteAll() {
        readWriteLock.write {
            itemMap.clear()
        }
    }


    override fun getById(id: K): V {
        val persistedShip = readWriteLock.read {
            itemMap[id]
        }

        if (persistedShip == null)
            itemNotFound(id)

        return toDomain(persistedShip)
    }


    override fun contains(id: K): Boolean {
        readWriteLock.read {
            return itemMap.contains(id)
        }
    }


    override fun saveAll(vararg items: V) {
        items.forEach(this::save)
    }


    override fun getAll(): List<V> {
        return itemMap.values.map(this::toDomain).toList()
    }

    fun getAll(predicate: (J) -> Boolean): List<V> {
        val persistedItems = readWriteLock.read {
            itemMap.values
                .filter(predicate)
        }

        return persistedItems
            .map(this::toDomain)
            .toList()
    }

    fun exportToJson(): String {
        val persistedPlayerList = readWriteLock.read { itemMap.values.toList() }
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(persistedPlayerList)
    }


    fun importJson(content: String) {
        val objectMapper = ObjectMapper()
        val newPersistedPlayerList: List<J> = objectMapper.readValue(
            content,
            getTypeReferenceList()
        )

        readWriteLock.write {
            itemMap.clear()
            itemMap.putAll(newPersistedPlayerList.associateBy(this::extractPersistanceId))
        }
    }
}