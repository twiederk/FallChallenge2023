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

    @Test
    fun should_return_creatures_on_screen() {
        // arrange
        val creatures = Creatures(
            creatures = mapOf(
                0 to Creature(0, 0, 0),
                1 to Creature(1, 1, 0),
                2 to Creature(2, 2, 0),
                3 to Creature(3, 3, 0),
                4 to Creature(4, 0, 1),
                5 to Creature(5, 1, 1),
                6 to Creature(6, 2, 1),
            )
        )
        val radarBlips = listOf(
            RadarBlip(0, 1),
            RadarBlip(0, 2),
            RadarBlip(0, 6),
        )

        // act
        val creaturesOnScreen = creatures.onScreenIdsWithoutMonsters(radarBlips)

        // assert
        assertThat(creaturesOnScreen).containsExactly(1, 2, 6)
    }

    @Test
    fun should_return_all_creatures_on_screen_of_one_type() {
        // arrange
        val creatures = Creatures(
            creatures = mapOf(
                0 to Creature(0, 0, 0),
                1 to Creature(1, 1, 0),
                2 to Creature(2, 2, 0),
                3 to Creature(3, 3, 0),
                4 to Creature(4, 0, 1),
                5 to Creature(5, 1, 1),
                6 to Creature(6, 2, 1),
            )
        )
        val radarBlips = listOf(
            RadarBlip(0, 1),
            RadarBlip(0, 2),
            RadarBlip(0, 6),
        )

        // act
        val creaturesOnScreenOfType = creatures.ofTypeOnScreen(0, radarBlips)

        // assert
        assertThat(creaturesOnScreenOfType).containsExactly(1, 2)

    }
}