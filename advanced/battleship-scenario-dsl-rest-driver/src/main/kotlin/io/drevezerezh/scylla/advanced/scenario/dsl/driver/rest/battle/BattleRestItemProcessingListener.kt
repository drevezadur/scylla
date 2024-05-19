package io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.battle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.drevezerezh.scylla.advanced.lang.BattlePlayer
import io.drevezerezh.scylla.advanced.lang.ShipType
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.RequestFailureException
import io.drevezerezh.scylla.advanced.scenario.dsl.driver.rest.restbase.RestItemProcessingListener
import org.slf4j.LoggerFactory

class BattleRestItemProcessingListener : RestItemProcessingListener {

    override fun onCreationSuccess(httpCode: Int, requestBody: String, itemId: String) {
        LOGGER.info("POST /battles $requestBody : create battle")
    }

    override fun onCreationFailure(httpCode: Int, requestBody: String, ex: RuntimeException?): Nothing {
        LOGGER.error("POST /battles $requestBody : fail to create battle")
        throw RequestFailureException(httpCode, "Failed to create battle", ex)
    }

    override fun onGetSuccess(httpCode: Int, itemId: String) {
        LOGGER.info("GET /battles/$itemId  : battle found")
    }

    override fun onGetFailure(httpCode: Int, itemId: String, ex: RuntimeException?): Nothing {
        LOGGER.error("GET /battles/$itemId : player not found")
        throw BattleNotFoundException(itemId, httpCode, "Cannot found battle with id '$itemId'")
    }

    override fun onGetAllSuccess(httpCode: Int) {
        LOGGER.info("GET /battles : success")
    }

    override fun onGetAllFailure(httpCode: Int, ex: RuntimeException?): Nothing {
        LOGGER.error("GET /battles : failure, code=$httpCode")
        throw RequestFailureException(httpCode, "Fail to get all battles", ex)
    }

    override fun onUpdateSuccess(httpCode: Int, itemId: String, requestBody: String) {
        LOGGER.info("PUT /battles/$itemId $requestBody : battle updated")
    }

    override fun onUpdateFailure(httpCode: Int, itemId: String, requestBody: String, ex: RuntimeException?): Nothing {
        LOGGER.warn("PUT /battles/$httpCode $requestBody : fail to update battle")
        throw BattleUpdateFailureException(itemId, httpCode, "Cannot update battle with id '$itemId'")
    }

    override fun onDeleteSuccess(httpCode: Int, itemId: String) {
        LOGGER.info("DELETE /battles/$itemId : battle deleted")
    }

    override fun onDeleteFailure(httpCode: Int, itemId: String, ex: RuntimeException?): Nothing {
        LOGGER.warn("DELETE /battles/$itemId : code=$httpCode : fail to delete battle")
        throw BattleDeletionFailureException(itemId, httpCode, "Fail to delete battle with id '$itemId'")
    }

    fun onShipDeploymentSuccess(battleId: String, player: BattlePlayer, shipType: ShipType) {
        val playerUriFragment = player.name.lowercase()
        LOGGER.info("POST /battles/$battleId/fleet/$playerUriFragment : ship $shipType deployed")
    }

    fun onShipDeploymentFailure(
        httpCode: Int,
        battleId: String,
        player: BattlePlayer,
        shipType: ShipType
    ): Nothing {
        val playerUriFragment = player.name.lowercase()
        LOGGER.info("POST /battles/$battleId/fleet/$playerUriFragment : fail to deploy ship $shipType")
        throw ShipDeploymentFailureException(
            battleId,
            player,
            shipType,
            httpCode,
            "Fail to deploy ship $shipType of player $player in battle $battleId"
        )
    }


    fun onShotSuccess(battleId: String, player: BattlePlayer, body: String): ShotReportJson {
        val report = OBJECT_MAPPER.readValue(body, ShotReportJson::class.java)
        LOGGER.info("POST /battles/$battleId/fleets/${player.name.lowercase()}/shot . Result=${report.result} Victory=${report.victorious}")
        return report
    }

    fun onShotFailure(battleId: String, player: BattlePlayer, httpCode: Int): Nothing {
        val playerUriFragment = player.name.lowercase()
        LOGGER.info("POST /battles/$battleId/fleet/$playerUriFragment/shot : fail to shot")
        throw ShotFailureException(
            battleId,
            player,
            httpCode,
            "Fail to shot with shooter $player in battle $battleId"
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(BattleRestItemProcessingListener::class.java)

        private val OBJECT_MAPPER = ObjectMapper().registerModules(JavaTimeModule(), KotlinModule.Builder().build())

    }
}