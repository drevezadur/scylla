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

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.drevezerezh.scylla.advanced.domain.api.player.Player
import io.drevezerezh.scylla.advanced.domain.spi.BattleStore
import io.drevezerezh.scylla.advanced.domain.spi.FleetStore
import io.drevezerezh.scylla.advanced.domain.spi.PlayerStore
import io.drevezerezh.scylla.advanced.domain.spi.ShipStore
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerJson
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

class IntegrationContext2(
    private val webApplicationContext: WebApplicationContext,
) {

    private val mockMvc: MockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build()
    private val objectMapper = ObjectMapper()
    private val playerStore: PlayerStore = webApplicationContext.getBean(PlayerStore::class.java)
    private val battleStore: BattleStore = webApplicationContext.getBean(BattleStore::class.java)
    private val fleetStore: FleetStore = webApplicationContext.getBean(FleetStore::class.java)
    private val shipStore: ShipStore = webApplicationContext.getBean(ShipStore::class.java)


    fun clearAll() {
        playerStore.deleteAll()
        battleStore.deleteAll()
        fleetStore.deleteAll()
        shipStore.deleteAll()
    }


    fun storePlayers(vararg players: PlayerJson) {
        val domainPlayers: List<Player> = players.map { Player(it.id, it.name) }
        playerStore.saveAll(*domainPlayers.toTypedArray())
    }

    fun getIdPlayerList(): List<String> {
        val resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/players"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        val result = resultActions.andReturn()
        val contentAsString = result.response.contentAsString
        return objectMapper.readValue(contentAsString, object : TypeReference<List<String>>() {})
    }

    fun getFullPlayerList(): List<PlayerJson> {
        val resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/players?style=full"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        val result = resultActions.andReturn()
        val contentAsString = result.response.contentAsString
        return objectMapper.readValue(contentAsString, object : TypeReference<List<PlayerJson>>() {})
    }


}