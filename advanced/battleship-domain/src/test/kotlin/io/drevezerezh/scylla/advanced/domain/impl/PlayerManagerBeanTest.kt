package io.drevezerezh.scylla.advanced.domain.impl

import io.drevezerezh.scylla.advanced.domain.api.*
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PlayerManagerBeanTest {

    @MockK
    private lateinit var idProvider: IdProvider

    @MockK
    private lateinit var playerStore: PlayerStore

    private lateinit var playerManagerBean: PlayerManagerBean

    @BeforeEach
    fun setUp() {
        playerManagerBean = PlayerManagerBean(idProvider, playerStore)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `createPlayer shall fail when name is too short`() {
        assertThatThrownBy {
            playerManagerBean.createPlayer(PlayerCreation("a"))
        }.isInstanceOf(InvalidPlayerAttributeException::class.java)
            .extracting("playerId", "attributeNames")
            .contains("in-creation", setOf("name"))

        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `createPlayer shall fail when name is too long`() {
        assertThatThrownBy {
            playerManagerBean.createPlayer(PlayerCreation(TOO_LONG_NAME))
        }.isInstanceOf(InvalidPlayerAttributeException::class.java)
            .extracting("playerId", "attributeNames")
            .contains("in-creation", setOf("name"))

        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `createPlayer shall fail when name already exists`() {
        every { idProvider.createId() } returns JOHN.id
        every { playerStore.save(JOHN) } throws PlayerAlreadyExistException(JOHN.id, setOf("name"))

        assertThatThrownBy {
            playerManagerBean.createPlayer(PlayerCreation(JOHN.name))
        }.isInstanceOf(PlayerAlreadyExistException::class.java)
            .extracting("playerId", "attributeNames")
            .contains(JOHN.id, setOf("name"))

        verify(exactly = 1) { idProvider.createId() }
        verify(exactly = 1) { playerStore.save(JOHN) }

        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `createPlayer shall create a player when it does not already exist`() {
        every { idProvider.createId() } returns JOHN.id
        every { playerStore.save(JOHN) } answers { Any() }


        assertThat(playerManagerBean.createPlayer(PlayerCreation(JOHN.name)))
            .isEqualTo(JOHN)


        verify(exactly = 1) { idProvider.createId() }
        verify(exactly = 1) { playerStore.save(JOHN) }

        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `containsPlayer shall return true when player is in store`() {
        every { playerStore.contains(JOHN.id) } returns true


        assertThat(playerManagerBean.containsPlayer(JOHN.id))
            .isTrue()


        verify(exactly = 1) { playerStore.contains(JOHN.id) }

        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `containsPlayer shall return false when player is not in store`() {
        every { playerStore.contains(JOHN.id) } returns false


        assertThat(playerManagerBean.containsPlayer(JOHN.id))
            .isFalse()


        verify(exactly = 1) { playerStore.contains(JOHN.id) }

        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }


    @Test
    fun `getPlayerById shall fail when player not in store`() {
        every { playerStore.getById(JOHN.id) } throws PlayerNotFoundException(JOHN.id)

        assertThatThrownBy {
            playerManagerBean.getPlayerById(JOHN.id)
        }.isInstanceOf(PlayerNotFoundException::class.java)
            .extracting("playerId")
            .isEqualTo(JOHN.id)

        verify(exactly = 1) { playerStore.getById(JOHN.id) }
        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `getPlayerById shall return the player when it exists in the store`() {
        every { playerStore.getById(JOHN.id) } returns JOHN

        assertThat(playerManagerBean.getPlayerById(JOHN.id))
            .isEqualTo(JOHN)

        verify(exactly = 1) { playerStore.getById(JOHN.id) }
        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }


    @Test
    fun `getAllPlayers shall return all players in store`() {
        every { playerStore.getAll() } returns listOf(JOHN, JANE)

        assertThat(playerManagerBean.getAllPlayers())
            .containsOnly(JOHN, JANE)

        verify(exactly = 1) { playerStore.getAll() }
        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `update shall fail when name is too short`() {
        assertThatThrownBy {
            playerManagerBean.update(
                JOHN.id, PlayerUpdate(
                    name = "a"
                )
            )
        }.isInstanceOf(InvalidPlayerAttributeException::class.java)
            .extracting("playerId", "attributeNames")
            .containsOnly(JOHN.id, setOf("name"))

        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `update shall fail when name is too long`() {
        assertThatThrownBy {
            playerManagerBean.update(JOHN.id, PlayerUpdate(TOO_LONG_NAME))
        }.isInstanceOf(InvalidPlayerAttributeException::class.java)
            .extracting("playerId", "attributeNames")
            .containsOnly(JOHN.id, setOf("name"))

        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `update shall fail when name already exist in another record`() {
        val expectedPlayer = Player(JOHN.id, "Jane")
        every { playerStore.getById(JOHN.id) } returns JOHN
        every { playerStore.save(expectedPlayer) } throws PlayerAlreadyExistException(JOHN.id, setOf("name"))

        assertThatThrownBy {
            playerManagerBean.update(JOHN.id, PlayerUpdate("Jane"))
        }.isInstanceOf(PlayerAlreadyExistException::class.java)
            .extracting("playerId", "attributeNames")
            .containsOnly(JOHN.id, setOf("name"))

        verify(exactly = 1) { playerStore.getById(JOHN.id) }
        verify(exactly = 1) { playerStore.save(expectedPlayer) }
        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `update shall do nothing when name is the same`() {
        every { playerStore.getById(JOHN.id) } returns JOHN

        playerManagerBean.update(JOHN.id, PlayerUpdate(JOHN.name))

        verify(exactly = 1) { playerStore.getById(JOHN.id) }
        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    @Test
    fun `update shall do nothing when name is null`() {
        every { playerStore.getById(JOHN.id) } returns JOHN

        playerManagerBean.update(JOHN.id, PlayerUpdate())

        verify(exactly = 1) { playerStore.getById(JOHN.id) }
        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }


    @Test
    fun `update shall change the name when it is valid`() {
        val expectedPlayer = Player(JOHN.id, "Walter")
        every { playerStore.getById(JOHN.id) } returns JOHN
        every { playerStore.save(expectedPlayer) } answers { Any() }

        assertThat(playerManagerBean.update(JOHN.id, PlayerUpdate(expectedPlayer.name)))
            .isEqualTo(expectedPlayer)

        verify(exactly = 1) { playerStore.getById(JOHN.id) }
        verify(exactly = 1) { playerStore.save(expectedPlayer) }
        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }


    @Test
    fun `deletePlayer shall do nothing when player is missing`() {
        every { playerStore.deleteById(JOHN.id) } returns false

        playerManagerBean.deletePlayer(JOHN.id)

        verify(exactly = 1) { playerStore.deleteById(JOHN.id) }
        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }


    @Test
    fun `deletePlayer shall delete the player when it exists`() {
        every { playerStore.deleteById(JOHN.id) } returns true

        playerManagerBean.deletePlayer(JOHN.id)

        verify(exactly = 1) { playerStore.deleteById(JOHN.id) }
        confirmVerified(playerStore)
        confirmVerified(idProvider)
    }

    companion object {
        private const val TOO_LONG_NAME = "This is a very too long name"

        private val JOHN = Player("id-01", "John")
        private val JANE = Player("id-02", "Jane")
    }
}