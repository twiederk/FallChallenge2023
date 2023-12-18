import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DroneTest {

    @Test
    fun should_find_nearestVisibleCreature() {
        // arrange
        val drone = Drone(droneId = 0, Point2D(3333, 500), 0, 30)
        val visibleCreatures = listOf(
            VisibleCreature(
                creatureId = 3,
                creaturePosition = Point2D(1195, 3926),
                creatureVelocity = Point2D(0, -200)
            ),
            VisibleCreature(
                creatureId = 9,
                creaturePosition = Point2D(2691, 3920),
                creatureVelocity = Point2D(0, -200)
            ),
            VisibleCreature(
                creatureId = 8,
                creaturePosition = Point2D(7308, 3920),
                creatureVelocity = Point2D(0, -200)
            ),
            VisibleCreature(
                creatureId = 3,
                creaturePosition = Point2D(8804, 3926),
                creatureVelocity = Point2D(0, -200)
            ),
        )

        // act
        val nearestVisibleCreature = drone.nearestVisibleCreature(visibleCreatures)

        // assert
        assertThat(nearestVisibleCreature).isEqualTo(visibleCreatures[1])
    }

}