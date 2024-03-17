package io.drevezerezh.scylla.advanced.domain.impl

import io.drevezerezh.scylla.advanced.domain.api.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PlayerManagerBean(
    private val idProvider: IdProvider,
    private val playerStore: PlayerStore
) : PlayerManager {

    override fun createPlayer(playerCreation: PlayerCreation): Player {
        PlayerValidation.checkValidity(playerCreation)
        val id = idProvider.createId()
        val player = Player(id, playerCreation.name)
        playerStore.save(player)
        return player
    }

    override fun containsPlayer(id: String): Boolean {
        return playerStore.contains(id)
    }

    override fun getPlayerById(id: String): Player {
        return playerStore.getById(id)
    }

    override fun getAllPlayers(): List<Player> {
        return playerStore.getAll()
    }

    override fun update(id: String, update: PlayerUpdate): Player {
        if( update.isEmpty())
            return playerStore.getById(id)

        PlayerValidation.checkValidity(id, update)

        val previousPlayer = playerStore.getById(id)
        if( previousPlayer.name == update.name)
            return previousPlayer

        val nextPlayer = Player(
            id,
            update.name ?: previousPlayer.name
        )

        playerStore.save(nextPlayer)
        return nextPlayer
    }

    override fun deletePlayer(id: String) : Boolean{
        LOGGER.info("deletePlayer($id)")
        return playerStore.deleteById(id)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PlayerManagerBean::class.java)
    }
}