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

package io.drevezadur.scylla.teacher.restserver.persist.model

import io.drevezadur.scylla.teacher.restserver.lang.*
import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity(name = "Ship")
@Table(name = "ships")
@IdClass(ShipId::class)
class ShipPEntity(

    @Id
    @Column(name = "battleid", nullable = false)
    var battleId: BattleUUID? = null,

    @Id
    @Column(name = "playerid", nullable = false)
    var playerId: PlayerUUID? = null,


    @Id
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    var type: ShipType? = null,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ShipStructuralStatus = ShipStructuralStatus.UNHARMED,

    @Column(name = "origin", nullable = false)
    var origin: String = "",

    @Column(name = "orientation", nullable = false)
    @Enumerated(EnumType.STRING)
    var orientation: GridOrientation = GridOrientation.ROW,

    @Column(name = "hits", nullable = false, length = 10)
    @Size(min = 0, max = 10)
    var hits: String = ""
) {

    constructor(
        battleId: BattleUUID,
        playerId: PlayerUUID,
        type: ShipType,
        status: ShipStructuralStatus,
        origin: Location,
        orientation: GridOrientation,
        hits: List<Location> = emptyList()
    )
            : this(
        battleId,
        playerId,
        type,
        status,
        LocationMapper.toString(origin),
        orientation,
        LocationMapper.toString(hits)
    )


    fun getOriginLocation(): Location {
        return LocationMapper.toLocation(origin)
    }

    fun getHitLocations(): List<Location> {
        return LocationMapper.toLocations(hits)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShipPEntity) return false

        if (playerId != other.playerId) return false
        if (battleId != other.battleId) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = playerId?.hashCode() ?: 0
        result = 31 * result + (battleId?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        return result
    }
}