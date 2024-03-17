package io.drevezerezh.scylla.advanced.webserver.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class PlayerJson(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name : String
)
