package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase

interface RestItemProcessingListener {
    fun onCreationSuccess(httpCode: Int, requestBody: String, itemId: String)
    fun onCreationFailure(httpCode: Int, requestBody: String, ex: RuntimeException? = null): Nothing

    fun onGetSuccess(httpCode: Int, itemId: String)
    fun onGetFailure(httpCode: Int, itemId: String, ex: RuntimeException? = null): Nothing

    fun onGetAllSuccess(httpCode: Int)
    fun onGetAllFailure(httpCode: Int, ex: RuntimeException? = null): Nothing

    fun onUpdateSuccess(httpCode: Int, itemId: String, requestBody: String)
    fun onUpdateFailure(httpCode: Int, itemId: String, requestBody: String, ex: RuntimeException? = null): Nothing

    fun onDeleteSuccess(httpCode: Int, itemId: String)
    fun onDeleteFailure(httpCode: Int, itemId: String, ex: RuntimeException? = null): Nothing

}