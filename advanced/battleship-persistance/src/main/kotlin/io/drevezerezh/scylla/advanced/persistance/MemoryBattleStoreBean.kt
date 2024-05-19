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
import io.drevezerezh.scylla.advanced.domain.api.battle.Battle
import io.drevezerezh.scylla.advanced.domain.api.battle.BattleNotFoundException
import io.drevezerezh.scylla.advanced.domain.spi.BattleStore
import org.springframework.stereotype.Component

@Component
class MemoryBattleStoreBean : MemoryItemStore<String, Battle, BattlePJson>(), BattleStore {

    override fun extractDomainId(domain: Battle): String = domain.id

    override fun extractPersistanceId(persistance: BattlePJson): String = persistance.id

    override fun toPersistance(domain: Battle): BattlePJson = BattleMapper.toPersistance(domain)

    override fun toDomain(persistance: BattlePJson): Battle = BattleMapper.toDomain(persistance)

    override fun itemNotFound(id: String): Nothing = throw BattleNotFoundException(id)

    override fun getTypeReferenceList(): TypeReference<List<BattlePJson>> =
        object : TypeReference<List<BattlePJson>>() {}

}