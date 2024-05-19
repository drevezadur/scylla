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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.engine.RestEngine
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.engine.RestResponse
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.LinkedMultiValueMap

class WebMvcRestEngine(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper = ObjectMapper().registerModules(JavaTimeModule(), KotlinModule.Builder().build())
) : RestEngine {

    override fun get(url: String, hasResponseContent : Boolean): RestResponse {
        val request = createRequest(HttpMethod.GET, url)
        return performRequest(request, hasResponseContent)
    }


    private fun createRequest(httpMethod: HttpMethod, url: String, json: Any? = null): MockHttpServletRequestBuilder {
        val decomposedUrl = decomposeUrl(url)

        val request = MockMvcRequestBuilders.request(httpMethod, decomposedUrl.first)
        if (decomposedUrl.second.isNotEmpty())
            request.params(decomposedUrl.second)
        if (json != null)
            request.content(objectMapper.writeValueAsString(json))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charsets.UTF_8)

        return request
    }


    private fun decomposeUrl(fullUrl: String): Pair<String, LinkedMultiValueMap<String, String>> {
        val startIndex: Int = fullUrl.indexOf('?')
        if (startIndex < 0)
            return Pair(fullUrl, LinkedMultiValueMap<String, String>())

        val urlPart = fullUrl.substring(0, startIndex)

        val multiValueMap = LinkedMultiValueMap<String, String>()
        fullUrl.substring(startIndex + 1).split('&').forEach {
            val fields = it.split('=')
            multiValueMap[fields[0]] = listOf(fields[1])
        }

        return Pair(urlPart, multiValueMap)
    }


    private fun performRequest(
        request: MockHttpServletRequestBuilder,
        hasResponseContent: Boolean
    ): RestResponse {
//        val result = this.mockMvc.perform(request)
//            .andDo(MockMvcResultHandlers.print())
//            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//            .andReturn()
        val result = doPerform(request, hasResponseContent)

        return RestResponse(
            result.response.status,
            extractHeaders(result.response),
            result.response.contentAsString
        )
    }


    private fun doPerform(request: MockHttpServletRequestBuilder, hasResponseContent: Boolean): MvcResult {
        val resultActions = this.mockMvc.perform(request)
            .andDo(MockMvcResultHandlers.print())
        if (hasResponseContent)
            resultActions.andExpect(
                MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            )

        return resultActions.andReturn()
    }


    private fun extractHeaders(response: MockHttpServletResponse): Map<String, String> {
        val map = HashMap<String, String>()
        for (name in response.headerNames) {
            map[name] = response.getHeader(name) ?: ""
        }
        return map
    }


    override fun postJson(url: String, json: Any, hasResponseContent : Boolean): RestResponse {
        val request = createRequest(HttpMethod.POST, url, json)
        return performRequest(request, hasResponseContent)
    }


    override fun putJson(url: String, json: Any, hasResponseContent:Boolean): RestResponse {
        val request = createRequest(HttpMethod.PUT, url, json)
        return performRequest(request, hasResponseContent)
    }


    override fun deleteJson(url: String, json: Any?, hasResponseContent : Boolean): RestResponse {
        val request = createRequest(HttpMethod.DELETE, url, json)
        return performRequest(request, hasResponseContent)
    }
}