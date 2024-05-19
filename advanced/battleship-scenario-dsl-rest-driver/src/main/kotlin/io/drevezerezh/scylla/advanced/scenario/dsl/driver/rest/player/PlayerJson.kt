package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.player

import com.fasterxml.jackson.annotation.JsonProperty
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.RestItem

data class PlayerJson(
    @JsonProperty("id")
    override val id: String="",
    @JsonProperty("name")
    val name: String=""
) : RestItem