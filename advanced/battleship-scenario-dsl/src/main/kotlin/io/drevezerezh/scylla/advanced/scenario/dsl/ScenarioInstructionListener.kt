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
     * A start battle instruction has been parsed
     * @param line the line number of the instruction start
     * @param instruction the instruction
     */
    fun onStartBattleInstruction(line: Int, instruction: BattleStartingInstruction)

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
