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

package io.drevezadur.scylla.teacher.restserver.service.util

import com.fasterxml.jackson.databind.ObjectMapper
import io.drevezadur.scylla.teacher.restserver.lang.BattleStatus
import io.drevezadur.scylla.teacher.restserver.lang.BattleUUID
import io.drevezadur.scylla.teacher.restserver.lang.Location
import io.drevezadur.scylla.teacher.restserver.lang.ShipType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI
import java.util.*


interface HttpErrorBuilder {
    fun setMessage(message: String): HttpErrorBuilder
    fun setAttribute(key: String, value: String): HttpErrorBuilder
    fun toResponse(status: HttpStatus): ResponseEntity<HttpErrorContent>
}


private class HttpErrorBuilderImpl(private val errorCode: String) : HttpErrorBuilder {

    private var message: String? = null
    private val attributes = ArrayList<Attribute>()

    override fun setMessage(message: String): HttpErrorBuilder {
        this.message = message
        return this
    }

    override fun setAttribute(key: String, value: String): HttpErrorBuilder {
        attributes.add(Attribute(key, value))
        return this
    }

    override fun toResponse(status: HttpStatus): ResponseEntity<HttpErrorContent> {
        val localMessage = message ?: throw IllegalArgumentException("message shall not be null")
        return ResponseEntity.status(status)
            .body(HttpErrorContent(errorCode, localMessage, attributes))
    }
}


object ResponseHelper {

    private val jsonMapper = ObjectMapper()

    private fun createHttpError(error: String): HttpErrorBuilder = HttpErrorBuilderImpl(error)

    @Suppress("unused")
    fun notImplemented(): ResponseEntity<HttpErrorContent> {
        return createHttpError("NOT_YET_IMPLEMENTED")
            .setMessage("Feature not yet implemented")
            .toResponse(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    fun toPlayerNotFoundInBattleResponse(battleId: String, playerId: String): ResponseEntity<HttpErrorContent> {
        return createHttpError("PLAYER_NOT_FOUND_IN_BATTLE")
            .setMessage("Cannot found player with id '$playerId' in battle with id '$battleId'")
            .setAttribute("battleId", battleId)
            .setAttribute("playerId", playerId)
            .toResponse(HttpStatus.NOT_FOUND)
    }

    fun toBattleNotFoundResponse(battleId: String): ResponseEntity<HttpErrorContent> {
        return createHttpError("BATTLE_NOT_FOUND")
            .setMessage("Cannot found battle with id '$battleId'")
            .setAttribute("battleId", battleId)
            .toResponse(HttpStatus.NOT_FOUND)
    }

    fun toBattleNotFoundResponse(battleId: UUID): ResponseEntity<HttpErrorContent> =
        toBattleNotFoundResponse(battleId.toString())

    fun toFleetNotFoundResponse(battleId: String, playerId: String): ResponseEntity<HttpErrorContent> {
        return createHttpError("PLAYER_NOT_IN_BATTLE")
            .setMessage("Player '$playerId' is not in battle '$battleId'")
            .setAttribute("battleId", battleId)
            .setAttribute("playerId", playerId)
            .toResponse(HttpStatus.NOT_FOUND)
    }

    fun toFleetNotFoundResponse(battleId: UUID, playerId: UUID): ResponseEntity<HttpErrorContent> =
        toFleetNotFoundResponse(battleId.toString(), playerId.toString())


    fun toShipNotFoundResponse(battleId: String, playerId: String, shipType: String): ResponseEntity<HttpErrorContent> {
        return createHttpError("SHIP_NOT_FOUND")
            .setMessage("Cannot found ship of battle '$battleId' and player '$playerId' and type '$shipType'")
            .setAttribute("battleId", battleId)
            .setAttribute("playerId", playerId)
            .setAttribute("shipType", shipType)
            .toResponse(HttpStatus.NOT_FOUND)
    }

    fun toShipNotFoundResponse(battleId: UUID, playerId: UUID, shipType: ShipType): ResponseEntity<HttpErrorContent> =
        toShipNotFoundResponse(battleId.toString(), playerId.toString(), shipType.name)


    fun toPlayerNotFoundResponse(playerId: String): ResponseEntity<HttpErrorContent> {
        return createHttpError("PLAYER_NOT_FOUND")
            .setMessage("Cannot found user with id '$playerId'")
            .setAttribute("playerId", playerId)
            .toResponse(HttpStatus.NOT_FOUND)
    }
    

    fun toShipCollisionResponse(
        battleId: UUID,
        playerId: UUID,
        shipType: ShipType,
        location: Location
    ): ResponseEntity<HttpErrorContent> {
        return createHttpError("SHIP_DEPLOYMENT_COLLISION")
            .setMessage("Cannot deploy ship of type '$shipType' in battle with id '$battleId' by player '$playerId'. A collision occurs on square (${location.x},${location.y})")
            .setAttribute("shipType", shipType.name)
            .setAttribute("battleId", battleId.toString())
            .setAttribute("playerId", playerId.toString())
            .setAttribute("location", "${location.x}${location.y}")
            .toResponse(HttpStatus.BAD_REQUEST)
    }


    fun toUnexpectedBattleStatusResponse(
        battleId: BattleUUID,
        currentStatus: BattleStatus,
        expectedStatus: BattleStatus
    ): ResponseEntity<HttpErrorContent> {
        return createHttpError("UNEXPECTED_BATTLE_STATUS")
            .setMessage("Battle shall have status $expectedStatus for this action. Current status is $currentStatus")
            .setAttribute("battleId", "$battleId")
            .setAttribute("currentStatus", "$currentStatus")
            .setAttribute("expectedStatus", "$expectedStatus")
            .toResponse(HttpStatus.BAD_REQUEST)
    }

    fun createdAt(uri: String): ResponseEntity<String> {
        return ResponseEntity.created(URI(uri)).build()
    }

    fun toUnsupportedShipTypeResponse(string: String): ResponseEntity<HttpErrorContent> {
        return createHttpError("INVALID_SHIP_TYPE")
            .setMessage("Unknown ship type '$string'")
            .setAttribute("shipType", string)
            .toResponse(HttpStatus.BAD_REQUEST)
    }


    fun noContent(): ResponseEntity<Any> {
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    fun toHttpErrorContent(string: String): HttpErrorContent {
        return jsonMapper.readValue(string, HttpErrorContentPojo::class.java)
            .toHttpErrorContent()
    }
}

private class HttpErrorContentPojo(
    var errorCode: String = "",
    var message: String = "",
    var attributes: MutableList<AttributePojo> = ArrayList<AttributePojo>()
) {

    fun toHttpErrorContent(): HttpErrorContent {
        val attributeList = attributes.map(AttributePojo::toAttribute)
        return HttpErrorContent(errorCode, message, attributeList)
    }
}

private class AttributePojo(
    var key: String = "default",
    var value: String = ""
) {
    fun toAttribute(): Attribute {
        return Attribute(key, value)
    }
}