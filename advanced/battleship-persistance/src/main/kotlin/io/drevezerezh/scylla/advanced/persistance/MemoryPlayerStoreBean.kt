package io.drevezerezh.scylla.advanced.persistance

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.drevezerezh.scylla.advanced.domain.api.Player
import io.drevezerezh.scylla.advanced.domain.api.PlayerAlreadyExistException
import io.drevezerezh.scylla.advanced.domain.api.PlayerNotFoundException
import io.drevezerezh.scylla.advanced.domain.api.PlayerStore
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

@Component
class MemoryPlayerStoreBean : PlayerStore {

    private val readWriteLock = ReentrantReadWriteLock()
    private val playerMap = HashMap<String, PlayerPJson>()

    override fun save(player: Player) {
        val persistedPlayer = PlayerMapper.toPersistance(player)

        readWriteLock.read {
            val existingPlayer = playerMap.values.firstOrNull { it.name == player.name }
            if (existingPlayer != null)
                throw PlayerAlreadyExistException(existingPlayer.id, setOf("name"))
            readWriteLock.write {
                playerMap[persistedPlayer.id] = persistedPlayer
            }
        }
    }

    override fun saveAll(vararg players: Player) {
        val persistedPlayers = players.map(PlayerMapper::toPersistance)
        readWriteLock.write {
            persistedPlayers.forEach {
                playerMap[it.id] = it
            }
        }
    }

    override fun deleteById(playerId: String): Boolean {
        LOGGER.info("deleteById($playerId)")
        readWriteLock.write {
            LOGGER.info("deleteById($playerId) [write]")
            return playerMap.remove(playerId) != null
        }
    }

    override fun deleteAll() {
        LOGGER.info("deleteAll()")
        readWriteLock.write {
            LOGGER.info("deleteAll() [write]")
            playerMap.clear()
        }
    }

    override fun contains(playerId: String): Boolean {
        readWriteLock.read {
            return playerMap.contains(playerId)
        }
    }

    override fun getById(playerId: String): Player {
        val persistedPlayer = readWriteLock.read {
            playerMap[playerId]
        }

        if (persistedPlayer == null)
            throw PlayerNotFoundException(playerId)

        return PlayerMapper.toDomain(persistedPlayer)
    }


    override fun getAll(): List<Player> {
        val persistedPlayerList = readWriteLock.read {
            playerMap.values.toList()
        }
        return persistedPlayerList.map(PlayerMapper::toDomain)
    }


    fun exportToJson(): String {
        val persistedPlayerList = readWriteLock.read { playerMap.values.toList() }
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(persistedPlayerList)
    }


    fun importJson(content: String) {
        val objectMapper = ObjectMapper()
        val newPersistedPlayerList: List<PlayerPJson> = objectMapper.readValue(
            content,
            object : TypeReference<List<PlayerPJson>>() {}
        )

        readWriteLock.write {
            playerMap.clear()
            playerMap.putAll(newPersistedPlayerList.associateBy { it.id })
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MemoryPlayerStoreBean::class.java)
    }
}