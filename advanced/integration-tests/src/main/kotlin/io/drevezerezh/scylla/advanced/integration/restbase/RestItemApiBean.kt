package io.drevezerezh.scylla.advanced.integration.restbase

import com.fasterxml.jackson.core.type.TypeReference
import io.drevezerezh.scylla.advanced.integration.ServerContext
import io.drevezerezh.scylla.advanced.integration.restbase.HttpHelper.JSON
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

abstract class RestItemApiBean<E : RestItem, C, U>(
    private val context: ServerContext,
    private val typeReference: TypeReference<E>,
    private val typeReferenceList: TypeReference<List<E>>,
    private val processingListener: RestItemProcessingListener = DefaultRestItemProcessingListener(
        context.baseUrl.substringAfterLast('/')
    )
) : RestItemApi<E, C, U> {


    override fun create(itemCreation: C): String {
        val bodyAsString = context.objectMapper.writeValueAsString(itemCreation)

        val request = Request.Builder().url(context.baseUrl)
            .post(bodyAsString.toRequestBody(JSON))
            .build()

        context.client.newCall(request).execute().use {
            if (it.code == 201) {
                val playerId = it.headers["location"]!!.substringAfterLast('/')
                processingListener.onCreationSuccess(it.code, bodyAsString, playerId)
                return playerId
            } else {
                processingListener.onCreationFailure(it.code, bodyAsString)
            }
        }
    }


    override fun findById(itemId: String): E? {
        val request = Request.Builder().url("${context.baseUrl}/$itemId")
            .build()

        context.client.newCall(request).execute().use {
            when (it.code) {
                200 -> try {
                    val content = it.body!!.string()
                    val item = context.objectMapper.readValue(content, typeReference)
                    processingListener.onGetSuccess(it.code, itemId)
                    return item
                } catch (ex: RuntimeException) {
                    processingListener.onGetFailure(it.code, itemId, ex)
                }

                404 -> {
                    processingListener.onGetSuccess(it.code, itemId)
                    return null
                }

                else -> processingListener.onGetFailure(it.code, itemId)
            }
        }
    }

    override fun getAllIds(): List<String> {
        val request = Request.Builder().url(context.baseUrl).build()

        context.client.newCall(request).execute().use {
            if (it.code == 200) {
                try {
                    val content = it.body!!.string()
                    return context.objectMapper.readValue(content, object : TypeReference<List<String>>() {})
                } catch (ex: RuntimeException) {
                    processingListener.onGetAllFailure(it.code, ex)
                }
            } else {
                processingListener.onGetAllFailure(it.code)
            }
        }
    }


    override fun getAll(): List<E> {
        val request = Request.Builder().url("${context.baseUrl}?style=full").build()
        context.client.newCall(request).execute().use {
            if (it.code == 200) {
                try {
                    val content = it.body!!.string()
                    return context.objectMapper.readValue(content, typeReferenceList)
                } catch (ex: RuntimeException) {
                    processingListener.onGetAllFailure(it.code, ex)
                }
            } else {
                processingListener.onGetAllFailure(it.code)
            }
        }
    }


    override fun update(itemId: String, itemUpdate: U): E {
        val bodyAsString = context.objectMapper.writeValueAsString(itemUpdate)

        val request = Request.Builder().url(context.baseUrl + "/$itemId")
            .put(bodyAsString.toRequestBody(JSON))
            .build()

        context.client.newCall(request).execute().use {
            if (it.code == 200) {
                try {
                    val content = it.body!!.string()
                    val updatedItem = context.objectMapper.readValue(content, typeReference)
                    processingListener.onUpdateSuccess(it.code, itemId, bodyAsString)
                    return updatedItem
                } catch (ex: RuntimeException) {
                    processingListener.onUpdateFailure(it.code, itemId, bodyAsString, ex)
                }
            } else {
                processingListener.onUpdateFailure(it.code, itemId, bodyAsString)
            }
        }
    }

    override fun delete(itemId: String) {
        val request = Request.Builder().url(context.baseUrl + "/$itemId")
            .delete()
            .build()

        context.client.newCall(request).execute().use {
            if (it.code == 204)
                processingListener.onDeleteSuccess(it.code, itemId)
            else
                processingListener.onDeleteFailure(it.code, itemId)
        }
    }

    override fun deleteAll() {
        getAllIds().forEach(::delete)
    }
}