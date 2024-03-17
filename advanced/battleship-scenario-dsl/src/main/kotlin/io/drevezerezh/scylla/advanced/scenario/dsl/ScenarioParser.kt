package io.drevezerezh.scylla.advanced.scenario.dsl

interface ScenarioParser {
    fun addListener(listener: ScenarioInstructionListener)
    fun removeListener(listener: ScenarioInstructionListener)
    fun parse(scenario: String)
}
