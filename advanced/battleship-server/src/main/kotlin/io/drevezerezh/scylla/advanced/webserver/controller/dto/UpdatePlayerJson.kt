package io.drevezerezh.scylla.advanced.webserver.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdatePlayerJson(
    @JsonProperty("name")
    var name : String
)
