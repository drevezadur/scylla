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

package io.drevezerezh.scylla.advanced.test.integration

import io.drevezerezh.scylla.advanced.domain.api.battle.Battle
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.player.Player
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipId
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.FIRST
import io.drevezerezh.scylla.advanced.lang.BattlePlayer.SECOND
import io.drevezerezh.scylla.advanced.lang.BattleStatus
import io.drevezerezh.scylla.advanced.lang.FleetStatus
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.lang.GridOrientation.ROW
import io.drevezerezh.scylla.advanced.lang.ShipStatus
import io.drevezerezh.scylla.advanced.lang.ShipType.BATTLESHIP
import io.drevezerezh.scylla.advanced.lang.ShipType.CARRIER
import io.drevezerezh.scylla.advanced.webserver.WebServerApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.context.WebApplicationContext

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [WebServerApplication::class])
@WebAppConfiguration
class BattleUCIntegTest(
    @Autowired
    private val webApplicationContext: WebApplicationContext,
) {

    private lateinit var context: IntegrationContext

    @BeforeEach
    @Throws(Exception::class)
    fun setup() {
        context = IntegrationContext(this.webApplicationContext)
        context.scenario(
            """
            create player John
            create player Jane
            create player Walter
        """.trimIndent()
        )
    }

    @AfterEach
    fun tearDown() {
        context.clearAll()
    }


    @Test
    fun `Shall be able to create a new battle`() {
        context.scenario(
            """            
            start battle with John and Jane
        """.trimIndent()
        )

        val john = context.playerStore.getByName("John")
        val jane = context.playerStore.getByName("Jane")

        val allBattles = context.battleStore.getAll()
        assertThat(allBattles)
            .hasSize(1)

        assertThat(containsBattle(allBattles, john, jane))
            .isTrue()
    }


    private fun containsBattle(allBattles: List<Battle>, player1: Player, player2: Player): Boolean {
        return allBattles.firstOrNull { it.player1Id == player1.id && it.player2Id == player2.id } != null
    }


    @Test
    fun `Shall be able to create several battles`() {
        context.scenario(
            """            
            start battle with John and Jane
            start battle with John and Walter
            start battle with Jane and Walter
        """.trimIndent()
        )

        val john = context.playerStore.getByName("John")
        val jane = context.playerStore.getByName("Jane")
        val walter = context.playerStore.getByName("Walter")

        val allBattles = context.battleStore.getAll()
        assertThat(allBattles)
            .hasSize(3)

        assertThat(containsBattle(allBattles, john, jane))
            .isTrue()

        assertThat(containsBattle(allBattles, john, walter))
            .isTrue()

        assertThat(containsBattle(allBattles, jane, walter))
            .isTrue()
    }


    @Test
    fun `Shall be able to deploy a ship`() {
        context.scenario(
            """            
            start battle with John and Jane
            John deploy Carrier at A0 on row
        """.trimIndent()
        )

        val battle = context.battleStore.getAll().first()
        val battleId = battle.id
        val shipId = ShipId(battleId, FIRST, CARRIER)
        assertThat(context.shipStore.getById(shipId))
            .extracting("location", "orientation", "hits")
            .contains(A0, ROW, emptySet<GridLocation>())
    }


    @Test
    fun `Shall not be able to deploy twice the same ship type`() {
        context.scenario(
            """            
            start battle with John and Jane
            John deploy Carrier at A0 on row
            John deploy Carrier at A1 on row
        """.trimIndent()
        )

        val battle = context.battleStore.getAll().first()
        val battleId = battle.id
        val shipId = ShipId(battleId, FIRST, CARRIER)
        assertThat(context.shipStore.getById(shipId))
            .extracting("location", "orientation", "hits")
            .contains(A0, ROW, emptySet<GridLocation>())
    }


    @Test
    fun `Shall not be able to deploy two ships at the same location`() {
        context.scenario(
            """            
            start battle with John and Jane
            John deploy Carrier at A0 on row
            John deploy Submarine at B0 on row
        """.trimIndent()
        )

        val battle = context.battleStore.getAll().first()
        val battleId = battle.id
        val shipId = ShipId(battleId, FIRST, CARRIER)
        assertThat(context.shipStore.getById(shipId))
            .extracting("location", "orientation", "hits")
            .contains(A0, ROW, emptySet<GridLocation>())
    }


    @Test
    fun `Shall be able to deploy all ships of a fleet`() {
        context.scenario(
            """            
            start battle with John and Jane
            
            John deploy Carrier at A0 on row
            John deploy Battleship at A1 on row
            John deploy Cruiser at A2 on row
            John deploy Submarine at A3 on row
            John deploy Destroyer at A4 on row
        """.trimIndent()
        )

        assertThat(context.shipStore.getAll())
            .hasSize(5)
            .extracting("player")
            .containsOnly(FIRST)
    }


    @Test
    fun `Shall be able to deploy all ships of all fleets`() {
        context.scenario(
            """            
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

        val battle = context.battleStore.getAll().first()
        val ships = context.shipStore.getAll()
        assertThat(ships)
            .hasSize(10)

        assertThat(ships.filter { it.player == FIRST })
            .hasSize(5)
        assertThat(ships.filter { it.player == SECOND })
            .hasSize(5)

        assertThat(battle.status)
            .isSameAs(BattleStatus.FIGHTING)
    }


    @Test
    fun `Shall be able to fight`() {
        context.scenario(
            """            
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
            
            John shot at A0
            Jane shot at B1
        """.trimIndent()
        )

        val battle = context.battleStore.getAll().first()
        assertThat(battle.status)
            .isSameAs(BattleStatus.FIGHTING)

        val ships = context.shipStore.getAll()
        val johnCarrier = ships.first { it.player == FIRST && it.type == BATTLESHIP }
        assertThat(johnCarrier)
            .extracting("status", "hits")
            .containsOnly(ShipStatus.DAMAGED, GridLocation.toSet("B1"))

        val janeCarrier = ships.first { it.player == SECOND && it.type == CARRIER }
        assertThat(janeCarrier)
            .extracting("status", "hits")
            .containsOnly(ShipStatus.DAMAGED, GridLocation.toSet("A0"))
    }


    @Test
    fun `Shall be able to end the battle`() {
        context.scenario(
            """            
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
            John shot at A0
            Jane shot at B4
        """.trimIndent()
        )

        val battle = context.battleStore.getAll().first()
        assertThat(battle.status)
            .isSameAs(BattleStatus.FINISHED)

        val johnFleet = context.fleetStore.getById(FleetId(battle.id, FIRST))
        assertThat(johnFleet.status)
            .isEqualTo(FleetStatus.DESTROYED)

        val janeFleet = context.fleetStore.getById(FleetId(battle.id, SECOND))
        assertThat(janeFleet.status)
            .isEqualTo(FleetStatus.DAMAGED)

        val ships = context.shipStore.getAll()
        val johnShips  = ships.filter { it.player == FIRST }
        assertThat(johnShips)
            .extracting("status")
            .containsOnly(ShipStatus.SUNK)

        val janeShips =  ships.filter { it.player == SECOND }
        assertThat(janeShips.filter { it.type != CARRIER })
            .extracting("status")
            .containsOnly(ShipStatus.UNHARMED)
        assertThat(janeShips.filter { it.type == CARRIER })
            .extracting("status")
            .containsOnly(ShipStatus.DAMAGED)
    }

    companion object {
        val A0: GridLocation = GridLocation.of("A0")
    }
}