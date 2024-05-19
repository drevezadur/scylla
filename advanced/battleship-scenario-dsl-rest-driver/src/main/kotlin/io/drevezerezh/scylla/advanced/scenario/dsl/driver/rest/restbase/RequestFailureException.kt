package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase

open class RequestFailureException(
    open val code: Int,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)