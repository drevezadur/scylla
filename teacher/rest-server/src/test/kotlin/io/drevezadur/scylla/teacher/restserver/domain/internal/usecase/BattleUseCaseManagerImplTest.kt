package io.drevezadur.scylla.teacher.restserver.domain.internal.usecase

import io.drevezadur.scylla.teacher.restserver.domain.BattleNotFoundException
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.BattleDEntity
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetCreation
import io.drevezadur.scylla.teacher.restserver.domain.model.FleetDEntity
import io.drevezadur.scylla.teacher.restserver.domain.store.BattleStore
import io.drevezadur.scylla.teacher.restserver.domain.store.FleetStore
import io.drevezadur.scylla.teacher.restserver.lang.BattleStatus
import io.drevezadur.scylla.teacher.restserver.lang.FleetStatus
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
class BattleUseCaseManagerImplTest {

    @MockK
    lateinit var battleStore: BattleStore

    @MockK
    lateinit var fleetStore: FleetStore

    private lateinit var useCaseManager: BattleUseCaseManagerImpl

    @BeforeEach
    fun setUp() {
        useCaseManager = BattleUseCaseManagerImpl(battleStore, fleetStore)
    }

    @Test
    fun `createBattle shall create the battle and a fleet for each player`() {
        val battleCreation = BattleCreation(PLAYER_1_ID, PLAYER_2_ID)
        val expectedBattle = BattleDEntity(BATTLE_ID, PLAYER_1_ID, PLAYER_2_ID, BattleStatus.DEPLOYMENT)

        every { battleStore.create(battleCreation) }
            .answers { expectedBattle }
        every { fleetStore.create(FleetCreation(BATTLE_ID, PLAYER_1_ID)) }
            .answers { FleetDEntity(BATTLE_ID, PLAYER_1_ID, FleetStatus.UNHARMED) }
        every { fleetStore.create(FleetCreation(BATTLE_ID, PLAYER_2_ID)) }
            .answers { FleetDEntity(BATTLE_ID, PLAYER_2_ID, FleetStatus.UNHARMED) }


        assertThat(useCaseManager.createBattle(battleCreation))
            .isSameAs(expectedBattle)


        verify(exactly = 1) { battleStore.create(battleCreation) }
        verify(exactly = 1) { fleetStore.create(FleetCreation(BATTLE_ID, PLAYER_1_ID)) }
        verify(exactly = 1) { fleetStore.create(FleetCreation(BATTLE_ID, PLAYER_2_ID)) }
    }

    @Test
    fun `findById shall call BattleStore`() {
        val expectedBattle = BattleDEntity(BATTLE_ID, PLAYER_1_ID, PLAYER_2_ID, BattleStatus.DEPLOYMENT)
        every { battleStore.findById(BATTLE_ID) }
            .answers { expectedBattle }

        assertThat(useCaseManager.findById(BATTLE_ID))
            .isSameAs(expectedBattle)

        verify(exactly = 1) { battleStore.findById(BATTLE_ID) }
    }

    @Test
    fun `getById shall throw an exception when battle not exists`() {
        every { battleStore.findById(BATTLE_ID) }
            .returns(null)

        assertThatThrownBy {
            useCaseManager.getById(BATTLE_ID)
        }.isInstanceOf(BattleNotFoundException::class.java)
            .extracting("battleId")
            .isEqualTo(BATTLE_ID)

        verify(exactly = 1) { battleStore.findById(BATTLE_ID) }
    }

    @Test
    fun `getById shall return the stored battle`() {
        val expectedBattle = BattleDEntity(BATTLE_ID, PLAYER_1_ID, PLAYER_2_ID, BattleStatus.DEPLOYMENT)
        every { battleStore.findById(BATTLE_ID) }
            .answers { expectedBattle }

        assertThat(useCaseManager.getById(BATTLE_ID))
            .isSameAs(expectedBattle)

        verify(exactly = 1) { battleStore.findById(BATTLE_ID) }
    }

    @Test
    fun `delete shall delete in store`() {
        every { battleStore.delete(BATTLE_ID) }
            .answers { Any() }

        useCaseManager.delete(BATTLE_ID)

        verify(exactly = 1) { battleStore.delete(BATTLE_ID) }
    }

    companion object {
        val BATTLE_ID: UUID = UUID.randomUUID()!!
        val PLAYER_1_ID: UUID = UUID.randomUUID()!!
        val PLAYER_2_ID: UUID = UUID.randomUUID()!!
    }
}