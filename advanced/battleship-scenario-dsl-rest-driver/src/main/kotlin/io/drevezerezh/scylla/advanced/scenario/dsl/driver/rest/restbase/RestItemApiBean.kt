package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.ServerContext
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.engine.RestEngine
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.engine.RestResponse

abstract class RestItemApiBean<E : RestItem, C : Any, U : Any>(
    private val context: ServerContext,
    private val typeReference: TypeReference<E>,
    private val typeReferenceList: TypeReference<List<E>>,
    private val processingListener: RestItemProcessingListener = DefaultRestItemProcessingListener(
        context.baseUrl.substringAfterLast('/')
    ),
    private val restEngine: RestEngine
) : RestItemApi<E, C, U> {

    private val objectMapper: ObjectMapper = ObjectMapper().registerModules(JavaTimeModule(), KotlinModule.Builder().build())


    override fun create(itemCreation: C): String {
        restEngine.postJson(context.baseUrl, itemCreation, false).apply {
            return when (code) {
                201 -> creationExtractIds(this, body)
                else -> creationFailure(this, body)
            }
        }
    }

    private fun creationExtractIds(response: RestResponse, bodyAsString: String): String {
        val playerId = response.headers["Location"]!!.substringAfterLast('/')
        processingListener.onCreationSuccess(response.code, bodyAsString, playerId)
        return playerId
    }

    private fun creationFailure(response: RestResponse, bodyAsString: String): Nothing {
        processingListener.onCreationFailure(response.code, bodyAsString)
    }


    override fun findById(itemId: String): E? {
        restEngine.get("${context.baseUrl}/$itemId").let {
            return when (it.code) {
                200 -> findExtractItem(it, itemId)
                404 -> findNoItem(it, itemId)
                else -> processingListener.onGetFailure(it.code, itemId)
            }
        }
    }


    private fun findExtractItem(response: RestResponse, itemId: String): E {
        try {

            val item = objectMapper.readValue(response.body, typeReference)
            processingListener.onGetSuccess(response.code, itemId)
            return item

        } catch (ex: RuntimeException) {
            processingListener.onGetFailure(response.code, itemId, ex)
        }
    }


    private fun findNoItem(response: RestResponse, itemId: String): E? {
        processingListener.onGetSuccess(response.code, itemId)
        return null
    }


    override fun getAllIds(): List<String> {
        restEngine.get(context.baseUrl).apply {
            return when (code) {
                200 -> getAllIdsExtractResponse(this)
                else -> getAllFailed(this)
            }
        }
    }

    private fun getAllIdsExtractResponse(it: RestResponse): List<String> {
        try {

            return objectMapper.readValue(it.body, object : TypeReference<List<String>>() {})

        } catch (ex: RuntimeException) {
            processingListener.onGetAllFailure(it.code, ex)
        }
    }

    private fun getAllFailed(response: RestResponse): Nothing {
        processingListener.onGetAllFailure(response.code)
    }


    override fun getAll(): List<E> {
        restEngine.get("${context.baseUrl}?style=full").apply {
            return when (code) {
                200 -> getAllExtractItems(this)
                else -> getAllFailed(this)
            }
        }
    }

    private fun getAllExtractItems(response: RestResponse): List<E> {
        try {

            return objectMapper.readValue(response.body, typeReferenceList)

        } catch (ex: RuntimeException) {
            processingListener.onGetAllFailure(response.code, ex)
        }
    }


    override fun update(itemId: String, itemUpdate: U): E {
        val bodyAsString = objectMapper.writeValueAsString(itemUpdate)
        restEngine.putJson("${context.baseUrl}/$itemId", bodyAsString).apply {
            return when (code) {
                200 -> updateExtractItem(this, itemId, bodyAsString)
                else -> updateFailed(this, itemId, bodyAsString)
            }
        }
    }

    private fun updateExtractItem(response: RestResponse, itemId: String, bodyAsString: String): E {
        try {

            val updatedItem = objectMapper.readValue(response.body, typeReference)
            processingListener.onUpdateSuccess(response.code, itemId, bodyAsString)
            return updatedItem

        } catch (ex: RuntimeException) {
            processingListener.onUpdateFailure(response.code, itemId, bodyAsString, ex)
        }
    }

    private fun updateFailed(response: RestResponse, itemId: String, bodyAsString: String): Nothing {
        processingListener.onUpdateFailure(response.code, itemId, bodyAsString)
    }


    override fun delete(itemId: String) {
        restEngine.deleteJson("${context.baseUrl}/$itemId").let {
            when (it.code) {
                204 -> processingListener.onDeleteSuccess(it.code, itemId)
                else -> processingListener.onDeleteFailure(it.code, itemId)
            }
        }
    }

    override fun deleteAll() {
        getAllIds().forEach(::delete)
    }
}