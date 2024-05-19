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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipId
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation
import io.drevezerezh.scylla.advanced.lang.ShipType

data class ShipPJson(
    @JsonProperty("id")
    val id: ShipId,
    @JsonProperty("battleId")
    val battleId: String,
    @JsonProperty("player")
    val player: BattlePlayer,
    @JsonProperty("type")
    val type: ShipType,
    @JsonProperty("location")
    val location: GridLocation,
    @JsonProperty("orientation")
    val orientation: GridOrientation,
    @JsonProperty("hits")
    val hits: String
) {
    @JsonIgnore
    val fleetId = FleetId(battleId,player)
}
