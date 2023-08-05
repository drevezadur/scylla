package io.gofannon.scylla.homework.service

import io.gofannon.scylla.homework.domain.internal.DefaultGameFactory
import io.gofannon.scylla.homework.domain.internal.DefaultPlayerBoardFactory
import io.gofannon.scylla.homework.domain.internal.DefaultShipFactory
import io.gofannon.scylla.homework.domain.internal.ShipFactory
import io.gofannon.scylla.homework.lang.*
import io.gofannon.scylla.homework.service.internal.DefaultCommandFactory
import io.gofannon.scylla.homework.service.internal.DefaultScenarioFactory
import io.gofannon.scylla.homework.service.internal.ScenarioFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URL

class ScenarioLoaderTest {

    private val shipFactory: ShipFactory = DefaultShipFactory()
    private val fleetBoardFactory = DefaultPlayerBoardFactory(shipFactory)
    private val gameFactory = DefaultGameFactory(fleetBoardFactory)
    private val commandFactory = DefaultCommandFactory()
    private val scenarioFactory: ScenarioFactory = DefaultScenarioFactory(commandFactory)

    @Test
    fun `loadFromResource() shall handle deployment only`() {
        val loader = ScenarioLoader(scenarioFactory)
        val scenario = loader.loadFromResource("/battle-test-3-deployment.txt")

        val game = gameFactory.createGame()
        scenario.execute(game)

        assertThat(game.getStatus())
            .isSameAs(GameStatus.DEPLOYED)

        assertThat(game.getTurnNumber())
            .isEqualTo(0)

        assertThat(game.getPlayerBoard(Player.PLAYER_A).getPlayerState())
            .isSameAs(PlayerState.FLEET_DEPLOYED)
    }

    @Test
    fun `loadFromResource() shall handle deployment and some shots`() {
        val loader = ScenarioLoader(scenarioFactory)
        val scenario = loader.loadFromResource("/battle-test-1.txt")

        val game = gameFactory.createGame()
        scenario.execute(game)

        assertThat(game.getStatus())
            .isSameAs(GameStatus.RUNNING)

        assertThat(game.getTurnNumber())
            .isEqualTo(7)

        assertThat(game.getPlayerBoard(Player.PLAYER_A).getShip(ShipType.CARRIER).status)
            .isSameAs(ShipStructuralStatus.DESTROYED)
        assertThat(game.getPlayerBoard(Player.PLAYER_A).getShip(ShipType.BATTLESHIP).status)
            .isSameAs(ShipStructuralStatus.DAMAGED)
    }

    @Test
    fun `loadFromResource() shall handle deployment and full battle`() {
        val loader = ScenarioLoader(scenarioFactory)
        val scenario = loader.loadFromResource("/battle-test-2-full-battle.txt")

        val game = gameFactory.createGame()
        scenario.execute(game)

        assertThat(game.getStatus())
            .isSameAs(GameStatus.FINISHED)

        assertThat(game.getTurnNumber())
            .isEqualTo(17)

        assertThat(game.getPlayerBoard(Player.PLAYER_A).getPlayerState())
            .isSameAs(PlayerState.LOSER)

        assertThat(game.getPlayerBoard(Player.PLAYER_B).getPlayerState())
            .isSameAs(PlayerState.WINNER)

        assertThat(game.getPlayerBoard(Player.PLAYER_A).getShip(ShipType.CARRIER).status)
            .isSameAs(ShipStructuralStatus.DESTROYED)
        assertThat(game.getPlayerBoard(Player.PLAYER_A).getShip(ShipType.BATTLESHIP).status)
            .isSameAs(ShipStructuralStatus.DESTROYED)
    }


    @Test
    fun `loadFromPath shall load the full battle`() {
        val loader = ScenarioLoader(scenarioFactory)
        val url: URL = ScenarioLoaderTest::class.java.getResource("/battle-test-2-full-battle.txt")!!
        val file = File(url.toURI())

        val scenario = loader.loadFromPath(file.toPath())

        val game = gameFactory.createGame()
        scenario.execute(game)

        assertThat(game.getStatus())
            .isSameAs(GameStatus.FINISHED)
    }
}