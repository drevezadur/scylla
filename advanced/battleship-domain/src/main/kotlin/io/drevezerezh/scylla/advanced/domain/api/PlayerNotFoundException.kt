package io.drevezerezh.scylla.advanced.domain.api

class PlayerNotFoundException(
    val playerId: String,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)