package io.drevezerezh.scylla.advanced.domain.api

interface PlayerStore {

    fun save(player: Player)

    fun saveAll(vararg players: Player)

    /**
     * Delete a player
     *
     * Does nothing when player does not exist
     *
     * @param playerId the identifier of the player
     * @return true if the player was existing, false otherwise
     */
    fun deleteById(playerId: String): Boolean

    fun deleteAll()

    fun contains(playerId: String): Boolean

    fun getById(playerId: String): Player

    /**
     * Get all players
     * @return the list of the players in the store
     */
    fun getAll(): List<Player>
}