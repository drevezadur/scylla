package io.drevezerezh.scylla.advanced.integration.player

import io.drevezerezh.scylla.advanced.integration.restbase.RequestFailureException

class PlayerCreationFailureException(
    val name: String,
    override val code : Int,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RequestFailureException(code,message, cause)