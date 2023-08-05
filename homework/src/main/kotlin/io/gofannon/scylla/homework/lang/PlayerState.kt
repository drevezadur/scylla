/*
 * Copyright (c) 2023. gofannon.io
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

package io.gofannon.scylla.homework.lang

/**
 * The state of play of the player
 */
enum class PlayerState {

    /**
     * The fleet is currently in deployment.
     *
     * Not all ships have been deployed
     */
    DEPLOYING_FLEET,

    /**
     * The fleet is deployed, ready for fighting
     */
    FLEET_DEPLOYED,

    /**
     * The fleet is deployed and the fight against the other player has started
     */
    FIGHTING,

    /**
     * The fight is over and the current fleet has win the battle
     */
    WINNER,

    /**
     * The fight is over and the current fleet has lost the battle
     */
    LOSER
}