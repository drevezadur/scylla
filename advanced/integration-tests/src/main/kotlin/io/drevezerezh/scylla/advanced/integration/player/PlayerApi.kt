package io.drevezerezh.scylla.advanced.integration.player

import io.drevezerezh.scylla.advanced.integration.restbase.RestItemApi

interface PlayerApi : RestItemApi<PlayerJson, PlayerCreation, PlayerUpdate>