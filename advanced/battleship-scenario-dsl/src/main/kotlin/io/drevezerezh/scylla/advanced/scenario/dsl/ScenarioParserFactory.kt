package io.drevezerezh.scylla.advanced.scenario.dsl

class ScenarioParserFactory {
    fun create(): ScenarioParser {
        return DefaultScenarioParser()
    }
}
