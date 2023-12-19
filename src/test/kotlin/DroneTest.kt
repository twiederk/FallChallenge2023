import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.junit.jupiter.api.Test

class DroneTest {

    @Test
    fun should_find_nearestCreatureToScan_with_none_scanned_creatures() {
        // arrange
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
        val drone = Drone(droneId = 0, Point2D(3333, 500), 0, 30)

        // act
        val nearestVisibleCreature = drone.nearestCreatureToScan(visibleCreatures, listOf())

        // assert
        assertThat(nearestVisibleCreature).isEqualTo(visibleCreatures[1])
    }

    @Test
    fun should_find_nearestCreatureToScan_with_scanned_creatures() {
        // arrange
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
        val drone = Drone(droneId = 0, Point2D(3333, 500), 0, 30)

        // act
        val nearestVisibleCreature = drone.nearestCreatureToScan(visibleCreatures, listOf(9))

        // assert
        assertThat(nearestVisibleCreature).isEqualTo(visibleCreatures[0])
    }

    @Test
    fun should_check_when_visible_creatures_is_empty() {
        // arrange
        val drone = Drone(droneId = 0, Point2D(3333, 500), 0, 30)

        // act
        val exception = catchException { drone.nearestCreatureToScan(listOf(), listOf()) }

        // assert
        assertThat(exception).hasMessage("No visible creatures for drone 0")
    }

}