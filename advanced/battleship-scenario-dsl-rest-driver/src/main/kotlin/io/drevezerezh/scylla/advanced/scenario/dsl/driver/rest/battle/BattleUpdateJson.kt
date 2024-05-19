package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle

import com.fasterxml.jackson.annotation.JsonProperty

data class BattleUpdateJson(
    @JsonProperty("player1Id")
    val player1Id: String,
    @JsonProperty("player2Id")
    val player2Id: String
)