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

package io.drevezerezh.scylla.advanced.persistance

import com.fasterxml.jackson.core.type.TypeReference
import io.drevezerezh.scylla.advanced.domain.api.fleet.Fleet
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetNotFoundException
import io.drevezerezh.scylla.advanced.domain.spi.FleetStore
import org.springframework.stereotype.Component

@Component
class MemoryFleetStoreBean : MemoryItemStore<FleetId, Fleet, FleetPJson>(), FleetStore {

    override fun extractDomainId(domain: Fleet): FleetId = domain.id

    override fun extractPersistanceId(persistance: FleetPJson): FleetId = persistance.id

    override fun toPersistance(domain: Fleet): FleetPJson =FleetMapper.toPersistance(domain)

    override fun toDomain(persistance: FleetPJson): Fleet = FleetMapper.toDomain(persistance)

    override fun itemNotFound(id: FleetId): Nothing = throw FleetNotFoundException(id)

    override fun getTypeReferenceList(): TypeReference<List<FleetPJson>> =
        object : TypeReference<List<FleetPJson>>() {}
}