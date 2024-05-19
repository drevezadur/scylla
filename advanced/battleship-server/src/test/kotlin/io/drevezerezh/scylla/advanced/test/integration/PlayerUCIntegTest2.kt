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
import io.drevezerezh.scylla.advanced.webserver.data.PlayerTestData.JANE
import io.drevezerezh.scylla.advanced.webserver.data.PlayerTestData.JOHN
import io.drevezerezh.scylla.advanced.webserver.data.PlayerTestData.WALTER
import jakarta.servlet.ServletContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.context.WebApplicationContext

@Suppress("EmptyMethod")
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [WebServerApplication::class])
@WebAppConfiguration
class PlayerUCIntegTest2(
    @Autowired
    private val webApplicationContext: WebApplicationContext,
) {

    private lateinit var context: IntegrationContext2

    @BeforeEach
    @Throws(Exception::class)
    fun setup() {
        context = IntegrationContext2(this.webApplicationContext)
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
        assertThat(context.getIdPlayerList())
            .isEmpty()
    }

    @Test
    fun `GET player ids shall all players when store filled`() {
        context.storePlayers(JOHN, JANE, WALTER)

        assertThat(context.getIdPlayerList())
            .containsOnly(JOHN.id, JANE.id, WALTER.id)
    }


    @Test
    fun `GET full players shall no players when empty store`() {
        assertThat(context.getFullPlayerList())
            .isEmpty()
    }


    @Test
    fun `GET full players shall all players when store is filled`() {
        context.storePlayers(JOHN, JANE, WALTER)

        assertThat(context.getFullPlayerList())
            .containsOnly(JOHN, JANE, WALTER)
    }
}