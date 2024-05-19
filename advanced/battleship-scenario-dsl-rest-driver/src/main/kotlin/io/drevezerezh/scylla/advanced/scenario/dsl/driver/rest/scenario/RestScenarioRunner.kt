package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.scenario

import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.scenario.dsl.*
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.ServerContext
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle.BattleApi
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle.BattleApiBean
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle.BattleCreationJson
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle.ShipDeploymentJson
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.player.PlayerApi
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.player.PlayerApiBean
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.player.PlayerCreationJson
import org.slf4j.LoggerFactory

class RestScenarioRunner(
    context: ServerContext
) {

    private val playerApi: PlayerApi = PlayerApiBean(context)
    private val battleApi: BattleApi = BattleApiBean(context)
    private val scenarioListener = RestScenarioListener(playerApi, battleApi)
    private val scenarioParser = ScenarioParserFactory().create()

    init {
        scenarioParser.addListener(scenarioListener)
    }


    fun run(scenario: String) {
        scenarioParser.parse(scenario)
    }
}


private class RestScenarioListener(
    private val playerApi: PlayerApi,
    private val battleApi: BattleApi
) : ScenarioInstructionListener {

    private val logger = LoggerFactory.getLogger(RestScenarioListener::class.java)

    /**
     * key = player name
     * value = player id
     */
    val playerNames: MutableMap<String, String> = HashMap()
    var player1Name = ""
    var player2Name = ""
    var battleId = ""

    override fun onCreatePlayerInstruction(line: Int, instruction: PlayerCreationInstruction) {
        logger.info("---> onCreatePlayerInstruction ")
        val playerId = playerApi.create(PlayerCreationJson(instruction.playerName))
        playerNames[instruction.playerName] = playerId
    }

    override fun onShipDeploymentInstruction(line: Int, instruction: ShipDeploymentInstruction) {
        val battlePlayer = toBattlePlayer(instruction.playerName)
        val shipDeployment = ShipDeploymentJson(instruction.shipType, instruction.location, instruction.orientation)
        battleApi.deployShip(battleId, battlePlayer, shipDeployment)
    }

    private fun toBattlePlayer(name: String): BattlePlayer {
        return when (name) {
            player1Name -> BattlePlayer.FIRST
            player2Name -> BattlePlayer.SECOND
            else -> throw IllegalArgumentException("Cannot found player '$name' in current battle '$battleId'")
        }
    }

    override fun onShotInstruction(line: Int, instruction: ShotInstruction) {
        val battlePlayer = toBattlePlayer(instruction.playerName)
        val target = GridLocation.of(instruction.location)
        battleApi.shot(battleId, battlePlayer, target)
    }

    override fun onStartBattleInstruction(line: Int, instruction: BattleStartingInstruction) {
        this.player1Name = instruction.player1Name
        this.player2Name = instruction.player2Name
        battleId = battleApi.create(BattleCreationJson(this.player1Name, this.player2Name))
    }
}