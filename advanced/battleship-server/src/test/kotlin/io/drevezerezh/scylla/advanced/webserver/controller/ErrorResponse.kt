package io.drevezerezh.scylla.advanced.webserver.controller

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorResponse(
    @JsonProperty("status")
    var status: Int,
    @JsonProperty("type")
    var type: String? = null,
    @JsonProperty("title")
    var title: String? = null,
    @JsonProperty("detail")
    var detail: String? = null,
    @JsonProperty("instance")
    var instance: String? = null,
    @JsonProperty("timestamp")
    var timestamp: String? = null,
    @JsonProperty("attributes")
    var attributes: Set<String>? = null
)
