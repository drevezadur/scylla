package io.drevezerezh.scylla.advanced.persistance

import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerPJson(
    @JsonProperty("id")
    val id : String,
    @JsonProperty("name")
    val name : String
)
