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

package io.drevezerezh.scylla.advanced.webserver.controller

import io.drevezerezh.scylla.advanced.domain.api.ship.ShipDeployment
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.webserver.controller.dto.ShipDeploymentJson


/**
 * Set of mappings methods related to ships
 */
object ShipMapper {

    /**
     * Create de domain ship deployment
     * @param battleId the battle identifier
     * @param player the player in the battle
     * @param dto the ship deployment DTO
     * @return the domain ship deployment
     */
    fun toDomain(battleId: String, player: BattlePlayer, dto: ShipDeploymentJson): ShipDeployment {
        return ShipDeployment(
            battleId = battleId,
            player = player,
            shipType = dto.shipType,
            location = dto.location,
            orientation = dto.orientation
        )
    }

    /**
     * Convert a battle player from string (DTO) format to domain
     * @param dto the battle player in DTO format ('first' or 'second'
     * @return the battle player in domain format
     * @throws IllegalArgumentException when the DTO value is not supported
     */
    fun toDomainBattlePlayer(dto: String): BattlePlayer {
        return when (dto.lowercase()) {
            "first" -> BattlePlayer.FIRST
            "second" -> BattlePlayer.SECOND
            else -> throw IllegalArgumentException("Unknown player value: '$dto'")
        }
    }

}