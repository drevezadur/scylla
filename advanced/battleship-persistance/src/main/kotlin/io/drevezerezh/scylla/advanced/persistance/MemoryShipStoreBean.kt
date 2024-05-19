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
import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.ship.Ship
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipId
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipNotFoundException
import io.drevezerezh.scylla.advanced.domain.spi.ShipStore
import org.springframework.stereotype.Component

@Component
class MemoryShipStoreBean : MemoryItemStore<ShipId, Ship, ShipPJson>(), ShipStore {

    override fun extractDomainId(domain: Ship): ShipId = domain.id

    override fun extractPersistanceId(persistance: ShipPJson): ShipId = persistance.id

    override fun toPersistance(domain: Ship): ShipPJson = ShipMapper.toPersistance(domain)

    override fun toDomain(persistance: ShipPJson): Ship = ShipMapper.toDomain(persistance)

    override fun itemNotFound(id: ShipId): Nothing = throw ShipNotFoundException(id)

    override fun getTypeReferenceList(): TypeReference<List<ShipPJson>> =
        object : TypeReference<List<ShipPJson>>() {}

    override fun getFleet(fleetId: FleetId): Set<Ship> = getAll { it.fleetId == fleetId }.toSet()
}