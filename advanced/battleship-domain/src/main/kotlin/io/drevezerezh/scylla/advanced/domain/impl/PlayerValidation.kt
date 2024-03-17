package io.drevezerezh.scylla.advanced.domain.impl

import io.drevezerezh.scylla.advanced.domain.api.InvalidPlayerAttributeException
import io.drevezerezh.scylla.advanced.domain.api.PlayerCreation
import io.drevezerezh.scylla.advanced.domain.api.PlayerUpdate

object PlayerValidation {

    private val namePattern = Regex("[a-zA-Z ]{3,20}")

    fun checkValidity(playerCreation: PlayerCreation) {
        if (!isValidName(playerCreation.name))
            throw InvalidPlayerAttributeException("in-creation", setOf("name"))
    }

    fun isValidName(name: String): Boolean {
        return name.matches(namePattern)
    }

    fun checkValidity(playerId : String, playerUpdate: PlayerUpdate) {
        if (playerUpdate.name != null && !isValidName(playerUpdate.name))
            throw InvalidPlayerAttributeException(playerId, setOf("name"))
    }
}