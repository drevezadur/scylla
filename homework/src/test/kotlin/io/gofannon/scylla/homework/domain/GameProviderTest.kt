package io.gofannon.scylla.homework.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameProviderTest {

    @Test
    fun `getGame shall always return the same instance`() {
        val game1 = GameProvider.getGame()
        val game2 = GameProvider.getGame()

        assertThat( game1).isSameAs(game2)
    }

    @Test
    fun `resetGame shall return a new instance`() {
        val game1 = GameProvider.getGame()
        val game2 = GameProvider.resetGame()
        val game3 = GameProvider.getGame()

        assertThat( game1).isNotSameAs(game2)
        assertThat( game2).isSameAs(game3)
    }
}