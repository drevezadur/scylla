package io.gofannon.scylla.homework.domain.internal

import io.gofannon.scylla.homework.domain.Ship
import io.gofannon.scylla.homework.lang.*
import io.gofannon.scylla.homework.lang.ShipType.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource

@ExtendWith(MockKExtension::class)
class PlayerBoardImplTest {

    @MockK
    private lateinit var gameManager: GameManager

    private lateinit var board: PlayerBoardImpl

    private var shipFactory: ShipFactory = DefaultShipFactory()

    @BeforeEach
    fun setUp() {
        shipFactory = DefaultShipFactory()
        board = PlayerBoardImpl(Player.PLAYER_A, shipFactory, gameManager)
    }

    @Test
    fun `getPlayerSituation() shall be DEPLOYING_SHIPS when no ships are deployed`() {
        assertThat(board.getPlayerState())
            .isSameAs(PlayerState.DEPLOYING_FLEET)
    }

    @Test
    fun `getPlayerSituation() shall be DEPLOYING_SHIPS when not all ships are deployed`() {
        board.deployShip(CARRIER, Location(0, 0), GridOrientation.ROW)
        board.deployShip(BATTLESHIP, Location(0, 1), GridOrientation.ROW)
        board.deployShip(CRUISER, Location(0, 2), GridOrientation.ROW)

        assertThat(board.getPlayerState())
            .isSameAs(PlayerState.DEPLOYING_FLEET)
    }

    @Test
    fun `getPlayerSituation() shall be DEPLOYED when all ships are deployed`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()

        assertThat(board.getPlayerState())
            .isSameAs(PlayerState.FLEET_DEPLOYED)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    private fun makeDefaultDeployment() {
        board.deployShip(CARRIER, Location(0, 0), GridOrientation.ROW)
        board.deployShip(BATTLESHIP, Location(0, 1), GridOrientation.ROW)
        board.deployShip(CRUISER, Location(0, 2), GridOrientation.ROW)
        board.deployShip(SUBMARINE, Location(0, 3), GridOrientation.ROW)
        board.deployShip(DESTROYER, Location(0, 4), GridOrientation.ROW)
    }

    @Test
    fun `getPlayerSituation() shall be WINNER when it has been set as the winner`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit

        makeDefaultDeployment()
        board.setPlayerState(PlayerState.WINNER)

        assertThat(board.getPlayerState())
            .isSameAs(PlayerState.WINNER)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `getPlayerSituation() shall be LOSER when all ships are sunk`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()
        board.setPlayerState(PlayerState.LOSER)

        assertThat(board.getPlayerState())
            .isSameAs(PlayerState.LOSER)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    private fun sunkAllTypes() {
        sunk(CARRIER)
        sunk(BATTLESHIP)
        sunk(CRUISER)
        sunk(SUBMARINE)
        sunk(DESTROYER)
    }

    private fun sunk(type: ShipType) {
        val y = getAbscisse(type)

        for (x in 0..<type.size) {
            board.resolveShot(Location(x, y))
        }
    }

    private fun getAbscisse(type: ShipType): Int {
        return when (type) {
            CARRIER -> 0
            BATTLESHIP -> 1
            CRUISER -> 2
            SUBMARINE -> 3
            DESTROYER -> 4
        }
    }

