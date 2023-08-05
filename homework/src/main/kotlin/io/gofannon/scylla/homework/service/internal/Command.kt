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

package io.gofannon.scylla.homework.service.internal

import io.gofannon.scylla.homework.domain.Game

/**
 * A command to execute in a game.
 */
internal interface Command {

    /**
     * Get the command in a text format in english language
     * @return the command in english
     */
    fun toInstruction(): String

    /**
     * Execute the command in a game
     * @param game the game on which apply the command
     */
    fun execute(game: Game)

}