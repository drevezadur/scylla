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

package io.gofannon.scylla.homework.domain.internal

import io.gofannon.scylla.homework.domain.PlayerBoard
import io.gofannon.scylla.homework.lang.Location
import io.gofannon.scylla.homework.lang.PlayerState
import io.gofannon.scylla.homework.lang.ShotResult

/**
 * An editable player board
 */
interface MutablePlayerBoard : PlayerBoard {

    /**
     * The board
     */
    fun startFighting()

    /**
     * The board receives a shot on its fleet
     * @param shotLocation the location of the shot
     * @return the result of the shot
     */
    fun takeShotAt(shotLocation: Location): ShotResult

    /**
     * The player of the board is designated as the winner
     */
    fun setPlayerState(state : PlayerState)
}