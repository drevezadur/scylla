package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.player

import com.fasterxml.jackson.core.type.TypeReference
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.ServerContext
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.RestItemApiBean

class PlayerApiBean(
    context: ServerContext
) : PlayerApi,
    RestItemApiBean<PlayerJson, PlayerCreationJson, PlayerUpdateJson>(
        context.completePath("/players"),
        object : TypeReference<PlayerJson>() {},
        object : TypeReference<List<PlayerJson>>() {},
        PlayerRestItemProcessingListener(),
        context.restEngine
    )