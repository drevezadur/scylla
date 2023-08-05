import org.junit.jupiter.api.Test

class MainKtTest {


    @Test
    fun `main shall accept to run a valid`() {
        main(arrayOf("src/test/resources/battle-test-2-full-battle.txt"))
    }
}