    @Test
    fun `deployShip() shall not accept twice same ship type`() {
        board.deployShip(CARRIER, Location(0, 0), GridOrientation.ROW)

        assertThatThrownBy {
            board.deployShip(CARRIER, Location(0, 1), GridOrientation.ROW)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `deployShip shall not accept to deploy two ships at same location`() {
        board.deployShip(CARRIER, Location(0, 0), GridOrientation.ROW)

        assertThatThrownBy {
            board.deployShip(BATTLESHIP, Location(3, 0), GridOrientation.ROW)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `deployShip shall accept to deploy ships at distinct location`() {
        board.deployShip(CARRIER, Location(0, 0), GridOrientation.ROW)
        board.deployShip(BATTLESHIP, Location(0, 1), GridOrientation.COLUMN)

        assertThat(board.getShips())
            .hasSize(2)

        assertThat(board.getShip(CARRIER))
            .extracting("type", "origin", "orientation")
            .containsExactly(CARRIER, Location(0, 0), GridOrientation.ROW)

        assertThat(board.getShip(BATTLESHIP))
            .extracting("type", "origin", "orientation")
            .containsExactly(BATTLESHIP, Location(0, 1), GridOrientation.COLUMN)
    }

    @Test
    fun `getNotDeployedShipTypes shall return empty set when all ships are deployed`() {
        board.deployShip(CARRIER, Location(0, 0), GridOrientation.ROW)
        board.deployShip(BATTLESHIP, Location(0, 1), GridOrientation.COLUMN)

        assertThat(board.getNotDeployedShipTypes())
            .containsOnly(CRUISER, SUBMARINE, DESTROYER)
    }

    @Test
    fun `getNotDeployedShipTypes shall return only not deployed ships`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit

        makeDefaultDeployment()

        assertThat(board.getNotDeployedShipTypes())
            .isEmpty()

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @ParameterizedTest
    @EnumSource(value = ShipType::class)
    fun `getShips shall return existing ships`(shipType: ShipType) {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit

        makeDefaultDeployment()

        assertThat(board.getShips().any { it.type == shipType })
            .isTrue()

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `getShips shall not return missing ships`() {
        board.deployShip(CARRIER, Location(0, 0), GridOrientation.ROW)
        board.deployShip(BATTLESHIP, Location(0, 1), GridOrientation.COLUMN)

        assertThat(board.getShips().map { it.type })
            .containsOnly(CARRIER, BATTLESHIP)
    }

    @Test
    fun `getShip() shall not accept missing ship`() {
        board.deployShip(CARRIER, Location(0, 0), GridOrientation.ROW)
        board.deployShip(BATTLESHIP, Location(0, 1), GridOrientation.COLUMN)

        assertThatThrownBy {
            board.getShip(SUBMARINE)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @EnumSource(value = ShipType::class)
    fun `getShip() shall accept existing ship`(shipType: ShipType) {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit

        makeDefaultDeployment()

        assertThat(board.getShip(shipType).type)
            .isSameAs(shipType)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `fireAt shall not accept when NOT_DEPLOYED`() {
        board.deployShip(CARRIER, Location(0, 0), GridOrientation.ROW)
        board.deployShip(BATTLESHIP, Location(0, 1), GridOrientation.ROW)

        assertThatThrownBy {
            board.fireAt(Location(1, 1))
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `fireAt shall not accept when WINNER`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit

        makeDefaultDeployment()
        board.setPlayerState(PlayerState.WINNER)

        assertThatThrownBy {
            board.fireAt(Location(1, 1))
        }.isInstanceOf(IllegalStateException::class.java)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `fireAt shall not accept when LOSER`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit

        makeDefaultDeployment()
        board.setPlayerState(PlayerState.LOSER)

        assertThatThrownBy {
            board.fireAt(Location(1, 1))
        }.isInstanceOf(IllegalStateException::class.java)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `fireAt shall call gameResolver`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        every { gameManager.shot(Player.PLAYER_A, Location(1, 1)) }.returns(ShotResult.HIT)

        makeDefaultDeployment()
        board.startFighting()

        board.fireAt(Location(1, 1))

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
        verify(exactly = 1) { gameManager.shot(Player.PLAYER_A, Location(1, 1)) }
    }

    @ParameterizedTest
    @EnumSource(PlayerState::class)
    fun setAsPlayerState(state: PlayerState) {
        board.setPlayerState(state)

        assertThat(board.getPlayerState())
            .isSameAs(state)
    }

    @ParameterizedTest
    @CsvSource("9,9,MISSED", "0,0,HIT")
    fun `resolveShot shall handle MISSED and HIT`(x: Int, y: Int, shotResult: ShotResult) {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()

        assertThat(board.resolveShot(Location(x, y)))
            .isSameAs(shotResult)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `resolveShot shall handle ALREADY_SHOT`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()
        val location = Location(0, 0)
        board.resolveShot(location)

        assertThat(board.resolveShot(location))
            .isSameAs(ShotResult.ALREADY_SHOT)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `resolveShot shall handle SUNK`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()
        board.resolveShot(Location(0, 4))

        assertThat(board.resolveShot(Location(1, 4)))
            .isSameAs(ShotResult.SUNK)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `getFleetStatus shall return NOT_DEPLOYED when no ships are deployed`() {
        assertThat(board.getFleetStatus())
            .isSameAs(FleetStatus.NOT_DEPLOYED)
    }

    @Test
    fun `getFleetStatus shall return NOT_DEPLOYED when not all ships are deployed`() {
        board.deployShip(CARRIER, Location(0, 0), GridOrientation.ROW)
        board.deployShip(BATTLESHIP, Location(0, 1), GridOrientation.ROW)
        board.deployShip(CRUISER, Location(0, 2), GridOrientation.ROW)

        assertThat(board.getFleetStatus())
            .isSameAs(FleetStatus.NOT_DEPLOYED)
    }

    @Test
    fun `getFleetStatus shall return UNHARMED when all ships are deployed and all are UNHARMED`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()

        assertThat(board.getFleetStatus())
            .isSameAs(FleetStatus.UNHARMED)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `getFleetStatus shall return DAMAGED when some ships are UNHARMED and DAMAGED and SUNK`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()
        damage(DESTROYER)
        sunk(BATTLESHIP)
        sunk(SUBMARINE)

        assertThat(board.getFleetStatus())
            .isSameAs(FleetStatus.DAMAGED)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }


    private fun damage(type: ShipType) {
        val y = getAbscisse(type)
        board.resolveShot(Location(0, y))
    }

    @Test
    fun `getFleetStatus shall return DAMAGED when some ships are UNHARMED and DAMAGED`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()
        damage(DESTROYER)
        damage(SUBMARINE)

        assertThat(board.getFleetStatus())
            .isSameAs(FleetStatus.DAMAGED)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `getFleetStatus shall return DAMAGED when some ships are UNHARMED and SUNK`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()
        sunk(DESTROYER)
        sunk(SUBMARINE)

        assertThat(board.getFleetStatus())
            .isSameAs(FleetStatus.DAMAGED)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @Test
    fun `getFleetStatus shall return DAMAGED when some ships are DAMAGED and SUNK`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()
        damage(CARRIER)
        damage(BATTLESHIP)
        damage(CRUISER)
        sunk(SUBMARINE)
        sunk(DESTROYER)

        assertThat(board.getFleetStatus())
            .isSameAs(FleetStatus.DAMAGED)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }


    @Test
    fun `getFleetStatus shall return SUNK when all ships are sunk`() {
        every { gameManager.fleetDeployed(Player.PLAYER_A) } returns Unit
        makeDefaultDeployment()
        sunkAllTypes()

        assertThat(board.getFleetStatus())
            .isSameAs(FleetStatus.SUNK)

        verify { gameManager.fleetDeployed(Player.PLAYER_A) }
    }

    @ParameterizedTest
    @EnumSource(Player::class)
    fun getFleetId(player: Player) {
        board = PlayerBoardImpl(player, shipFactory, gameManager)
        assertThat(board.player)
            .isSameAs(player)
    }


    @Test
    fun `computeFleetStatus() shall return NOT_DEPLOYED when all ships are not deployed`() {
        assertThat(
            PlayerBoardImpl.computeFleetStatus(
                listOf(
                    createShip(BATTLESHIP),
                    createShip(CARRIER)
                )
            )
        ).isSameAs(FleetStatus.NOT_DEPLOYED)
    }

    private fun createShip(type: ShipType, status: ShipStructuralStatus = ShipStructuralStatus.UNHARMED): Ship {
        val hits: Set<Location> = when (status) {
            ShipStructuralStatus.UNHARMED -> emptySet()
            ShipStructuralStatus.DAMAGED -> setOf(Location(0, 0))
            ShipStructuralStatus.DESTROYED -> (0..<type.size).map { Location(it, 0) }.toSet()
        }

        return ShipImpl(type, Location(0, 0), GridOrientation.ROW, hits)
    }

    @Test
    fun `computeFleetStatus() shall return SUNK when all ships are sunk`() {
        assertThat(
            PlayerBoardImpl.computeFleetStatus(
                listOf(
                    createShip(CARRIER, ShipStructuralStatus.DESTROYED),
                    createShip(BATTLESHIP, ShipStructuralStatus.DESTROYED),
                    createShip(CRUISER, ShipStructuralStatus.DESTROYED),
                    createShip(SUBMARINE, ShipStructuralStatus.DESTROYED),
                    createShip(DESTROYER, ShipStructuralStatus.DESTROYED)
                )
            )
        ).isSameAs(FleetStatus.SUNK)
    }

    @Test
    fun `computeFleetStatus() shall return UNHARMED when all ships are UNHARMED`() {
        assertThat(
            PlayerBoardImpl.computeFleetStatus(
                listOf(
                    createShip(CARRIER),
                    createShip(BATTLESHIP),
                    createShip(CRUISER),
                    createShip(SUBMARINE),
                    createShip(DESTROYER)
                )
            )
        ).isSameAs(FleetStatus.UNHARMED)
    }

    @Test
    fun `computeFleetStatus() shall return DAMAGED when all ships are DAMAGED`() {
        assertThat(
            PlayerBoardImpl.computeFleetStatus(
                listOf(
                    createShip(CARRIER, ShipStructuralStatus.DAMAGED),
                    createShip(BATTLESHIP, ShipStructuralStatus.DAMAGED),
                    createShip(CRUISER, ShipStructuralStatus.DAMAGED),
                    createShip(SUBMARINE, ShipStructuralStatus.DAMAGED),
                    createShip(DESTROYER, ShipStructuralStatus.DAMAGED)
                )
            )
        ).isSameAs(FleetStatus.DAMAGED)
    }

    @Test
    fun `computeFleetStatus() shall return DAMAGED when ship status are UNHARMED and DAMAGED`() {
        assertThat(
            PlayerBoardImpl.computeFleetStatus(
                listOf(
                    createShip(CARRIER, ShipStructuralStatus.UNHARMED),
                    createShip(BATTLESHIP, ShipStructuralStatus.UNHARMED),
                    createShip(CRUISER, ShipStructuralStatus.DAMAGED),
                    createShip(SUBMARINE, ShipStructuralStatus.DAMAGED),
                    createShip(DESTROYER, ShipStructuralStatus.DAMAGED)
                )
            )
        ).isSameAs(FleetStatus.DAMAGED)
    }

    @Test
    fun `computeFleetStatus() shall return DAMAGED when ship status are UNHARMED and DESTROYED`() {
        assertThat(
            PlayerBoardImpl.computeFleetStatus(
                listOf(
                    createShip(CARRIER, ShipStructuralStatus.UNHARMED),
                    createShip(BATTLESHIP, ShipStructuralStatus.UNHARMED),
                    createShip(CRUISER, ShipStructuralStatus.DESTROYED),
                    createShip(SUBMARINE, ShipStructuralStatus.DESTROYED),
                    createShip(DESTROYER, ShipStructuralStatus.DESTROYED)
                )
            )
        ).isSameAs(FleetStatus.DAMAGED)
    }

    @Test
    fun `computeFleetStatus() shall return DAMAGED when ship status are DAMAGED and DESTROYED`() {
        assertThat(
            PlayerBoardImpl.computeFleetStatus(
                listOf(
                    createShip(CARRIER, ShipStructuralStatus.DAMAGED),
                    createShip(BATTLESHIP, ShipStructuralStatus.DAMAGED),
                    createShip(CRUISER, ShipStructuralStatus.DESTROYED),
                    createShip(SUBMARINE, ShipStructuralStatus.DESTROYED),
                    createShip(DESTROYER, ShipStructuralStatus.DESTROYED)
                )
            )
        ).isSameAs(FleetStatus.DAMAGED)
    }

    @Test
    fun `computeFleetStatus() shall return DAMAGED when ship status are UNHARMED and DAMAGED and DESTROYED`() {
        assertThat(
            PlayerBoardImpl.computeFleetStatus(
                listOf(
                    createShip(CARRIER, ShipStructuralStatus.UNHARMED),
                    createShip(BATTLESHIP, ShipStructuralStatus.UNHARMED),
                    createShip(CRUISER, ShipStructuralStatus.UNHARMED),
                    createShip(SUBMARINE, ShipStructuralStatus.DESTROYED),
                    createShip(DESTROYER, ShipStructuralStatus.DESTROYED)
                )
            )
        ).isSameAs(FleetStatus.DAMAGED)
    }
}