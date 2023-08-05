package io.gofannon.scylla.homework.domain.internal

import io.gofannon.scylla.homework.lang.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ShipImplTest {

    @Test
    fun `getHits() shall be empty when no initial hits`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW
        )

        assertThat(ship.hits)
            .isEmpty()
    }

    @Test
    fun `getHits() shall be filled when some initial hits`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW,
            setOf(Location(1, 0))
        )

        assertThat(ship.hits)
            .containsExactly(Location(1, 0))
    }

    @Test
    fun `getHits() shall be updated after successful hits`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW,
            setOf(Location(1, 0))
        )

        ship.hitAt(Location(0,0))

        assertThat(ship.hits)
            .containsExactly(Location(0,0), Location(1, 0))
    }

    @Test
    fun `getStatus shall return UNHARMED when no hits`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW
        )

        assertThat(ship.status)
            .isSameAs(ShipStructuralStatus.UNHARMED)
    }

    @Test
    fun `getStatus shall return DAMAGED when some hits`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW,
            setOf(Location(0,0), Location(1,0))
        )

        assertThat(ship.status)
            .isSameAs(ShipStructuralStatus.DAMAGED)
    }

    @Test
    fun `getStatus shall return DESTROYED when ship is fully hit`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW,
            setOf(Location(0,0), Location(1,0), Location(2,0))
        )

        assertThat(ship.status)
            .isSameAs(ShipStructuralStatus.DESTROYED)
    }

    @ParameterizedTest
    @CsvSource("0,0, true","1,0,true", "2,0,true", "3,0,false", "0,1,false")
    fun contains(x:Int, y:Int, contain : Boolean) {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW
        )

        assertThat( ship.contains(Location(x,y)))
            .isEqualTo(contain)
    }

    @Test
    fun `hitAt() shall return MISSED when shot is out of the ship`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW
        )
        assertThat( ship.hitAt(Location(5,5)) )
            .isSameAs(ShotResult.MISSED)
    }

    @Test
    fun `hitAt() shall return ALREADY_SHOT when shot is on a location already hit`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW,
            setOf( Location(0, 0))
        )
        assertThat( ship.hitAt(Location(0,0)) )
            .isSameAs(ShotResult.ALREADY_SHOT)
    }

    @Test
    fun `hitAt() shall return HIT when shot is on a new location of the ship`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW,
            setOf( Location(0, 0))
        )
        assertThat( ship.hitAt(Location(1,0)) )
            .isSameAs(ShotResult.HIT)
    }

    @Test
    fun `hitAt() shall return SUNK when shot is sunk the ship`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW,
            setOf( Location(0, 0), Location(2,0))
        )
        assertThat( ship.hitAt(Location(1,0)) )
            .isSameAs(ShotResult.SUNK)
    }

    @Test
    fun `intersects() shall return false when ships are not overriding`() {
        val ship1 = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW
        )

        val ship2 = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 1),
            GridOrientation.COLUMN
        )

        assertThat( ship1.intersects(ship2))
            .isFalse()
        assertThat( ship2.intersects(ship1))
            .isFalse()
    }

    @Test
    fun `intersects() shall return true when ships are overriding`() {
        val ship1 = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW
        )

        val ship2 = ShipImpl(
            ShipType.SUBMARINE,
            Location(1, 0),
            GridOrientation.COLUMN
        )

        assertThat( ship1.intersects(ship2))
            .isTrue()
        assertThat( ship2.intersects(ship1))
            .isTrue()
    }

    @Test
    fun `intersects() shall return true when same ship`() {
        val ship = ShipImpl(
            ShipType.SUBMARINE,
            Location(0, 0),
            GridOrientation.ROW
        )

        assertThat( ship.intersects(ship))
            .isTrue()
    }

}