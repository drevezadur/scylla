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

package io.drevezerezh.scylla.advanced.domain.spi

import io.drevezerezh.scylla.advanced.domain.api.fleet.FleetId
import io.drevezerezh.scylla.advanced.domain.api.ship.Ship
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipId
import io.drevezerezh.scylla.advanced.domain.api.ship.ShipNotFoundException

/**
 * Stub of [ShipStore] for testing purpose
 *
 * This implementation is very simple and does not provide any "protection" such as thread safe
 */
class ShipStoreStub : AbstractItemStoreStub<ShipId, Ship>(), ShipStore {

    override fun extractId(item: Ship): ShipId = item.id

    override fun itemAlreadyExists(id: ShipId): Nothing {
        throw ShipNotFoundException(id)
    }

    override fun getFleet(fleetId: FleetId): Set<Ship> {
        return getAll().filter { it.fleetId == fleetId }.toSet()
    }
}