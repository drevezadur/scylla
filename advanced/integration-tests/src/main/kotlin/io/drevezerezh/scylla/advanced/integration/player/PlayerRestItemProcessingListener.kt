package io.drevezerezh.scylla.advanced.integration.player

import io.drevezerezh.scylla.advanced.integration.restbase.RequestFailureException
import io.drevezerezh.scylla.advanced.integration.restbase.RestItemProcessingListener
import org.slf4j.LoggerFactory

class PlayerRestItemProcessingListener : RestItemProcessingListener {
    override fun onCreationSuccess(httpCode: Int, requestBody: String, itemId: String) {
        LOGGER.info("POST /players $requestBody : create player")
    }

    override fun onCreationFailure(httpCode: Int, requestBody: String, ex: RuntimeException?): Nothing {
        LOGGER.error("POST /players $requestBody : fail to create player")
        throw RequestFailureException(httpCode, "Failed to create player", ex)
    }

    override fun onGetSuccess(httpCode: Int, itemId: String) {
        LOGGER.info("GET /players/$itemId  : player found")
    }

    override fun onGetFailure(httpCode: Int, itemId: String, ex: RuntimeException?): Nothing {
        LOGGER.error("GET /players/$itemId : player not found")
        throw PlayerNotFoundException(itemId, httpCode, "Cannot found player with id '$itemId'")
    }

    override fun onGetAllSuccess(httpCode: Int) {
        LOGGER.info("GET /players : success")
    }

    override fun onGetAllFailure(httpCode: Int, ex: RuntimeException?): Nothing {
        LOGGER.error("GET /players : failure, code=$httpCode")
        throw RequestFailureException(httpCode, "Fail to get all items", ex)
    }

    override fun onUpdateSuccess(httpCode: Int, itemId: String, requestBody: String) {
        LOGGER.info("PUT /players/$itemId $requestBody : player updated")
    }

    override fun onUpdateFailure(httpCode: Int, itemId: String, requestBody: String, ex: RuntimeException?): Nothing {
        LOGGER.warn("PUT /players/$httpCode $requestBody : fail to update player")
        throw PlayerUpdateFailureException(itemId, httpCode, "Cannot update player with id '$itemId'")
    }

    override fun onDeleteSuccess(httpCode: Int, itemId: String) {
        LOGGER.info("DELETE /players/$itemId : player deleted")
    }

    override fun onDeleteFailure(httpCode: Int, itemId: String, ex: RuntimeException?): Nothing {
        LOGGER.warn("DELETE /players/$itemId : code=$httpCode : fail to delete player")
        throw PlayerDeletionFailureException(itemId, httpCode, "Fail to delete player with id '$itemId'")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PlayerRestItemProcessingListener::class.java)
    }
}