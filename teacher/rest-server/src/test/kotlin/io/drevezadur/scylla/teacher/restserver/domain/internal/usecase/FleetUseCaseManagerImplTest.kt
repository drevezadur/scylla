package io.drevezadur.scylla.teacher.restserver.domain.internal.usecase

import io.drevezadur.scylla.teacher.restserver.domain.FleetNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.FleetStore
import io.drevezadur.scylla.teacher.restserver.domain.usecase.FleetUseCaseManager
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
class FleetUseCaseManagerImplTest {

    @MockK
    lateinit var fleetStore: FleetStore

    private lateinit var useCaseManager: FleetUseCaseManager

    @BeforeEach
    fun setUp() {
        useCaseManager = FleetUseCaseManagerImpl(fleetStore)
    }

    @Test
    fun `findFleet shall call store`() {
        val expectedFleet = FleetDEntity(BATTLE_ID, PLAYER_ID)
        every { fleetStore.findFleetByBattleAndPlayer(BATTLE_ID, PLAYER_ID) }
            .returns(expectedFleet)

        assertThat(useCaseManager.findFleet(BATTLE_ID, PLAYER_ID))
            .isSameAs(expectedFleet)

        verify(exactly = 1) { fleetStore.findFleetByBattleAndPlayer(BATTLE_ID, PLAYER_ID) }
    }

    @Test
    fun `getFleet shall failed when fleet not exist`() {
        every { fleetStore.findFleetByBattleAndPlayer(BATTLE_ID, PLAYER_ID) }
            .returns(null)

        assertThatThrownBy {
            useCaseManager.getFleet(BATTLE_ID, PLAYER_ID)
        }.isInstanceOf(FleetNotFoundException::class.java)
            .extracting("battleId", "playerId")
            .containsExactly(BATTLE_ID, PLAYER_ID)

        verify(exactly = 1) { fleetStore.findFleetByBattleAndPlayer(BATTLE_ID, PLAYER_ID) }
    }

    @Test
    fun `getFleet shall return the fleet from store`() {
        val expectedFleet = FleetDEntity(BATTLE_ID, PLAYER_ID)
        every { fleetStore.findFleetByBattleAndPlayer(BATTLE_ID, PLAYER_ID) }
            .returns(expectedFleet)

        assertThat(useCaseManager.getFleet(BATTLE_ID, PLAYER_ID))
            .isSameAs(expectedFleet)

        verify(exactly = 1) { fleetStore.findFleetByBattleAndPlayer(BATTLE_ID, PLAYER_ID) }
    }

    companion object {
        private val BATTLE_ID: UUID = UUID.randomUUID()!!
        private val PLAYER_ID: UUID = UUID.randomUUID()!!
    }
}