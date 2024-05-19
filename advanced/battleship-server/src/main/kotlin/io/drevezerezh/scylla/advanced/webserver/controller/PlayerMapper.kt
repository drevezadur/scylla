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

package io.drevezerezh.scylla.advanced.webserver.controller

import io.drevezerezh.scylla.advanced.domain.api.player.Player
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerCreation
import io.drevezerezh.scylla.advanced.domain.api.player.PlayerUpdate
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerCreationJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerJson
import io.drevezerezh.scylla.advanced.webserver.controller.dto.PlayerUpdateJson

object PlayerMapper {

    fun toJson(domain: Player): PlayerJson {
        return PlayerJson(
            domain.id,
            domain.name
        )
    }

    fun toDomain(domain: PlayerCreationJson): PlayerCreation {
        return PlayerCreation(domain.name)
    }

    fun toDomain(domain: PlayerUpdateJson): PlayerUpdate {
        return PlayerUpdate(domain.name)
    }
}