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

package io.drevezadur.scylla.teacher.restserver.persist

import io.drevezadur.scylla.teacher.restserver.lang.BattleStatus
import io.drevezadur.scylla.teacher.restserver.persist.model.BattlePEntity
import io.drevezadur.scylla.teacher.restserver.persist.model.PlayerPEntity
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*


@ExtendWith(SpringExtension::class)
@DataJpaTest
class BattleRepositoryTest {

    @Autowired
    lateinit var battleRepository: BattleRepository

    @Autowired
    lateinit var playerRepository: PlayerRepository

    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    fun `save() shall fail when first player does not exist`() {
        playerRepository.save(PlayerPEntity(player2Id, "John"))

        assertThatThrownBy {
            battleRepository.save(
                BattlePEntity(
                    battleId,
                    unknownId,
                    player2Id,
                    BattleStatus.DEPLOYMENT,
                    unknownId,
                    null
                )
            )
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }

    @Test
    fun `save() shall fail when second player does not exist`() {
        playerRepository.save(PlayerPEntity(player1Id, "Jane"))

        assertThatThrownBy {
            battleRepository.save(
                BattlePEntity(
                    battleId,
                    player1Id,
                    unknownId,
                    BattleStatus.DEPLOYMENT,
                    player1Id,
                    null
                )
            )
            entityManager.flush()
        }.isInstanceOf(ConstraintViolationException::class.java)
    }

    @Test
    fun `save() shall success when first and second player exist`() {
        playerRepository.save(PlayerPEntity(player1Id, "Jane"))
        playerRepository.save(PlayerPEntity(player2Id, "John"))

        battleRepository.save(
            BattlePEntity(
                battleId,
                player1Id,
                player2Id,
                BattleStatus.DEPLOYMENT,
                player1Id,
                null
            )
        )
    }

    companion object {
        val battleId: UUID = UUID.randomUUID()
        val player1Id: UUID = UUID.randomUUID()
        val player2Id: UUID = UUID.randomUUID()
        val unknownId: UUID = UUID.randomUUID()
    }
}