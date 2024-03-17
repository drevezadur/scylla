package io.drevezerezh.scylla.advanced.integration.player

import com.fasterxml.jackson.core.type.TypeReference
import io.drevezerezh.scylla.advanced.integration.restbase.RestItemApiBean
import io.drevezerezh.scylla.advanced.integration.ServerContext

class PlayerApiBean(
    context: ServerContext
) : PlayerApi,
    RestItemApiBean<PlayerJson, PlayerCreation, PlayerUpdate>(
        context.completePath("/players"),
        object : TypeReference<PlayerJson>() {},
        object : TypeReference<List<PlayerJson>>() {},
        PlayerRestItemProcessingListener()
    )