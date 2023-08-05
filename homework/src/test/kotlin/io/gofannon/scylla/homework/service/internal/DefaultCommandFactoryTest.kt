package io.gofannon.scylla.homework.service.internal

import io.gofannon.scylla.homework.domain.Game
import io.gofannon.scylla.homework.domain.PlayerBoard
import io.gofannon.scylla.homework.lang.*
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.regex.Pattern

@ExtendWith(MockKExtension::class)
class DefaultCommandFactoryTest {

    private val deployCommandPattern =
        Pattern.compile("([AB]) deploy (CARRIER|BATTLESHIP|CRUISER|SUBMARINE|DESTROYER) ([0-9]),([0-9]) (ROW|COLUMN)")
    private val fireCommandPattern = Pattern.compile("([AB]) fire ([0-9]),([0-9])")

    @MockK
    private lateinit var game: Game

    @MockK
    private lateinit var playerBoard: PlayerBoard

    @Test
    fun deployCommandPatternTest() {
        val matcher = deployCommandPattern.matcher("B deploy SUBMARINE 3,0 COLUMN")
        assertThat(matcher.find())
            .isTrue()

        assertThat(matcher.group(1)).isEqualTo("B")
        assertThat(matcher.group(2)).isEqualTo("SUBMARINE")
        assertThat(matcher.group(3)).isEqualTo("3")
        assertThat(matcher.group(4)).isEqualTo("0")
        assertThat(matcher.group(5)).isEqualTo("COLUMN")
    }

    @Test
    fun fireCommandPatternTest() {
        val matcher = fireCommandPattern.matcher("A fire 2,0")

        assertThat(matcher.find())
            .isTrue()

        assertThat(matcher.group(1)).isEqualTo("A")
        assertThat(matcher.group(2)).isEqualTo("2")
        assertThat(matcher.group(3)).isEqualTo("0")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "    ", "\t   \t  "])
    fun `createCommand shall handle blank lines`(instruction: String) {
        val commandFactory = DefaultCommandFactory()
        val command = commandFactory.createCommand(instruction)

        assertThat(command.toInstruction())
            .isEmpty()

        command.execute(game)

        confirmVerified(game)
    }


    @ParameterizedTest
    @CsvSource("#,#", "    # This is a comment,# This is a comment", "  \t# Another comment,# Another comment")
    fun `createCommand shall handle comment instruction`(instruction: String, expectedInstruction: String) {
        val commandFactory = DefaultCommandFactory()
        val command = commandFactory.createCommand(instruction)

        assertThat(command.toInstruction())
            .isEqualTo(expectedInstruction)

        command.execute(game)

        confirmVerified(game)
    }

    @Test
    fun `createCommand shall handle deploy instruction`() {
        every { game.getPlayerBoard(Player.PLAYER_B) } returns playerBoard
        every { playerBoard.deployShip(ShipType.SUBMARINE, Location(4, 1), GridOrientation.ROW) } returns Unit

        val commandFactory = DefaultCommandFactory()

        val instruction = "B deploy SUBMARINE 4,1 ROW"
        val command = commandFactory.createCommand(instruction)

        assertThat(command.toInstruction())
            .isEqualTo(instruction)

        command.execute(game)

        verify { game.getPlayerBoard(Player.PLAYER_B) }
        verify { playerBoard.deployShip(ShipType.SUBMARINE, Location(4, 1), GridOrientation.ROW) }
    }

    @Test
    fun `createCommand shall handle fire instruction`() {
        every { game.getPlayerBoard(Player.PLAYER_A) } returns playerBoard
        every { playerBoard.fireAt(Location(4, 1)) } returns ShotResult.HIT

        val commandFactory = DefaultCommandFactory()

        val instruction = "A fire 4,1"
        val command = commandFactory.createCommand(instruction)

        assertThat(command.toInstruction())
            .isEqualTo(instruction)

        command.execute(game)

        verify { game.getPlayerBoard(Player.PLAYER_A) }
        verify { playerBoard.fireAt(Location(4, 1)) }
    }
}