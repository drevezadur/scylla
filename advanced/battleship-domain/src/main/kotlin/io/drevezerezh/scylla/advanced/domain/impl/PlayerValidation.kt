/*
 * Copyright (c) 2024 gofannon.xyz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.drevezerezh.scylla.advanced.domain.impl

import io.drevezerezh.scylla.advanced.domain.api.player.InvalidPlayerAttributeException
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerCreation
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerUpdate

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