package io.drevezerezh.scylla.advanced.persistance

import io.drevezerezh.scylla.advanced.domain.api.Player

object PlayerMapper {

    fun toPersistance( domain : Player) : PlayerPJson {
        return PlayerPJson(
            domain.id,
            domain.name
        )
    }

    fun toDomain( persisted : PlayerPJson) : Player {
        return Player(
            persisted.id,
            persisted.name
        )
    }
}