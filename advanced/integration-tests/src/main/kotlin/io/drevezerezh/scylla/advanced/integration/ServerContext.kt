package io.drevezerezh.scylla.advanced.integration

import com.fasterxml.jackson.databind.ObjectMapper
import io.drevezerezh.scylla.advanced.integration.restbase.HttpHelper
import okhttp3.OkHttpClient

data class ServerContext(
    val client: OkHttpClient,
    val baseUrl: String,
    val objectMapper: ObjectMapper = ObjectMapper()
) {
    fun completePath(pathFragment: String): ServerContext {
        val newUrl = HttpHelper.mergeUrl(baseUrl, pathFragment)
        return ServerContext(
            client,
            newUrl,
            objectMapper
        )
    }
}