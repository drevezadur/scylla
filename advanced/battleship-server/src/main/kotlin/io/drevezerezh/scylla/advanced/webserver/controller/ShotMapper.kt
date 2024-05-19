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

import io.drevezerezh.scylla.advanced.domain.api.shot.ShotReport
import io.drevezerezh.scylla.advanced.webserver.controller.dto.ShotReportJson

/**
 * Set of mapping methods related to shots
 */
object ShotMapper {

    /**
     * Convert a domain shot report to DTO
     * @param domain a shot report in domain format
     * @return the DTO version of the shot report
     */
    fun toDto(domain: ShotReport): ShotReportJson {
        return ShotReportJson(domain.shotResult, domain.victorious)
    }
}