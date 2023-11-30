package io.drevezadur.scylla.teacher.restserver.domain.internal.usecase

import io.drevezadur.scylla.teacher.restserver.domain.internal.ShotResolver
import io.drevezadur.scylla.teacher.restserver.domain.internal.ShotResolverFactory
import io.drevezadur.scylla.teacher.restserver.domain.model.ShotReport
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.ShotResult
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ShotUseCaseManagerImplTest {

    @MockK
    lateinit var shotResolverFactory: ShotResolverFactory

    private lateinit var shotUseCaseManager: ShotUseCaseManagerImpl

    @BeforeEach
    fun setUp() {
        shotUseCaseManager = ShotUseCaseManagerImpl(shotResolverFactory)
    }

    @Test
    fun `shotAt() shall call ShotResolver`() {
        val shotResolver = mockk<ShotResolver>()
        every { shotResolverFactory.createInstance() }
            .answers { shotResolver }

        val shotReport = ShotReport(ShotResult.HIT, false)
        every {
            shotResolver.resolve(
                ShipDeploymentUseCaseBeanTest.battleId,
                ShipDeploymentUseCaseBeanTest.player1Id,
                Location(0, 0)
            )
        }
            .answers { shotReport }

        val actualShotReport = shotUseCaseManager.shotAt(
            ShipDeploymentUseCaseBeanTest.battleId,
            ShipDeploymentUseCaseBeanTest.player1Id, Location(0, 0)
        )

        Assertions.assertThat(actualShotReport)
            .isSameAs(shotReport)

        verify(exactly = 1) { shotResolverFactory.createInstance() }
        verify(exactly = 1) {
            shotResolver.resolve(
                ShipDeploymentUseCaseBeanTest.battleId,
                ShipDeploymentUseCaseBeanTest.player1Id,
                Location(0, 0)
            )
        }
    }
}