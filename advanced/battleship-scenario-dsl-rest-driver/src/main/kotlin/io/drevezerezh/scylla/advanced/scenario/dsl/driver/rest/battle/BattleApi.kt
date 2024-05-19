package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle

import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.RestItemApi

interface BattleApi : RestItemApi<BattleJson, BattleCreationJson, BattleUpdateJson> {

    fun deployShip( battleId: String, player: BattlePlayer, shipDeployment : ShipDeploymentJson)

    fun shot(battleId: String, player: BattlePlayer, target: GridLocation): ShotReportJson
}