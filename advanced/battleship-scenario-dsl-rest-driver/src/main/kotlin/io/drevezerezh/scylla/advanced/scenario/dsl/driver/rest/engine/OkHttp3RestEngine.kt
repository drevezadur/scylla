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

package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.engine

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.HttpHelper.JSON
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class OkHttp3RestEngine(
    private val httpClient: OkHttpClient,
    private val objectMapper: ObjectMapper = ObjectMapper().registerModules(JavaTimeModule())
) : RestEngine {

    override fun get(url: String, hasResponseContent : Boolean): RestResponse {
        val request = Request.Builder().url(url)
            .build()
        return runRequest(request)
    }


    override fun deleteJson(url: String, json: Any?, hasResponseContent:Boolean): RestResponse {
        val request = if( json== null) {
            Request.Builder().url(url)
                .delete()
                .build()
        } else {
            val body = toRequestBody(json)
            Request.Builder().url(url)
                .delete(body)
                .build()
        }

        return runRequest(request)
    }


    private fun toRequestBody( json : Any) : RequestBody {
        return objectMapper.writeValueAsString(json).toRequestBody(JSON)
    }


    private fun runRequest( request: Request): RestResponse {
        return httpClient.newCall(request).execute().use {
            toRestResponse(it)
        }
    }


    override fun postJson(url: String, json: Any, hasResponseContent : Boolean): RestResponse {
        val body = toRequestBody(json)
        val request = Request.Builder().url(url)
            .post(body)
            .build()
        return runRequest(request)
    }


    override fun putJson(url: String, json: Any, hasResponseContent:Boolean): RestResponse {
        val body = toRequestBody(json)
        val request = Request.Builder().url(url)
            .put(body)
            .build()
        return runRequest(request)
    }


    companion object {
        private fun toRestResponse(response: Response): RestResponse {
            return RestResponse(
                response.code,
                response.headers.toMap(),
                response.body?.string() ?: "",
            )
        }
    }
}