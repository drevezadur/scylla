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

package io.drevezadur.scylla.teacher.restserver.domain.model

import io.drevezadur.scylla.teacher.restserver.lang.GridOrientation
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.ShipStructuralStatus
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class ShipMappingTest {


    @Test
    fun `toLocations() shall handle valid horizontal ship`() {
        val locations = ShipMapping.toLocations(
            Location(1, 3),
            GridOrientation.ROW,
            ShipType.SUBMARINE
        )

        assertThat(locations)
            .containsOnly(
                Location(1, 3),
                Location(2, 3),
                Location(3, 3)
            )
    }

    @Test
    fun `toLocations() shall handle valid vertical ship`() {
        val locations = ShipMapping.toLocations(
            Location(1, 3),
            GridOrientation.COLUMN,
            ShipType.SUBMARINE
        )

        assertThat(locations)
            .containsOnly(
                Location(1, 3),
                Location(1, 4),
                Location(1, 5)
            )
    }

    @Test
    fun toPersistedShip() {
        val battleId = UUID.randomUUID()
        val playerId = UUID.randomUUID()
        val creation = ShipCreation(
            battleId, playerId,
            ShipType.CRUISER,
            Location(2, 7),
            GridOrientation.COLUMN
        )

        assertThat(
            ShipMapping.toPersistedShip(
                creation
            )
        ).extracting("battleId", "playerId", "type", "status", "origin", "orientation", "hits")
            .containsExactly(
                battleId,
                playerId,
                ShipType.CRUISER,
                ShipStructuralStatus.UNHARMED,
                "27",
                GridOrientation.COLUMN,
                ""
            )
    }
}