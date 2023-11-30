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

package io.drevezadur.scylla.teacher.restserver.domain.internal

import io.drevezadur.scylla.teacher.restserver.domain.model.ShotReport
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.PlayerUUID

interface ShotResolver {

    /**
     * Resolve a shot
     * @param battleId the battle identifier
     * @param fromPlayerId the shooting player identifier
     * @param targetLocation the target location of the shot
     * @return the report of the shot
     */
    fun resolve(battleId: BattleUUID, fromPlayerId: PlayerUUID, targetLocation: Location): ShotReport

}
