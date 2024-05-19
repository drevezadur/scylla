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

import io.drevezerezh.scylla.advanced.webserver.WebServerApplication
import io.drevezerezh.scylla.advanced.webserver.controller.BattleController
import io.drevezerezh.scylla.advanced.webserver.controller.PlayerController
import jakarta.servlet.ServletContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [WebServerApplication::class])
@WebAppConfiguration
class PlayerUCIntegTest(
    @Autowired
    private val webApplicationContext: WebApplicationContext,
) {

    private lateinit var context: IntegrationContext

    @BeforeEach
    @Throws(Exception::class)
    fun setup() {
        context = IntegrationContext(this.webApplicationContext)
    }

    @AfterEach
    fun tearDown() {
        context.clearAll()
    }

    @Test
    fun `All REST controllers shall be accessible`() {
        val servletContext: ServletContext = webApplicationContext.servletContext!!

        assertThat(servletContext)
            .isInstanceOf(MockServletContext::class.java)

        assertThat(webApplicationContext.getBean(BattleController::class.java))
            .isNotNull()

        assertThat(webApplicationContext.getBean(PlayerController::class.java))
            .isNotNull()
    }


    @Test
    fun `GET player ids shall no players when empty store`() {
        assertThat(context.getPlayerIdList())
            .isEmpty()
    }

    @Test
    fun `GET player ids shall all players when store filled`() {
        context.scenario(
            """
            create player John
            create player Jane
            create player Walter
        """.trimIndent()
        )

        val john = context.playerStore.getByName("John")
        val jane = context.playerStore.getByName("Jane")
        val walter = context.playerStore.getByName("Walter")

        assertThat(context.getPlayerIdList())
            .hasSize(3)
            .containsOnly(john.id, jane.id, walter.id)
    }


    @Test
    fun `GET full players shall no players when empty store`() {
        assertThat(context.getFullPlayerList())
            .isEmpty()
    }


    @Test
    fun `GET full players shall all players when store is filled`() {
        context.scenario(
            """
            create player John
            create player Jane
            create player Walter
        """.trimIndent()
        )

        val john = context.playerStore.getByName("John")
        val jane = context.playerStore.getByName("Jane")
        val walter = context.playerStore.getByName("Walter")

        assertThat(context.getFullPlayerList())
            .extracting("id", "name")
            .containsOnly(
                Tuple(john.id, john.name),
                Tuple(jane.id, jane.name),
                Tuple(walter.id, walter.name)
            )
    }


    @Disabled
    @Test
    fun `create single player previous`() {
        val mockMvc: MockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"John\" }")
        ).andDo(MockMvcResultHandlers.print())
//            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn()

        println("Status: ${result.response.status}")
        println("Charset: ${result.response.characterEncoding}")
        println("Content: ${result.response.contentAsString}")
    }


    @Test
    fun `create single player`() {
        context.scenario("create player John")

        val john = context.playerStore.getByName("John")
        assertThat(john.id)
            .isNotEmpty()
    }


}