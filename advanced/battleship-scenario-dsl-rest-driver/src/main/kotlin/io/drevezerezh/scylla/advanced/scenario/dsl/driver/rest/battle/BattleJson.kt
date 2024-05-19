package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle

import com.fasterxml.jackson.annotation.JsonProperty
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.RestItem
import io.drevezerezh.scylla.advanced.lang.BattleStatus
import java.time.LocalDateTime

data class BattleJson(
    @JsonProperty("id")
    override val id: String = "",
    @JsonProperty("player1Id")
    val player1Id: String,
    @JsonProperty("player2Id")
    val player2Id: String,
    @JsonProperty("creationTime")
    val creationTime: LocalDateTime,
    @JsonProperty("startTime")
    val startTime: LocalDateTime?,
    @JsonProperty("stopTime")
    val stopTime: LocalDateTime?,
    @JsonProperty("nextPlayer")
    val nextPlayer: BattlePlayer,
    @JsonProperty("status")
    val status: BattleStatus = BattleStatus.DEPLOYMENT,
    @JsonProperty("turn")
    val turn: Int,
) : RestItem