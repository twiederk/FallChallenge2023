import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DroneTest {

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
        val creatures = Creatures(
            creatures = mapOf(
                0 to Creature(creatureId = 0, color = 0, type = 0)
            )
        )

        // act
        val direction = drone.searchDirection(turnData, creatures)

        // assert
        assertThat(direction).isEqualTo(Point2D(2_500, 1_500))
    }

    @Test
    fun should_search_only_creatures_one_screen() {
        // arrange
        val drone = Drone(dronePosition = Point2D(3_000, 2_000))
        val turnData = TurnData(
            radarBlips = listOf(
                RadarBlip(droneId = 0, creatureId = 1, radar = "TL")
            ),
        )
        val creatures = Creatures(
            creatures = mapOf(
                0 to Creature(creatureId = 0, color = 0, type = 0),
                1 to Creature(creatureId = 1, color = 0, type = 1)
            )
        )

        // act
        val direction = drone.searchDirection(turnData, creatures)

        // assert
        assertThat(direction).isEqualTo(Point2D(2_500, 1_500))

    }

    @Test
    fun should_find_creature_with_lowest_type_of_not_scanned_creatures_on_screen() {
        // arrange
        val drone = Drone(droneId = 0, dronePosition = Point2D(2_000, 3_000))
        val creatures = Creatures(
            creatures = mapOf(
                0 to Creature(creatureId = 0, color = 0, type = 2),
                1 to Creature(creatureId = 1, color = 0, type = 1), // scanned by friendly drone
                2 to Creature(creatureId = 2, color = 1, type = 1), // scanned by enemy drone
                3 to Creature(creatureId = 3, color = 0, type = 0), // left screen
                4 to Creature(creatureId = 4, color = -1, type = -1), // monster
            )
        )
        val turnData = TurnData(
            myDrones = listOf(
                drone,
                Drone(droneId = 1, dronePosition = Point2D(2_000, 3_000))
            ),
            myScannedCreatures = listOf(),
            radarBlips = listOf(
                RadarBlip(0, 0, "TL"),
                RadarBlip(0, 1, "TL"),
                RadarBlip(0, 2, "TL"),
                RadarBlip(0, 4, "TL"),
            ),
            dronesScans = listOf(
                DroneScan(1, 1), // friendly drone
                DroneScan(2, 2), // enemy drone
            )
        )

        // act
        val creature = drone.nextCreatureToScan(turnData, creatures)

        // assert
        assertThat(creature).isEqualTo(creatures.creature(2))
    }

    @Test
    fun should_return_false_when_not_all_creatures_scanned() {
        // arrange
        val turnData = TurnData(
            myDrones = listOf(
                Drone(droneId = 0),
                Drone(droneId = 1),
            ),
            myScannedCreatures = listOf(1, 2),
            dronesScans = listOf(
                DroneScan(droneId = 1, creatureId = 3), // friendly drone
                DroneScan(droneId = 2, creatureId = 3), // enemy drone
            ),
            radarBlips = listOf(
                RadarBlip(0, 1),
                RadarBlip(0, 2),
                RadarBlip(0, 3),
                RadarBlip(0, 4),
                RadarBlip(1, 1),
                RadarBlip(1, 2),
                RadarBlip(1, 3),
                RadarBlip(1, 4),
                RadarBlip(2, 1),
                RadarBlip(2, 2),
                RadarBlip(2, 3),
                RadarBlip(2, 4),
            ),
        )

        // act
        val result = Drone().isAllCreaturesScanned(turnData)

        // assert
        assertThat(result).isFalse()
    }

    @Test
    fun should_return_true_when_all_creatures_scanned() {
        // arrange
        val turnData = TurnData()

        // act
        val result = Drone().isAllCreaturesScanned(turnData)

        // assert
        assertThat(result).isTrue()
    }

    @Test
    fun should_return_true_when_more_creatures_scanned_than_on_screen() {
        // arrange
        val turnData = TurnData(
            myDrones = listOf(
                Drone(droneId = 0),
                Drone(droneId = 1),
            ),
            dronesScans = listOf(
                DroneScan(droneId = 0, creatureId = 1), // friend drone
                DroneScan(droneId = 0, creatureId = 2), // friend drone
                DroneScan(droneId = 1, creatureId = 3), // friend drone
                DroneScan(droneId = 2, creatureId = 3), // enemy drone
            ),
            radarBlips = listOf(
                RadarBlip(0, 1),
                RadarBlip(0, 2),
                RadarBlip(1, 1),
                RadarBlip(1, 2),
                RadarBlip(2, 1),
                RadarBlip(2, 2),
            ),
        )

        // act
        val result = Drone().isAllCreaturesScanned(turnData)

        // assert
        assertThat(result).isTrue()
    }

    @Test
    fun should_avoid_monster_when_monster_crosses_the_way() {
        // arrange
        val drone = Drone(dronePosition = Point2D(1_000, 2_000))
        val direction = Point2D(5_000, 2_000)
        val monsters = listOf(
            VisibleCreature(creaturePosition = Point2D(1200, 1800), creatureVelocity = Point2D(200, 0))
        )

        // act
        val command = drone.avoidMonster(direction, monsters)

        // assert
        assertThat(command).isEqualTo("MOVE 1000 1400 0 The monster is hunting me")
    }


}