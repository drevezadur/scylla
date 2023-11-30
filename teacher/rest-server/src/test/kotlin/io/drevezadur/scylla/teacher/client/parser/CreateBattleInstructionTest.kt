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

package io.drevezadur.scylla.teacher.client.parser

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CreateBattleInstructionTest {

    @MockK
    lateinit var interpreter: InstructionInterpreter

    private lateinit var instruction: Instruction

    @BeforeEach
    fun setUp() {
        instruction = CreateBattleInstruction(JOHN, JANE)
    }

    @Test
    fun `Shall call createBattle of interpreter`() {
        every { interpreter.createBattle(eq(JOHN), eq(JANE)) } answers { nothing }

        instruction.execute(interpreter)

        verify(exactly = 1) { interpreter.createBattle(eq(JOHN), eq(JANE)) }
    }

    companion object {
        const val JOHN = "John"
        const val JANE = "Jane"
    }
}