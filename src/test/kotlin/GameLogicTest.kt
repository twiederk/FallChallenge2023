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
            )
        )

        // act
        val commands = GameLogic(GameData()).turn(turnData)

        // assert
        assertThat(commands).containsExactly("WAIT 0", "WAIT 0")
    }

}