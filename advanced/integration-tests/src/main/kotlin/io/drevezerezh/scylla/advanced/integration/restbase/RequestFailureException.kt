package io.drevezerezh.scylla.advanced.integration.restbase

open class RequestFailureException(
    open val code: Int,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)