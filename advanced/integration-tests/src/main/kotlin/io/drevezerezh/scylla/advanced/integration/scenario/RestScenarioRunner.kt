package io.drevezerezh.scylla.advanced.integration.scenario

import io.drevezerezh.scylla.advanced.integration.ServerContext
import io.drevezerezh.scylla.advanced.integration.player.PlayerApi
import io.drevezerezh.scylla.advanced.integration.player.PlayerApiBean
import io.drevezerezh.scylla.advanced.integration.player.PlayerCreation
import io.drevezerezh.scylla.advanced.scenario.dsl.*

class RestScenarioRunner(
    context: ServerContext
) {

    private val playerApi: PlayerApi = PlayerApiBean(context)
    private val scenarioParser = ScenarioParserFactory().create()

    init {
        scenarioParser.addListener(RestScenarioListener(playerApi))
    }

    fun run(scenario: String) {
        scenarioParser.parse(scenario)
    }


}

private class RestScenarioListener(
    private val playerApi: PlayerApi
) : ScenarioInstructionListener {

    /**
     * key = player name
     * value = player id
     */
    private val playerNames: MutableMap<String, String> = HashMap()

    override fun onCreatePlayerInstruction(line: Int, instruction: PlayerCreationInstruction) {
        val playerId = playerApi.create(PlayerCreation(instruction.playerName))
        playerNames[instruction.playerName] = playerId
    }

    override fun onShipDeploymentInstruction(line: Int, instruction: ShipDeploymentInstruction) {
        TODO("Not yet implemented")
    }

    override fun onShotInstruction(line: Int, instruction: ShotInstruction) {
        TODO("Not yet implemented")
    }
}