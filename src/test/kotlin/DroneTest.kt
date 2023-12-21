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

    @Test
    fun should_search_when_initialized() {

        // act
        val drone = Drone()

        // assert
        assertThat(drone.state).isEqualTo(Drone.State.SEARCH)
    }

    @Test
    fun should_surface_when_drone_scanned() {
        // arrange
        val drone = Drone(dronePosition = Point2D(2_000, 3_000))
        val turnData = TurnData(
            dronesScans = listOf(Pair(0, 10))
        )

        // act
        val command = drone.turn(turnData, mapOf())

        // assert
        assertThat(drone.state).isEqualTo(Drone.State.SURFACE)
        assertThat(command).isEqualTo("MOVE 2000 500 0")
    }

    @Test
    fun should_continue_to_surface_when_surface_not_reached() {
        // arrange
        val drone = Drone(dronePosition = Point2D(2_000, 3_000))
        drone.state = Drone.State.SURFACE

        // act
        val command = drone.turn(TurnData(), mapOf())

        // assert
        assertThat(drone.state).isEqualTo(Drone.State.SURFACE)
        assertThat(command).isEqualTo("MOVE 2000 500 0")
    }


    @Test
    fun should_search_when_surface_is_reached() {
        // arrange
        val drone = Drone(dronePosition = Point2D(2_000, 500))
        drone.state = Drone.State.SURFACE

        // act
        val command = drone.turn(TurnData(), mapOf())

        // assert
        assertThat(drone.state).isEqualTo(Drone.State.SEARCH)
        assertThat(command).isEqualTo("WAIT 0")
    }

    @Test
    fun should_turn_on_power_light_when_in_habitat_zone() {
        // arrange
        val drone = Drone(dronePosition = Point2D(0, 3_000))

        // act
        val command = drone.light()

        // assert
        assertThat(command).isEqualTo(1)
    }

    @Test
    fun should_turn_off_power_light_when_not_in_habitat_zone() {
        // arrange
        val drone = Drone(dronePosition = Point2D(0, 2_000))

        // act
        val light = drone.light()

        // assert
        assertThat(light).isEqualTo(0)
    }

    @Test
    fun should_search_first_type_0_when_not_all_type_0_scanned() {
        // arrange
        val drone = Drone(dronePosition = Point2D(3_000, 2_000))
        val turnData = TurnData(
            radarBlips = listOf(
                RadarBlip(droneId = 0, creatureId = 0, radar = "TL")
            ),
        )
        val creatures = mapOf(
            0 to Creature(creatureId = 0, color = 0, type = 0)
        )

        // act
        val direction = drone.searchDirection(turnData, creatures)

        // assert
        assertThat(direction).isEqualTo(Point2D(2_500, 1_500))
    }

    @Test
    fun should_find_creature_with_lowest_type_of_not_scanned_creatures() {
        // arrange
        val drone = Drone(dronePosition = Point2D(2_000, 3_000))
        val creatures = mapOf(
            0 to Creature(creatureId = 0, color = 0, type = 2),
            1 to Creature(creatureId = 1, color = 0, type = 1),
            2 to Creature(creatureId = 2, color = 1, type = 1),
            3 to Creature(creatureId = 3, color = 0, type = 0),
        )
        val myScannedCreatures = listOf<Int>()

        // act
        val creature = drone.nextCreatureToScan(creatures, myScannedCreatures)

        // assert
        assertThat(creature).isEqualTo(creatures[3])
    }

}