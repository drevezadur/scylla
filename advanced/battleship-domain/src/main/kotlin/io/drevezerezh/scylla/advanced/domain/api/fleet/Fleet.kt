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

package io.drevezerezh.scylla.advanced.domain.api.fleet

import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.FleetStatus
import io.drevezerezh.scylla.advanced.lang.GridLocation

/**
 * A fleet of a player in a battle
 */
data class Fleet(
    val battleId: String,
    val player: BattlePlayer,
    val status: FleetStatus = FleetStatus.NOT_DEPLOYED,
    val shots: Set<GridLocation> = HashSet()
) {
    val id: FleetId = FleetId(battleId, player)
}
