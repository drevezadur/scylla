package io.drevezerezh.scylla.advanced.domain.api

class InvalidPlayerAttributeException(
    val playerId: String,
    val attributeNames: Set<String>,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause) {
}