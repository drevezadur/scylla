/*
 * Copyright (c)  2023-2023.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.drevezadur.scylla.teacher.restserver.service.model

import io.drevezadur.scylla.teacher.restserver.lang.*

data class ShipPojo(
    val battleId: BattleUUID,
    val playerId: PlayerUUID,
    val type: ShipType,
    val status: ShipStructuralStatus,
    val origin: LocationPojo,
    val orientation: GridOrientation,
    val hits: List<LocationPojo>
)