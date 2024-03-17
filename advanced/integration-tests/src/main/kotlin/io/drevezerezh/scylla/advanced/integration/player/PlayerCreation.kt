package io.drevezerezh.scylla.advanced.integration.player

import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerCreation(
    @JsonProperty("name")
    val name : String=""
)