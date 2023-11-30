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

import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ScenarioImplTest {

    @MockK
    private lateinit var interpreter: InstructionInterpreter


    @BeforeEach
    fun setUp() {
    }


    @Test
    fun `Shall not call interpreter when no instructions`() {
        val scenario = ScenarioImpl(emptyList())

        scenario.execute(interpreter)

        verify { interpreter wasNot called }
    }


    @Test
    fun `Shall call interpreted for each instruction`() {
        val instruction1 = mockk<Instruction>()
        val instruction2 = mockk<Instruction>()
        val scenario = ScenarioImpl(listOf(instruction1, instruction2))

        every { instruction1.execute(eq(interpreter)) } answers { nothing }
        every { instruction2.execute(eq(interpreter)) } answers { nothing }

        scenario.execute(interpreter)

        verify(exactly = 1) { instruction1.execute(eq(interpreter)) }
        verify(exactly = 1) { instruction2.execute(eq(interpreter)) }
    }

}