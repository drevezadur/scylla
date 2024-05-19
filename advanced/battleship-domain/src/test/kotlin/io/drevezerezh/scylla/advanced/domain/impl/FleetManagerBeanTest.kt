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

package io.drevezerezh.scylla.advanced.domain.impl

import io.drevezerezh.scylla.advanced.domain.api.fleet.Fleet
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetAlreadyDeployedException
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetNotFoundException
import io.drevezerezh.scylla.advanced.domain.spi.FleetStore
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.FleetStatus
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
@MockKExtension.ConfirmVerification
class FleetManagerBeanTest {

    @MockK
    lateinit var fleetStore: FleetStore

    private lateinit var fleetManager: FleetManager

    @BeforeEach
    fun setUp() {
        fleetManager = FleetManagerBean(fleetStore)
    }


    @Test
    fun `getFleet shall call store`() {
        every { fleetStore.getById(FLEET.id) } returns FLEET

        assertThat(fleetManager.getFleet(FLEET.id))
            .isEqualTo(FLEET)

        verify { fleetStore.getById(FLEET.id) }
    }


    @Test
    fun `createFleet shall fail when fleet already exists`() {
        val id = FleetId("battle-01", BattlePlayer.SECOND)

        every { fleetStore.contains(id) } returns true

        assertThatThrownBy { fleetManager.createFleet(id) }
            .isInstanceOf(FleetAlreadyDeployedException::class.java)

        verify { fleetStore.contains(id) }
    }


    @Test
    fun `createFleet shall create fleet with initial state and store it`() {
        val expectedFleet = Fleet("battle-01", BattlePlayer.SECOND)

        every { fleetStore.contains(expectedFleet.id) } returns false
        every { fleetStore.save(expectedFleet) } answers { Any() }

        assertThat(fleetManager.createFleet(expectedFleet.id))
            .isEqualTo(expectedFleet)

        verify { fleetStore.contains(expectedFleet.id) }
        verify { fleetStore.save(expectedFleet) }
    }


    @Test
    fun `updateFleet shall fail when fleet not exists`() {
        every { fleetStore.contains(FLEET.id) } returns false

        assertThatThrownBy {
            fleetManager.updateFleet(FLEET)
        }.isInstanceOf(FleetNotFoundException::class.java)

        verify { fleetStore.contains(FLEET.id) }
    }


    @Test
    fun `updateFleet shall store existing fleet`() {
        every { fleetStore.contains(FLEET.id) } returns true
        every { fleetStore.save(FLEET) } answers { Any() }

        fleetManager.updateFleet(FLEET)

        verify { fleetStore.contains(FLEET.id) }
        verify { fleetStore.save(FLEET) }
    }


    @Test
    fun `deleteFleet shall call the store`() {
        every { fleetStore.deleteById(FLEET.id) } returns true

        assertThat(fleetManager.deleteFleet(FLEET.id))
            .isTrue()

        verify { fleetStore.deleteById(FLEET.id) }
    }


    companion object {
        val FLEET = Fleet("battle-01", BattlePlayer.FIRST, FleetStatus.OPERATIONAL, GridLocation.toSet("A1B2C3D4"))
    }
}