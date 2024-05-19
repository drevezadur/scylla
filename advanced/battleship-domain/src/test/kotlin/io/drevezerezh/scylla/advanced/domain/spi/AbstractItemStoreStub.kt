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

package io.drevezerezh.scylla.advanced.domain.spi

/**
 * Stub of [BattleStore] for testing purpose
 *
 * This implementation is very simple and does not provide any "protection" such as thread safe
 */
abstract class AbstractItemStoreStub<K, V> : ItemStore<K, V> {

    private val itemMap = mutableMapOf<K, V>()

    override fun save(item: V) {
        itemMap[extractId(item)] = item
    }

    protected abstract fun extractId(item: V): K

    override fun saveAll(vararg items: V) {
        items.forEach(this::save)
    }

    override fun deleteById(id: K): Boolean {
        return itemMap.remove(id) != null
    }

    override fun deleteAll() {
        itemMap.clear()
    }

    override fun getById(id: K): V {
        return itemMap[id] ?: itemAlreadyExists(id)
    }

    protected abstract fun itemAlreadyExists(id: K): Nothing

    override fun contains(id: K): Boolean {
        return itemMap.containsKey(id)
    }

    override fun getAll(): List<V> {
        return itemMap.values.toList()
    }
}