package io.drevezerezh.scylla.advanced.integration.player

import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerUpdate(
    @JsonProperty("name")
    val name: String? = null
)