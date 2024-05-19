package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle

import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.ShipType
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.RequestFailureException

class ShipDeploymentFailureException(
    val battleId: String,
    val player: BattlePlayer,
    val shipType: ShipType,
    override val code: Int,
    override val message: String? = null,
    override val cause: Throwable? = null
) : RequestFailureException(code, message, cause)