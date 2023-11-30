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

package io.drevezadur.scylla.teacher.restserver.persist

import io.drevezadur.scylla.teacher.restserver.persist.model.FleetId
import io.drevezadur.scylla.teacher.restserver.persist.model.FleetPEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FleetRepository : CrudRepository<FleetPEntity, FleetId> {

    // FIXME repository.query.NamedQuery - Did not find named query

    fun findByBattleIdAndPlayerId(battleId: UUID, playerId: UUID): FleetPEntity?

//    @Query("SELECT f FROM Fleet f WHERE f.battleId = :battleId AND f.playerId = :playerId")
//    fun getFleet(
//        @Param("battleId") battleId: UUID,
//        @Param("playerId") playerId: UUID
//    ): FleetPEntity?

    fun findAllByBattleId(battleId: UUID): List<FleetPEntity>


    @Query("SELECT f.playerId FROM Fleet f WHERE f.battleId = :battleId AND f.playerId != :playerId")
    fun getOpponentIdOf(
        @Param("battleId") battleId: UUID,
        @Param("playerId") playerId: UUID
    ): UUID?

//    @Query("SELECT f FROM Fleet f WHERE f.battleId = :battleId AND f.playerId != :playerId")
//    fun findOpponentOf(
//        @Param("battleId") battleId: UUID,
//        @Param("playerId") playerId: UUID
//    ): FleetPEntity?

}