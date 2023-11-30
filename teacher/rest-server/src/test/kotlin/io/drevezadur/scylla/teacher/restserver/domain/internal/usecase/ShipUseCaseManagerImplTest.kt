package io.drevezadur.scylla.teacher.restserver.domain.internal.usecase

import io.drevezadur.scylla.teacher.restserver.domain.ShipCollisionException
import io.drevezadur.scylla.teacher.restserver.domain.ShipNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.ShipDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.ShipStore
import io.drevezadur.scylla.teacher.restserver.domain.usecase.ShipDeploymentUseCase
import io.drevezadur.scylla.teacher.restserver.lang.GridOrientation
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ShipUseCaseManagerImplTest {

    @MockK
    lateinit var shipStore: ShipStore

    @MockK
    lateinit var shipDeploymentUseCase: ShipDeploymentUseCase

    private lateinit var useCaseManager: ShipUseCaseManagerImpl

    @BeforeEach
    fun setUp() {
        useCaseManager = ShipUseCaseManagerImpl(shipStore, shipDeploymentUseCase)
    }

    @Test
    fun `deploy shall call store and check deployment`() {
        every { shipStore.getAllShipsInFleet(BATTLE_ID, PLAYER_ID) }
            .returns(emptyList())

        val shipCreation =
            ShipCreation(BATTLE_ID, PLAYER_ID, ShipType.CARRIER, Location(1, 2), orientation = GridOrientation.ROW)
        val expectedShip =
            ShipDEntity(BATTLE_ID, PLAYER_ID, ShipType.CARRIER, Location(1, 2), orientation = GridOrientation.ROW)
        every { shipStore.create(shipCreation) }
            .returns(expectedShip)
        every { shipDeploymentUseCase.shipDeployed(BATTLE_ID, PLAYER_ID, ShipType.CARRIER) }
            .answers { Any() }


        assertThat(useCaseManager.deploy(shipCreation))
            .isSameAs(expectedShip)


        verify(exactly = 1) { shipStore.create(shipCreation) }
        verify(exactly = 1) { shipDeploymentUseCase.shipDeployed(BATTLE_ID, PLAYER_ID, ShipType.CARRIER) }
    }


    @Test
    fun `deploy shall not accept to override another ship`() {
        val currentShipList = listOf(
            ShipDEntity(
                BATTLE_ID,
                PLAYER_ID,
                ShipType.DESTROYER,
                Location(2, 2),
                GridOrientation.COLUMN
            ),
            ShipDEntity(
                BATTLE_ID,
                PLAYER_ID,
                ShipType.CRUISER,
                Location(0, 9),
                GridOrientation.ROW
            )
        )

        val shipCreation =
            ShipCreation(BATTLE_ID, PLAYER_ID, ShipType.CARRIER, Location(1, 2), orientation = GridOrientation.ROW)
        every { shipStore.getAllShipsInFleet(BATTLE_ID, PLAYER_ID) }
            .returns(currentShipList)


        assertThatThrownBy {
            useCaseManager.deploy(shipCreation)
        }.isInstanceOf(ShipCollisionException::class.java)
    }


    @Test
    fun `deploy shall accept to override the same ship`() {
        val currentShipList = listOf(
            ShipDEntity(
                BATTLE_ID,
                PLAYER_ID,
                ShipType.DESTROYER,
                Location(2, 2),
                GridOrientation.COLUMN
            ),
            ShipDEntity(
                BATTLE_ID,
                PLAYER_ID,
                ShipType.CRUISER,
                Location(0, 9),
                GridOrientation.ROW
            )
        )

        val shipCreation =
            ShipCreation(BATTLE_ID, PLAYER_ID, ShipType.DESTROYER, Location(1, 2), orientation = GridOrientation.ROW)
        val shipCreationEntity =
            ShipDEntity(BATTLE_ID, PLAYER_ID, ShipType.DESTROYER, Location(1, 2), orientation = GridOrientation.ROW)

        every { shipStore.getAllShipsInFleet(BATTLE_ID, PLAYER_ID) }
            .returns(currentShipList)
        every { shipStore.create(shipCreation) }
            .answers { shipCreationEntity }
        every { shipDeploymentUseCase.shipDeployed(BATTLE_ID, PLAYER_ID, ShipType.DESTROYER) }
            .answers { Any() }

        assertThat(useCaseManager.deploy(shipCreation))
            .extracting("type", "origin", "orientation")
            .contains(ShipType.DESTROYER, Location(1, 2), GridOrientation.ROW)

        verify(exactly = 1) { shipStore.getAllShipsInFleet(BATTLE_ID, PLAYER_ID) }
        verify(exactly = 1) { shipStore.create(shipCreation) }
    }


    @Test
    fun `getAllShipsInFleet shall call the store`() {
        val shipList: List<ShipDEntity> = listOf()
        every { shipStore.getAllShipsInFleet(BATTLE_ID, PLAYER_ID) }
            .returns(shipList)


        assertThat(useCaseManager.getAllShipsInFleet(BATTLE_ID, PLAYER_ID))
            .isSameAs(shipList)


        verify(exactly = 1) { shipStore.getAllShipsInFleet(BATTLE_ID, PLAYER_ID) }
    }

    @Test
    fun `getShip shall failed when no ship in the store`() {
        every { shipStore.findById(BATTLE_ID, PLAYER_ID, ShipType.CARRIER) }
            .returns(null)

        assertThatThrownBy { useCaseManager.getShip(BATTLE_ID, PLAYER_ID, ShipType.CARRIER) }
            .isInstanceOf(ShipNotFoundException::class.java)
            .extracting("battleId", "playerId", "type")
            .containsExactly(BATTLE_ID, PLAYER_ID, ShipType.CARRIER)

        verify(exactly = 1) { shipStore.findById(BATTLE_ID, PLAYER_ID, ShipType.CARRIER) }
    }


    @Test
    fun `getShip shall return the ship when it exists in the store`() {
        val expectedShip = ShipDEntity(BATTLE_ID, PLAYER_ID, ShipType.CARRIER, Location(3, 2), GridOrientation.COLUMN)
        every { shipStore.findById(BATTLE_ID, PLAYER_ID, ShipType.CARRIER) }
            .returns(expectedShip)

        assertThat(useCaseManager.getShip(BATTLE_ID, PLAYER_ID, ShipType.CARRIER))
            .isSameAs(expectedShip)

        verify(exactly = 1) { shipStore.findById(BATTLE_ID, PLAYER_ID, ShipType.CARRIER) }
    }

    companion object {
        private val BATTLE_ID: UUID = UUID.randomUUID()!!
        private val PLAYER_ID: UUID = UUID.randomUUID()!!
    }
}