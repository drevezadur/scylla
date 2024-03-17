package io.drevezerezh.scylla.advanced.domain.api

class PlayerAlreadyExistException(
    val playerId: String,
    val attributeNames: Set<String> = emptySet(),
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)