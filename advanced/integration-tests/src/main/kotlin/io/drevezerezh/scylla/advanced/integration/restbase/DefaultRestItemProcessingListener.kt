package io.drevezerezh.scylla.advanced.integration.restbase

import org.slf4j.LoggerFactory

class DefaultRestItemProcessingListener(
    urlFragment: String
) : RestItemProcessingListener {

    private val urlPath: String = toUrlPath(urlFragment)

    override fun onCreationSuccess(httpCode: Int, requestBody: String, itemId: String) {
        LOGGER.info("POST $urlPath $requestBody : code=$httpCode : creation success")
    }

    override fun onCreationFailure(httpCode: Int, requestBody: String, ex: RuntimeException?): Nothing {
        LOGGER.error("POST $urlPath $requestBody : code=$httpCode : creation failure")
        throw RequestFailureException(httpCode, "Fail to create item", ex)
    }

    override fun onGetSuccess(httpCode: Int, itemId: String) {
        LOGGER.info("GET $urlPath/$itemId : code=$httpCode : get item")
    }

    override fun onGetFailure(httpCode: Int, itemId: String, ex: RuntimeException?): Nothing {
        LOGGER.error("GET $urlPath/$itemId : code=$httpCode : get item failed")
        throw RequestFailureException(httpCode, "Failed to get item '$itemId'", ex)
    }

    override fun onGetAllSuccess(httpCode: Int) {
        LOGGER.info("GET $urlPath : code=$httpCode : get all success")
    }

    override fun onGetAllFailure(httpCode: Int, ex: RuntimeException?): Nothing {
        LOGGER.error("GET $urlPath : code=$httpCode : get all failure", ex)
        throw RequestFailureException(httpCode, "Fail to get all items", ex)
    }

    override fun onUpdateSuccess(httpCode: Int, itemId: String, requestBody: String) {
        LOGGER.info("PUT $urlPath/$itemId $requestBody : code=$httpCode : item updated")
    }

    override fun onUpdateFailure(httpCode: Int, itemId: String, requestBody: String, ex: RuntimeException?): Nothing {
        LOGGER.error("PUT $urlPath/$itemId $requestBody : code=$httpCode : fail to update item", ex)
        throw RequestFailureException(httpCode, "Fail to get item '$itemId'", ex)
    }

    override fun onDeleteSuccess(httpCode: Int, itemId: String) {
        LOGGER.info("DELETE $urlPath/$itemId : code=$httpCode : item deleted")
    }

    override fun onDeleteFailure(httpCode: Int, itemId: String, ex: RuntimeException?): Nothing {
        LOGGER.error("DELETE $urlPath/$itemId : code=$httpCode : item deletion failed")
        throw RequestFailureException(httpCode, "Fail to delete item '$itemId'", ex)
    }

    companion object {
        private fun toUrlPath(urlFragment: String): String {
            if (urlFragment.isEmpty())
                return urlFragment
            if (!urlFragment.startsWith("/"))
                return "/$urlFragment"
            return urlFragment
        }

        private val LOGGER = LoggerFactory.getLogger(DefaultRestItemProcessingListener::class.java)
    }
}