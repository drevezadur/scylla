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

package io.drevezerezh.scylla.advanced.domain.integration

import io.drevezerezh.scylla.advanced.domain.impl.usecase.UseCaseContext
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.FIRST
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.SECOND
import io.drevezerezh.scylla.advanced.lang.BattleStatus
import io.drevezerezh.scylla.advanced.lang.FleetStatus
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.ShipStatus
import io.drevezerezh.scylla.advanced.lang.ShipType.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IntegrationTest {

    private lateinit var context: UseCaseContext


    @BeforeEach
    fun setUp() {
        context = UseCaseContext()
    }


    @Test
    fun `all players shall have been created`() {
        context.scenario(
            """
            create player John
            create player Jane
        """.trimIndent()
        )

        assertThat(context.hasPlayer("John"))
            .isTrue()
        assertThat(context.hasPlayer("Jane"))
            .isTrue()
    }

    @Test
    fun `battle and fleets shall be created at battle start`() {
        context.scenario(
            """
            create player John
            create player Jane
            start battle with John and Jane
        """.trimIndent()
        )

        assertThat(context.firstBattle())
            .extracting("player1Id", "player2Id", "nextPlayer", "status", "turn")
            .containsExactly(
                context.player("John").id,
                context.player("Jane").id,
                FIRST,
                BattleStatus.DEPLOYMENT,
                0
            )

        assertThat(context.fleet(FIRST))
            .extracting("battleId", "player", "status", "shots")
            .containsExactly(context.firstBattle().id, FIRST, FleetStatus.NOT_DEPLOYED, emptySet<GridLocation>())

        assertThat(context.fleet(SECOND))
            .extracting("battleId", "player", "status", "shots")
            .containsExactly(context.firstBattle().id, SECOND, FleetStatus.NOT_DEPLOYED, emptySet<GridLocation>())
    }


    @Test
    fun `deployed fleet shall be stored`() {
        context.scenario(
            """
            create player John
            create player Jane
            start battle with John and Jane

            John deploy Carrier at A0 on row
            John deploy Battleship at A1 on row
            John deploy Cruiser at A2 on row
            John deploy Submarine at A3 on row
            John deploy Destroyer at A4 on row
        """.trimIndent()
        )

        val battleId = context.firstBattle().id


        assertThat(context.firstBattle())
            .extracting("player1Id", "player2Id", "nextPlayer", "status", "turn")
            .containsExactly(
                context.player("John").id,
                context.player("Jane").id,
                FIRST,
                BattleStatus.DEPLOYMENT,
                0
            )

        assertThat(context.fleet(FIRST))
            .extracting("battleId", "player", "status", "shots")
            .containsExactly(battleId, FIRST, FleetStatus.OPERATIONAL, emptySet<GridLocation>())


        assertThat(context.fleet(SECOND))
            .extracting("battleId", "player", "status", "shots")
            .containsExactly(battleId, SECOND, FleetStatus.NOT_DEPLOYED, emptySet<GridLocation>())

        context.shipsHaveStatus(FIRST, ShipStatus.UNHARMED, CARRIER, BATTLESHIP, CRUISER, SUBMARINE, DESTROYER)
    }


    @Test
    fun `battle shall start when all fleets are deployed`() {
        context.scenario(
            """
            create player John
            create player Jane
            start battle with John and Jane

            John deploy Carrier at A0 on row
            John deploy Battleship at A1 on row
            John deploy Cruiser at A2 on row
            John deploy Submarine at A3 on row
            John deploy Destroyer at A4 on row

            Jane deploy Carrier at A0 on row
            Jane deploy Battleship at A1 on row
            Jane deploy Cruiser at A2 on row
            Jane deploy Submarine at A3 on row
            Jane deploy Destroyer at A4 on row
        """.trimIndent()
        )

        val battleId = context.firstBattle().id


        assertThat(context.firstBattle())
            .extracting("player1Id", "player2Id", "nextPlayer", "status", "turn")
            .containsExactly(
                context.player("John").id,
                context.player("Jane").id,
                FIRST,
                BattleStatus.FIGHTING,
                0
            )

        assertThat(context.fleet(FIRST))
            .extracting("battleId", "player", "status", "shots")
            .containsExactly(battleId, FIRST, FleetStatus.OPERATIONAL, emptySet<GridLocation>())


        assertThat(context.fleet(SECOND))
            .extracting("battleId", "player", "status", "shots")
            .containsExactly(battleId, SECOND, FleetStatus.OPERATIONAL, emptySet<GridLocation>())

        context.shipsHaveStatus(FIRST, ShipStatus.UNHARMED, CARRIER, BATTLESHIP, CRUISER, SUBMARINE, DESTROYER)
        context.shipsHaveStatus(SECOND, ShipStatus.UNHARMED, CARRIER, BATTLESHIP, CRUISER, SUBMARINE, DESTROYER)
    }


    @Test
    fun `first shot missed`() {
        context.scenario(
            """
            create player John
            create player Jane
            start battle with John and Jane

            John deploy Carrier at A0 on row
            John deploy Battleship at A1 on row
            John deploy Cruiser at A2 on row
            John deploy Submarine at A3 on row
            John deploy Destroyer at A4 on row

            Jane deploy Carrier at A0 on row
            Jane deploy Battleship at A1 on row
            Jane deploy Cruiser at A2 on row
            Jane deploy Submarine at A3 on row
            Jane deploy Destroyer at A4 on row
            
            John shot at J9
        """.trimIndent()
        )

        val battleId = context.firstBattle().id


        assertThat(context.firstBattle())
            .extracting("player1Id", "player2Id", "nextPlayer", "status", "turn")
            .containsExactly(
                context.player("John").id,
                context.player("Jane").id,
                SECOND,
                BattleStatus.FIGHTING,
                0
            )

        assertThat(context.fleet(FIRST))
            .extracting("battleId", "player", "status", "shots")
            .containsExactly(battleId, FIRST, FleetStatus.OPERATIONAL, GridLocation.toSet("J9"))


        assertThat(context.fleet(SECOND))
            .extracting("battleId", "player", "status", "shots")
            .containsExactly(battleId, SECOND, FleetStatus.OPERATIONAL, emptySet<GridLocation>())
    }

    @Test
    fun `second shot hit`() {
        context.scenario(
            """
            create player John
            create player Jane
            start battle with John and Jane

            John deploy Carrier at A0 on row
            John deploy Battleship at A1 on row
            John deploy Cruiser at A2 on row
            John deploy Submarine at A3 on row
            John deploy Destroyer at A4 on row

            Jane deploy Carrier at A0 on row
            Jane deploy Battleship at A1 on row
            Jane deploy Cruiser at A2 on row
            Jane deploy Submarine at A3 on row
            Jane deploy Destroyer at A4 on row
            
            John shot at J9
            Jane shot at A0
        """.trimIndent()
        )

        val battleId = context.firstBattle().id


        assertThat(context.firstBattle())
            .extracting("player1Id", "player2Id", "nextPlayer", "status", "turn")
            .containsExactly(
                context.player("John").id,
                context.player("Jane").id,
                FIRST,
                BattleStatus.FIGHTING,
                1
            )

        assertThat(context.fleet(FIRST))
            .extracting("battleId", "player", "status", "shots")
            .containsExactly(battleId, FIRST, FleetStatus.DAMAGED, GridLocation.toSet("J9"))


        assertThat(context.fleet(SECOND))
            .extracting("battleId", "player", "status", "shots")
            .containsExactly(battleId, SECOND, FleetStatus.OPERATIONAL, GridLocation.toSet("A0"))

        assertThat(context.ship(FIRST, CARRIER))
            .extracting("battleId", "player", "type", "status", "hits")
            .containsExactly(battleId, FIRST, CARRIER, ShipStatus.DAMAGED, GridLocation.toSet("A0"))

        context.shipsHaveStatus(FIRST, ShipStatus.UNHARMED, BATTLESHIP, CRUISER, SUBMARINE, DESTROYER)
        context.shipsHaveStatus(FIRST, ShipStatus.DAMAGED, CARRIER)
        context.shipsHaveStatus(SECOND, ShipStatus.UNHARMED, CARRIER, BATTLESHIP, CRUISER, SUBMARINE, DESTROYER)
    }

    @Test
    fun `fight just before last shot`() {
        context.scenario(
            """
            create player John
            create player Jane
            start battle with John and Jane

            John deploy Carrier at A0 on row
            John deploy Battleship at A1 on row
            John deploy Cruiser at A2 on row
            John deploy Submarine at A3 on row
            John deploy Destroyer at A4 on row

            Jane deploy Carrier at A0 on row
            Jane deploy Battleship at A1 on row
            Jane deploy Cruiser at A2 on row
            Jane deploy Submarine at A3 on row
            Jane deploy Destroyer at A4 on row
            
            John shot at J9
            Jane shot at A0
            John shot at A6
            Jane shot at B0
            John shot at A7
            Jane shot at C0
            John shot at A8
            Jane shot at D0
            John shot at A9
            Jane shot at E0
            
            John shot at F0
            Jane shot at A1
            John shot at F1
            Jane shot at B1
            John shot at F2
            Jane shot at C1
            John shot at F3
            Jane shot at D1
            
            John shot at F4
            Jane shot at A2
            John shot at F5
            Jane shot at B2
            John shot at F6
            Jane shot at C2
            
            John shot at F7
            Jane shot at A3
            John shot at F8
            Jane shot at B3
            John shot at F9
            Jane shot at C3
            
            John shot at G0
            Jane shot at A4
            John shot at G1
        """.trimIndent()
        )

        val battleId = context.firstBattle().id


        assertThat(context.firstBattle())
            .extracting("player1Id", "player2Id", "nextPlayer", "status", "turn")
            .containsExactly(
                context.player("John").id,
                context.player("Jane").id,
                SECOND,
                BattleStatus.FIGHTING,
                16
            )

        assertThat(context.fleet(FIRST))
            .extracting("battleId", "player", "status")
            .containsExactly(battleId, FIRST, FleetStatus.DAMAGED)


        assertThat(context.fleet(SECOND))
            .extracting("battleId", "player", "status")
            .containsExactly(battleId, SECOND, FleetStatus.OPERATIONAL)

        assertThat(context.ship(FIRST, DESTROYER))
            .extracting("battleId", "player", "type", "status", "hits")
            .containsExactly(battleId, FIRST, DESTROYER, ShipStatus.DAMAGED, GridLocation.toSet("A4"))

        context.shipsHaveStatus(FIRST, ShipStatus.SUNK, CARRIER, BATTLESHIP, CRUISER, SUBMARINE)
        context.shipsHaveStatus(FIRST, ShipStatus.DAMAGED, DESTROYER)
        context.shipsHaveStatus(SECOND, ShipStatus.UNHARMED, CARRIER, BATTLESHIP, CRUISER, SUBMARINE, DESTROYER)
    }

    @Test
    fun `last shot`() {
        context.scenario(
            """
            create player John
            create player Jane
            start battle with John and Jane

            John deploy Carrier at A0 on row
            John deploy Battleship at A1 on row
            John deploy Cruiser at A2 on row
            John deploy Submarine at A3 on row
            John deploy Destroyer at A4 on row

            Jane deploy Carrier at A0 on row
            Jane deploy Battleship at A1 on row
            Jane deploy Cruiser at A2 on row
            Jane deploy Submarine at A3 on row
            Jane deploy Destroyer at A4 on row
            
            John shot at J9
            Jane shot at A0
            John shot at A6
            Jane shot at B0
            John shot at A7
            Jane shot at C0
            John shot at A8
            Jane shot at D0
            John shot at A9
            Jane shot at E0
            
            John shot at F0
            Jane shot at A1
            John shot at F1
            Jane shot at B1
            John shot at F2
            Jane shot at C1
            John shot at F3
            Jane shot at D1
            
            John shot at F4
            Jane shot at A2
            John shot at F5
            Jane shot at B2
            John shot at F6
            Jane shot at C2
            
            John shot at F7
            Jane shot at A3
            John shot at F8
            Jane shot at B3
            John shot at F9
            Jane shot at C3
            
            John shot at G0
            Jane shot at A4
            John shot at G1
            Jane shot at B4
        """.trimIndent()
        )

        val battleId = context.firstBattle().id


        assertThat(context.firstBattle())
            .extracting("player1Id", "player2Id", "nextPlayer", "status", "turn")
            .containsExactly(
                context.player("John").id,
                context.player("Jane").id,
                SECOND,
                BattleStatus.FINISHED,
                16
            )

        assertThat(context.fleet(FIRST))
            .extracting("battleId", "player", "status")
            .containsExactly(battleId, FIRST, FleetStatus.DESTROYED)

        assertThat(context.fleet(SECOND))
            .extracting("battleId", "player", "status")
            .containsExactly(battleId, SECOND, FleetStatus.OPERATIONAL)

        context.shipsHaveStatus(FIRST, ShipStatus.SUNK, CARRIER, BATTLESHIP, CRUISER, SUBMARINE, DESTROYER)
        context.shipsHaveStatus(SECOND, ShipStatus.UNHARMED, CARRIER, BATTLESHIP, CRUISER, SUBMARINE, DESTROYER)
    }
}