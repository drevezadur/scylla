/*
 * Copyright (c) 2024 gofannon.xyz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.drevezerezh.scylla.advanced.persistance

import com.fasterxml.jackson.annotation.JsonProperty
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.BattleStatus
import java.time.LocalDateTime

data class BattlePJson(
    @JsonProperty("id")
    val id : String,
    @JsonProperty("creation")
    val creation: LocalDateTime,
    @JsonProperty("startTime", required = false)
    val startTime: LocalDateTime? = null,
    @JsonProperty("stopTime", required = false)
    val stopTime: LocalDateTime? = null,
    @JsonProperty("player1Id")
    val player1Id : String,
    @JsonProperty("player2Id")
    val player2Id : String,
    @JsonProperty("nextPlayer")
    val nextPlayer: BattlePlayer,
    @JsonProperty("status")
    val status: BattleStatus,
    @JsonProperty("turn")
    val turn : Int
)
