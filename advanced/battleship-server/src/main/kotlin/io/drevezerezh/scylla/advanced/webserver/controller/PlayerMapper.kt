package io.drevezerezh.scylla.advanced.webserver.controller

import io.drevezerezh.scylla.advanced.domain.api.Player
import io.drevezerezh.scylla.advanced.domain.api.PlayerCreation
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerCreationJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerJson

object PlayerMapper {

    fun toJson(domain: Player): PlayerJson {
        return PlayerJson(
            domain.id,
            domain.name
        )
    }

    fun toDomain(domain: PlayerCreationJson): PlayerCreation {
        return PlayerCreation(domain.name)
    }

}