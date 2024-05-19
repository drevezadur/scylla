package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.player

import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.RequestFailureException

class PlayerUpdateFailureException(
    val id: String,
    override val code: Int,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RequestFailureException(code,message, cause)