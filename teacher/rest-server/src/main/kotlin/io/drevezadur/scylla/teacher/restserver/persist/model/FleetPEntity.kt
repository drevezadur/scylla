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

import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.FleetStatus
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID
import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity(name = "Fleet")
@Table(name = "fleets")
@IdClass(FleetId::class)
class FleetPEntity(

    @Id
    @Column(name = "battleid", nullable = false)
    var battleId: BattleUUID? = null,

    @Id
    @Column(name = "playerid", nullable = false)
    var playerId: PlayerUUID? = null,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: FleetStatus = FleetStatus.NOT_DEPLOYED,

    @Column(name = "shots")
    @Size(min = 0, max = 200)
    var shotStorage: String = ""

) {
    constructor(battleId: BattleUUID, playerId: PlayerUUID, status: FleetStatus, shots: List<Location>)
            : this(battleId, playerId, status, LocationMapper.toString(shots))

    @Transient
    private var shotLocations: MutableList<Location> = LocationMapper.toLocations(shotStorage).toMutableList()

    fun getShots(): List<Location> = shotLocations

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FleetPEntity) return false
        if (battleId != other.battleId) return false
        if (playerId != other.playerId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = battleId?.hashCode() ?: 0
        result = 31 * result + (playerId?.hashCode() ?: 0)
        return result
    }
}