package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.player

import com.fasterxml.jackson.annotation.JsonProperty

data class PlayerCreationJson(
    @JsonProperty("name")
    val name : String=""
)