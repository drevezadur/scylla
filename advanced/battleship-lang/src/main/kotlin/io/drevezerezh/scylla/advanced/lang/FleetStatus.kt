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

package io.drevezerezh.scylla.advanced.lang

/**
 * The status of a fleet
 */
enum class FleetStatus {
    /**
     * The fleet is not completely deployed
     */
    NOT_DEPLOYED,

    /**
     * The fleet is deployed and none of ships are damaged nor sunk
     */
    OPERATIONAL,

    /**
     * At least one ship has been damaged
     */
    DAMAGED,

    /**
     * The fleet is deployed and all ships are sunk
     */
    DESTROYED
}