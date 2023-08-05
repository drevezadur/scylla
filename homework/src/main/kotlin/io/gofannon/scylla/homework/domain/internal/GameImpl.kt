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

import io.gofannon.scylla.homework.domain.Game
import io.gofannon.scylla.homework.domain.PlayerBoard
import io.gofannon.scylla.homework.lang.*
import io.gofannon.scylla.homework.lang.GameStatus.*
import io.gofannon.scylla.homework.lang.Player.PLAYER_A
import io.gofannon.scylla.homework.lang.Player.PLAYER_B
import java.lang.IllegalStateException

internal class GameImpl(
    boardFactory: PlayerBoardFactory
) : Game, GameManager {

    private val boards: Map<Player, MutablePlayerBoard> = createFleets(boardFactory, this)

    private var status = DEPLOYMENT
    private var winner: Player? = null

    private var nextPlayer = PLAYER_A
    private var turn: Int = 0


    override fun getStatus(): GameStatus = status
    override fun getWinner(): Player? = findPlayerWithBoardState(PlayerState.WINNER)

    private fun findPlayerWithBoardState(state: PlayerState): Player? {
        return boards.map { it.value }
            .filter { it.getPlayerState() == state }
            .map { it.player }
            .firstOrNull()
    }

    override fun getLoser(): Player? = findPlayerWithBoardState(PlayerState.LOSER)


    override fun getPlayerBoard(player: Player): PlayerBoard {
        // players are completely filled at initialization
        return boards[player]!!
    }

    override fun fleetDeployed(player: Player) {
        if (boards[player]!!.getFleetStatus() != FleetStatus.UNHARMED)
            throw IllegalStateException("Invalid fleet status. It should be UNHARMED")

        val allFleetDeployed = boards.all { it.value.getPlayerState() == PlayerState.FLEET_DEPLOYED }
        if (allFleetDeployed) {
            status = DEPLOYED
        }
    }

    override fun shot(target: Player, at: Location): ShotResult {
        checkGameInPlayStatus()
        checkNextPlayer(target)

        updateInternalStateBeforeAction(target)

        val targetBoard = boardOf(target)
        val shotResult = targetBoard.resolveShot(at)

        if (shotResult != ShotResult.MISSED)
            updateInternalStateAfterAction(target)

        nextPlayer = getOpponent(target)
        return shotResult
    }

    private fun updateInternalStateBeforeAction(player: Player) {
        if (status == DEPLOYED) {
            status = RUNNING
            turn = 1
            boards.values.forEach(MutablePlayerBoard::startFighting)
        } else if (isTurnFirstPlayer(player)) {
            turn++
        }
    }

    private fun checkGameInPlayStatus() {
        when (status) {
            DEPLOYMENT -> throw IllegalArgumentException("Game not running, deployment phase is on going")
            FINISHED -> throw IllegalArgumentException("Game is over")
            DEPLOYED, RUNNING -> {}
        }
    }

    private fun checkNextPlayer(player: Player) {
        if (player != nextPlayer)
            throw IllegalArgumentException("Player $player cannot fire, it is the turn of $nextPlayer.")
    }

    private fun isTurnFirstPlayer(player: Player): Boolean {
        return player == PLAYER_A
    }

    private fun boardOf(currentPlayer: Player): MutablePlayerBoard {
        val targetFleetId = getOpponent(currentPlayer)
        return boards[targetFleetId]!!
    }

    private fun getOpponent(player: Player): Player {
        return when (player) {
            PLAYER_A -> PLAYER_B
            PLAYER_B -> PLAYER_A
        }
    }

    private fun updateInternalStateAfterAction(target: Player) {
        val targetBoard = boardOf(target)
        if( targetBoard.getFleetStatus() == FleetStatus.SUNK ) {
            val shooter = getOpponent(target)
            val shooterBoard = boardOf(shooter)
            shooterBoard.setPlayerState(PlayerState.WINNER)
            targetBoard.setPlayerState(PlayerState.LOSER)
            winner = target
            status = FINISHED
        }
    }

    override fun getTurnNumber(): Int {
        return turn
    }

    companion object {
        private fun createFleets(
            boardFactory: PlayerBoardFactory,
            gameManager: GameManager
        ): Map<Player, MutablePlayerBoard> {
            return Player.entries
                .map { boardFactory.createInstance(it, gameManager) }
                .associateBy { it.player }
        }
    }
}