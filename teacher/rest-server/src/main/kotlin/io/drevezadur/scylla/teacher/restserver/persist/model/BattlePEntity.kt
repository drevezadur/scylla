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

import io.drevezadur.scylla.teacher.restserver.lang.BattleStatus
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID
import jakarta.persistence.*
import java.io.Serializable

@Entity(name = "Battle")
@Table(name = "battles")
class BattlePEntity(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    var id: BattleUUID? = null,

    @Column(name = "player1", nullable = false)
    var player1Id: PlayerUUID? = null,

    @Column(name = "player2", nullable = false)
    var player2Id: PlayerUUID? = null,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: BattleStatus = BattleStatus.DEPLOYMENT,

    @Column(name = "shooterplayer", nullable = false)
    var shooterPlayer: PlayerUUID? = null,

    @Column(name = "winner", nullable = true)
    val winner: PlayerUUID? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BattlePEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}