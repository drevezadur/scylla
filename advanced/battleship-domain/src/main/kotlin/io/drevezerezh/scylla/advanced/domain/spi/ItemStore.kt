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

interface ItemStore<K, V> {

    /**
     * Save an item into the store
     * @param item the item to store
     */
    fun save(item: V)

    /**
     * Save several items into the store
     * @param items the items to store
     */
    fun saveAll(vararg items: V)


    /**
     * Check if an item is stored
     * @param id the item identifier
     * @return true when the item is stored, false otherwise
     */
    fun contains(id: K): Boolean

    /**
     * Get a stored item from its identifier
     * @param id the identifier of the item
     * @return the stored item
     */
    fun getById(id: K): V

    /**
     * Get all items
     * @return the list of the items in the store
     */
    fun getAll(): List<V>


    /**
     * Delete an item
     *
     * Does nothing when item does not exist
     *
     * @param id the identifier of the item
     * @return true if the item was existing, false otherwise
     */
    fun deleteById(id: K): Boolean

    /**
     * Delete all stored items
     */
    fun deleteAll()

}