import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreaturesTest {

    @Test
    fun should_return_monsters() {
        // arrange
        val creatures = Creatures(
            creatures = mapOf(
                0 to Creature(0, 1, 1),
                1 to Creature(1, 1, 1),
                2 to Creature(2, 1, 1),
                3 to Creature(3, 1, 1),
                4 to Creature(4, -1, -1),
                5 to Creature(5, -1, -1),
            )
        )

        // act
        val monsters = creatures.monsterIds()

        // assert
        assertThat(monsters).contains(4, 5)
    }

}