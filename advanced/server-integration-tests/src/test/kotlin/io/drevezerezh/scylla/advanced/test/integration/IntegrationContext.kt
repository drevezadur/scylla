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

import io.drevezerezh.scylla.advanced.domain.spi.BattleStore
import io.drevezerezh.scylla.advanced.domain.spi.FleetStore
import io.drevezerezh.scylla.advanced.domain.spi.PlayerStore
import io.drevezerezh.scylla.advanced.domain.spi.ShipStore
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.scenario.RestScenarioRunner
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.ServerContext
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle.BattleApiBean
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.player.PlayerApiBean
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.player.PlayerJson
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

class IntegrationContext(
    private val webApplicationContext: WebApplicationContext,
) {

    private val mockMvc: MockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build()

    private val serverContext = ServerContext(WebMvcRestEngine(mockMvc), "")
    private val playerApi = PlayerApiBean(serverContext)
    private val battleApi = BattleApiBean(serverContext)

    val playerStore: PlayerStore = webApplicationContext.getBean(PlayerStore::class.java)
    val battleStore: BattleStore = webApplicationContext.getBean(BattleStore::class.java)
    val fleetStore: FleetStore = webApplicationContext.getBean(FleetStore::class.java)
    val shipStore: ShipStore = webApplicationContext.getBean(ShipStore::class.java)

    private val scenarioRunner: RestScenarioRunner = RestScenarioRunner(serverContext)

    fun clearAll() {
        playerStore.deleteAll()
        battleStore.deleteAll()
        fleetStore.deleteAll()
        shipStore.deleteAll()
    }


    fun scenario(script: String): IntegrationContext {
        scenarioRunner.run(script)
        return this
    }


    fun getPlayerIdList(): List<String> {
        return playerApi.getAllIds()
    }

    fun getFullPlayerList(): List<PlayerJson> {
        return playerApi.getAll()
    }
}