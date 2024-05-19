package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle

import com.fasterxml.jackson.core.type.TypeReference
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.GridLocation
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.ServerContext
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.RestItemApiBean

class BattleApiBean(
    generalContext: ServerContext,
    private val processingListener: BattleRestItemProcessingListener = BattleRestItemProcessingListener(),
    private val context: ServerContext = generalContext.completePath("/battles")
) : BattleApi,
    RestItemApiBean<BattleJson, BattleCreationJson, BattleUpdateJson>(
        context,
        object : TypeReference<BattleJson>() {},
        object : TypeReference<List<BattleJson>>() {},
        processingListener,
        context.restEngine
    ) {

    override fun update(itemId: String, itemUpdate: BattleUpdateJson): BattleJson {
        throw UnsupportedOperationException("Battle cannot be updated")
    }

    override fun deployShip(battleId: String, player: BattlePlayer, shipDeployment: ShipDeploymentJson) {
        val playerUriFragment = player.name.lowercase()
        val uri = "/battles/$battleId/fleets/$playerUriFragment/ships"
        val response = context.restEngine.postJson(uri, shipDeployment, false)
        when (response.code) {
            201 -> processingListener.onShipDeploymentSuccess(battleId, player, shipDeployment.shipType)
            else -> processingListener.onShipDeploymentFailure(response.code, battleId, player, shipDeployment.shipType)
        }
    }


    override fun shot(battleId: String, player: BattlePlayer, target: GridLocation): ShotReportJson {
        val playerUriFragment = player.name.lowercase()
        val uri = "/battles/$battleId/fleets/$playerUriFragment/shot"
        val shot = ShotJson(target = target)
        context.restEngine.postJson(uri, shot, true).apply {
            return when (code) {
                200 -> processingListener.onShotSuccess(battleId, player, body)
                else -> processingListener.onShotFailure(battleId, player, code)
            }
        }
    }
}