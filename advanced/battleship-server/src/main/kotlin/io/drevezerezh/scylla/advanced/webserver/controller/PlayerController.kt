package io.drevezerezh.scylla.advanced.webserver.controller

import io.drevezerezh.scylla.advanced.domain.api.*
import io.drevezerezh.scylla.advanced.webserver.controller.PlayerMapper.toJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerCreationJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.UpdatePlayerJson
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder


@RestController
@RequestMapping(path = ["/players"])
class PlayerController(
    private val playerManager: PlayerManager
) {

    /**
     * Get all players
     */
    @GetMapping(path = ["", "/"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllPlayers(@RequestParam(name = "style", required = false) style: String?): ResponseEntity<Any> {
        LOGGER.info("GET /players")
        return when (style?.lowercase()) {
            null, "", "id" -> getAllPlayerIds()
            "full" -> getAllFullPlayers()
            else -> getAllPlayersFailure(style)
        }
    }

    private fun getAllPlayerIds(): ResponseEntity<Any> {
        val idList = playerManager.getAllPlayers().map(Player::id)
        return ResponseEntity.ok(idList)
    }

    private fun getAllFullPlayers(): ResponseEntity<Any> {
        val playerJsonList = playerManager.getAllPlayers().map(PlayerMapper::toJson)
        return ResponseEntity.ok(playerJsonList)
    }

    private fun getAllPlayersFailure(style: String): ResponseEntity<Any> {
        val content = ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Unsupported style '$style'").type
        return ResponseEntity.badRequest().body(content)
    }


    /**
     * Get a player from its identifier
     * @param id : the identifier of the  player
     * @return the player with 200 code
     */
    @GetMapping(path = ["/{id}", "/{id}/"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPlayerById(@PathVariable("id") id: String): ResponseEntity<PlayerJson> {
        LOGGER.info("GET /players/$id")
        val player = playerManager.getPlayerById(id)
        val playerJson = toJson(player)
        return ResponseEntity.ok(playerJson)
    }


    /**
     * Create a new player
     * @param createPlayer the information about the player to create
     * @return an HTTP response with 201 code and location field filled
     */
    @PostMapping(
        path = ["", "/"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE]
    )
    fun createPlayer(
        @RequestBody createPlayer: PlayerCreationJson,
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        LOGGER.info("POST /players")
        val player = playerManager.createPlayer(PlayerCreation(createPlayer.name))
        val uri = ServletUriComponentsBuilder.fromRequest(request)
            .path("/${player.id}")
            .build()
            .toUri()
        return ResponseEntity.created(uri).build()
    }


    /**
     * Update de player
     * @param id the identifier of the player
     * @param updatePlayer the information to update
     * @return an HTTP response with 204 code
     */
    @PutMapping(
        path = ["/{id}", "/{id}/"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updatePlayer(
        @PathVariable("id") id: String,
        @RequestBody updatePlayer: UpdatePlayerJson
    ): ResponseEntity<PlayerJson> {
        LOGGER.info("PUT /players/$id")
        val playerUpdateDomain = PlayerUpdate(updatePlayer.name)
        val updatedPlayer = playerManager.update(id, playerUpdateDomain)
        val updatedPlayerJson = toJson(updatedPlayer)
        return ResponseEntity.ok(updatedPlayerJson)
    }


    @DeleteMapping(path = ["/{id}", "/{id}/"])
    fun deletePlayer(@PathVariable("id") id: String): ResponseEntity<Void> {
        LOGGER.info("DELETE /players/$id")
        if (!playerManager.deletePlayer(id))
            throw PlayerNotFoundException(id)

        return ResponseEntity.noContent().build()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PlayerController::class.java)
    }
}