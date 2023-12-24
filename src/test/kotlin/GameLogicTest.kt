import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameLogicTest {

    @Test
    fun should_display_both_commands_when_two_drones_are_owned() {
        // arrange
        val turnData = TurnData(
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
            "MOVE 2000 500 1 Moving to 2000 500",
            "MOVE 4000 500 1 Moving to 4000 500"
        )

    }

}