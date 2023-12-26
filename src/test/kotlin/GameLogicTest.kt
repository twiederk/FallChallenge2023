import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameLogicTest {

    @Test
    fun should_display_both_commands_when_two_drones_are_owned() {
        // arrange
        val turnData = TurnData(
            turnNumber = 2,
            myDrones = listOf(
                Drone(dronePosition = Point2D(2_000, 3_000)),
                Drone(dronePosition = Point2D(4_000, 3_000)),
            ),
            radarBlips = listOf(
                RadarBlip()
            )
        )

        // act
        val commands = GameLogic(Creatures()).turn(turnData)

        // assert
        assertThat(commands).containsExactly(
            "MOVE 2000 500 0 null SURFACE ALL SCANNED",
            "MOVE 4000 500 0 null SURFACE ALL SCANNED"
        )
    }

    @Test
    fun should_init_with_turn_number_1() {

        // act
        val gameLogic = GameLogic(Creatures())

        // assert
        assertThat(gameLogic.turnNumber).isEqualTo(1)
    }

    @Test
    fun should_increase_turn_number_by_1_every_turn() {
        // arrange
        val gameLogic = GameLogic(Creatures())

        // act
        gameLogic.turn(TurnData())

        // assert
        assertThat(gameLogic.turnNumber).isEqualTo(2)
    }
}