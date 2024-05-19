package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest

import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.engine.RestEngine
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.HttpHelper

data class ServerContext(
    val restEngine: RestEngine,
    val baseUrl: String = ""
) {
    fun completePath(pathFragment: String): ServerContext {
        val newUrl = HttpHelper.mergeUrl(baseUrl, pathFragment)
        return ServerContext(restEngine, newUrl)
    }
}