package io.drevezerezh.scylla.advanced.domain.api

interface PlayerManager {

    /**
     * Create a new player
     * @param playerCreation the information for creating the player
     * @return the created player
     * @throws PlayerAlreadyExistException if the name of the player already exist
     * @throws InvalidPlayerAttributeException if the name attribute isinvalid
     */
    fun createPlayer(playerCreation: PlayerCreation): Player

    /**
     * Check if a player exists or not
     * @param id the player identifier
     * @return true if a player with such id exists, false otherwise
     */
    fun containsPlayer(id: String): Boolean

    /**
     * Get a player from its identifier
     * @param id the identifier of the player
     * @return the player
     * @throws PlayerNotFoundException when the id does not match an existing player
     */
    fun getPlayerById(id: String): Player

    /**
     * Get all the players
     * @return all the players
     */
    fun getAllPlayers(): List<Player>

    /**
     * Update a player
     * @param id the id of the player to update
     * @param update the
     * @return the
     * @throws PlayerNotFoundException when the player id does not match an existing player
     * @throws InvalidPlayerAttributeException when an attribute of the update structure is invalid
     */
    fun update(id: String, update: PlayerUpdate): Player

    /**
     * Delete a player by its identifier
     *
     * Do nothing when players does not exist.
     *
     * @param id the id of the player to delete
     * @return true when the player was existing, false otherwise
     */
    fun deletePlayer(id: String) : Boolean
}