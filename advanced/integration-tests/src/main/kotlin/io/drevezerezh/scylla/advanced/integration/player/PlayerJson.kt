package io.drevezerezh.scylla.advanced.integration.player

import com.fasterxml.jackson.annotation.JsonProperty
import io.drevezerezh.scylla.advanced.integration.restbase.RestItem

data class PlayerJson(
    @JsonProperty("id")
    override val id: String="",
    @JsonProperty("name")
    val name: String=""
) : RestItem