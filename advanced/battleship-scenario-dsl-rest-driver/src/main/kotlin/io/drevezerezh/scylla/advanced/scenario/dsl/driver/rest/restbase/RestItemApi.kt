package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase

interface RestItemApi<E : RestItem, C, U> {

    fun create(itemCreation: C): String

    fun findById(itemId: String): E?

    fun getAllIds(): List<String>

    fun getAll(): List<E>

    fun update(itemId: String, itemUpdate: U): E

    fun delete(itemId: String)

    fun deleteAll()
}