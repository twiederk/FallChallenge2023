import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DroneTest {

    @Test
    fun should_find_nearestCreatureToScan() {
        // arrange
        val drone = Drone(droneId = 0, Point2D(3333, 500), 0, 30)
        val visibleCreatures = listOf(
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

        // act
        val nearestVisibleCreature = drone.nearestCreatureToScan(visibleCreatures, listOf<Int>())

        // assert
        assertThat(nearestVisibleCreature).isEqualTo(visibleCreatures[1])
    }

}