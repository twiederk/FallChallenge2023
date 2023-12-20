import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameLogicTest {

    @Test
    fun should_move_to_nearest_creature() {
        // arrange
        val gameData = GameData(
            creatureCount = 0,
            creatures = listOf()
        )
        val turnData = TurnData(
            myScore = 0,
            foeScore = 0,
            myScannedCreatures = listOf(9),
            foeScannedCreatures = listOf(),
            myDrones = listOf(Drone(0, Point2D(3333, 500), 0, 30)),
            foeDrones = listOf(),
            visibleCreatures = listOf(
                VisibleCreature(
                    id = 0,
                    creatureId = 3,
                    creaturePosition = Point2D(1195, 3926),
                    creatureVelocity = Point2D(0, -200)
                ),
                VisibleCreature(
                    id = 1,
                    creatureId = 9,
                    creaturePosition = Point2D(2691, 3920),
                    creatureVelocity = Point2D(0, -200)
                ),
                VisibleCreature(
                    id = 2,
                    creatureId = 8,
                    creaturePosition = Point2D(7308, 3920),
                    creatureVelocity = Point2D(0, -200)
                ),
                VisibleCreature(
                    id = 3,
                    creatureId = 3,
                    creaturePosition = Point2D(8804, 3926),
                    creatureVelocity = Point2D(0, -200)
                ),
            )
        )

        // act
        val command = GameLogic(gameData).turn(turnData)

        // assert
        assertThat(command).isEqualTo("MOVE 1195 3926 0")
    }

    @Test
    fun should_wait_when_no_fish_is_visible() {
        // arrange
        val gameData = GameData(
            creatureCount = 0,
            creatures = listOf()
        )
        val turnData = TurnData(
            myScore = 0,
            foeScore = 0,
            myScannedCreatures = listOf(9),
            foeScannedCreatures = listOf(),
            myDrones = listOf(Drone(0, Point2D(3333, 500), 0, 30)),
            foeDrones = listOf(),
            visibleCreatures = listOf()
        )

        // act
        val command = GameLogic(gameData).turn(turnData)

        // assert
        assertThat(command).isEqualTo("WAIT 0")
    }



}