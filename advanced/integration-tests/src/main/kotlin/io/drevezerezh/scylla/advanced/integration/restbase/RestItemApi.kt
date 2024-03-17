package io.drevezerezh.scylla.advanced.integration.restbase

interface RestItemApi<E : RestItem, C, U> {

    fun create(itemCreation: C): String

    fun findById(itemId: String): E?

    fun getAllIds(): List<String>

    fun getAll(): List<E>

//    fun <T> getAll(style: String? = null): List<T>

    fun update(itemId: String, itemUpdate: U): E

    fun delete(itemId: String)

    fun deleteAll()
}