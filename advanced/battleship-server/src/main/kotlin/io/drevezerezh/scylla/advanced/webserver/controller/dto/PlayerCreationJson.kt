package io.drevezerezh.scylla.advanced.webserver.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerCreationJson (
    @JsonProperty("name")
    var name : String
)