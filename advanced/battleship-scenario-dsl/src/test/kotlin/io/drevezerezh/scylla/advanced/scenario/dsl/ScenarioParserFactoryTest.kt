package io.drevezerezh.scylla.advanced.scenario.dsl

import org.junit.jupiter.api.Test

class ScenarioParserFactoryTest {

    @Test
    fun `create shall return a parser`() {
        val factory = ScenarioParserFactory()
        factory.create()
    }
}