package io.drevezerezh.scylla.advanced.scenario.dsl

/**
 * A listener of instructions during a scenario parsing
 */
interface ScenarioInstructionListener {

    /**
     * A player creation instruction has been parsed
     * @param line the line number of the instruction start
     * @param instruction the instruction
     */
    fun onCreatePlayerInstruction(line: Int, instruction: PlayerCreationInstruction)

    /**
     * A ship deployment instruction has been parsed
     * @param line the line number of the instruction start
     * @param instruction the instruction
     */
    fun onShipDeploymentInstruction(line: Int, instruction: ShipDeploymentInstruction)

    /**
     * A shot instruction has been parsed
     * @param line the line number of the instruction start
     * @param instruction the instruction
     */
    fun onShotInstruction(line: Int, instruction: ShotInstruction)
}
