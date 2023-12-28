import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GameLogicTest {

    companion object {
        val creaturesScenario1 = listOf(
            Creature(creatureId = 4, type = 0),
            Creature(creatureId = 5, type = 0),
            Creature(creatureId = 6, type = 1),
            Creature(creatureId = 7, type = 1),
            Creature(creatureId = 8, type = 2),
            Creature(creatureId = 9, type = 2),
            Creature(creatureId = 10, type = 0),
            Creature(creatureId = 11, type = 0),
            Creature(creatureId = 12, type = 1),
            Creature(creatureId = 13, type = 1),
            Creature(creatureId = 14, type = 2),
            Creature(creatureId = 15, type = 2),
            Creature(creatureId = 16, type = -1),
            Creature(creatureId = 17, type = -1),
        )

        private val radarBlipsScenario1 = listOf(
            RadarBlip(droneId = 0, creatureId = 4, radar = "BL"),
            RadarBlip(droneId = 0, creatureId = 5, radar = "BR"),
            RadarBlip(droneId = 0, creatureId = 6, radar = "BR"),
            RadarBlip(droneId = 0, creatureId = 7, radar = "BR"),
            RadarBlip(droneId = 0, creatureId = 8, radar = "BR"),
            RadarBlip(droneId = 0, creatureId = 9, radar = "BL"),
            RadarBlip(droneId = 0, creatureId = 10, radar = "BR"),
            RadarBlip(droneId = 0, creatureId = 11, radar = "BR"),
            RadarBlip(droneId = 0, creatureId = 12, radar = "BR"),
            RadarBlip(droneId = 0, creatureId = 13, radar = "BR"),
            RadarBlip(droneId = 0, creatureId = 14, radar = "BR"),
            RadarBlip(droneId = 0, creatureId = 15, radar = "BR"),
            RadarBlip(droneId = 0, creatureId = 16, radar = "BL"),
            RadarBlip(droneId = 0, creatureId = 17, radar = "BR"),
            RadarBlip(droneId = 2, creatureId = 4, radar = "BL"),
            RadarBlip(droneId = 2, creatureId = 5, radar = "BR"),
            RadarBlip(droneId = 2, creatureId = 6, radar = "BL"),
            RadarBlip(droneId = 2, creatureId = 7, radar = "BR"),
            RadarBlip(droneId = 2, creatureId = 8, radar = "BR"),
            RadarBlip(droneId = 2, creatureId = 9, radar = "BL"),
            RadarBlip(droneId = 2, creatureId = 10, radar = "BL"),
            RadarBlip(droneId = 2, creatureId = 11, radar = "BR"),
            RadarBlip(droneId = 2, creatureId = 12, radar = "BL"),
            RadarBlip(droneId = 2, creatureId = 13, radar = "BR"),
            RadarBlip(droneId = 2, creatureId = 14, radar = "BL"),
            RadarBlip(droneId = 2, creatureId = 15, radar = "BR"),
            RadarBlip(droneId = 2, creatureId = 16, radar = "BL"),
            RadarBlip(droneId = 2, creatureId = 17, radar = "BR"),
        )

        private val creaturesScenario2 = listOf(
            Creature(creatureId = 4, type = 0),
            Creature(creatureId = 5, type = 0),
            Creature(creatureId = 6, type = 1),
            Creature(creatureId = 7, type = 1),
            Creature(creatureId = 8, type = 2),
            Creature(creatureId = 9, type = 2),
            Creature(creatureId = 10, type = 0),
            Creature(creatureId = 11, type = 0),
            Creature(creatureId = 12, type = 1),
            Creature(creatureId = 13, type = 1),
            Creature(creatureId = 14, type = 2),
            Creature(creatureId = 15, type = 2),
            Creature(creatureId = 16, type = -1),
            Creature(creatureId = 17, type = -1),
            Creature(creatureId = 18, type = -1),
            Creature(creatureId = 19, type = -1),
            Creature(creatureId = 20, type = -1),
            Creature(creatureId = 21, type = -1),
        )

        private val radarBlipsScenario2 = listOf(
            RadarBlip(droneId = 1, creatureId = 4, radar = "BR"),
            RadarBlip(droneId = 1, creatureId = 5, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 6, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 7, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 8, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 9, radar = "BR"),
            RadarBlip(droneId = 1, creatureId = 10, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 11, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 12, radar = "BR"),
            RadarBlip(droneId = 1, creatureId = 13, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 14, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 15, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 16, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 17, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 18, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 19, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 20, radar = "BL"),
            RadarBlip(droneId = 1, creatureId = 21, radar = "BL"),

            RadarBlip(droneId = 3, creatureId = 4, radar = "BR"),
            RadarBlip(droneId = 3, creatureId = 5, radar = "BL"),
            RadarBlip(droneId = 3, creatureId = 6, radar = "BR"),
            RadarBlip(droneId = 3, creatureId = 7, radar = "BL"),
            RadarBlip(droneId = 3, creatureId = 8, radar = "BL"),
            RadarBlip(droneId = 3, creatureId = 9, radar = "BR"),
            RadarBlip(droneId = 3, creatureId = 10, radar = "BL"),
            RadarBlip(droneId = 3, creatureId = 11, radar = "BR"),
            RadarBlip(droneId = 3, creatureId = 12, radar = "BR"),
            RadarBlip(droneId = 3, creatureId = 13, radar = "BL"),
            RadarBlip(droneId = 3, creatureId = 14, radar = "BL"),
            RadarBlip(droneId = 3, creatureId = 15, radar = "BR"),
            RadarBlip(droneId = 3, creatureId = 16, radar = "BR"),
            RadarBlip(droneId = 3, creatureId = 17, radar = "BR"),
            RadarBlip(droneId = 3, creatureId = 18, radar = "BL"),
            RadarBlip(droneId = 3, creatureId = 19, radar = "BR"),
            RadarBlip(droneId = 3, creatureId = 20, radar = "BL"),
            RadarBlip(droneId = 3, creatureId = 21, radar = "BR"),
        )
    }

    @Test
    fun should_display_both_commands_when_two_drones_are_owned() {
        // arrange
        val turnData = TurnData(
            turnNumber = 2,
            myDrones = listOf(
                Drone(droneId = 0, dronePosition = Point2D(2_000, 3_000)),
                Drone(droneId = 2, dronePosition = Point2D(4_000, 3_000)),
            ),
            radarBlips = listOf(
                RadarBlip()
            )
        )

        // act
        val commands = GameLogic(Creatures()).turn(turnData)

        // assert
        assertThat(commands).containsExactly(
            "MOVE 2000 500 0 [] SURFACE ALL SCANNED",
            "MOVE 4000 500 0 [] SURFACE ALL SCANNED"
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
        val turnData = TurnData(
            myDrones = listOf(
                Drone(droneId = 0, dronePosition = Point2D(2000, 500)),
                Drone(droneId = 2, dronePosition = Point2D(6000, 500)),
            ),
            radarBlips = radarBlipsScenario1
        )
        val creatures = Creatures(creatures = creaturesScenario1.associateBy { it.creatureId })
        val gameLogic = GameLogic(creatures)

        // act
        gameLogic.turn(turnData)

        // assert
        assertThat(gameLogic.turnNumber).isEqualTo(2)
    }



    @Test
    fun should_create_initial_scan_lists_scenario_1() {

        // arrange
        val turnData = TurnData(
            myDrones = listOf(
                Drone(droneId = 0, dronePosition = Point2D(2000, 500)),
                Drone(droneId = 2, dronePosition = Point2D(6000, 500)),
            ),
            radarBlips = radarBlipsScenario1
        )
        val creatures = Creatures(creatures = creaturesScenario1.associateBy { it.creatureId })

        // act
        val scanLists = GameLogic(creatures).initialScanLists(turnData, creatures)

        // assert
        assertThat(scanLists).hasSize(2)
        assertThat(scanLists[0]).containsOnly(4, 10, 6, 12, 9, 14)
        assertThat(scanLists[2]).containsOnly(11, 5, 13, 7, 15, 8)
    }

    @Test
    fun should_order_scan_list_by_type_scenario_1() {
        // arrange
        val creatures = Creatures(creatures = creaturesScenario1.associateBy { it.creatureId })

        // act
        val orderedScanList = GameLogic(creatures).orderScanList(listOf(4, 6, 9, 10, 12, 14), creatures)

        // assert
        assertThat(orderedScanList).containsExactly(4, 10, 6, 12, 9, 14)
    }

    @Test
    fun should_create_initial_scan_lists_scenario_2() {

        // arrange
        val turnData = TurnData(
            myDrones = listOf(
                Drone(droneId = 1, dronePosition = Point2D(4000, 500)),
                Drone(droneId = 3, dronePosition = Point2D(8000, 500)),
            ),
            radarBlips = radarBlipsScenario2
        )
        val creatures = Creatures(creatures = creaturesScenario2.associateBy { it.creatureId })

        // act
        val scanLists = GameLogic(creatures).createScanLists(turnData, creatures)

        // assert
        assertThat(scanLists).hasSize(2)
        assertThat(scanLists[1]).containsOnly(5, 10, 13, 7, 8, 14)
        assertThat(scanLists[3]).containsOnly(4, 11, 12, 6, 15, 9)
    }

    @Test
    fun should_order_scan_list_by_type_scenario_2() {
        // arrange
        val creatures = Creatures(creatures = creaturesScenario1.associateBy { it.creatureId })

        // act
        val orderedScanList = GameLogic(creatures).orderScanList(listOf(5, 7, 8, 10, 13, 14), creatures)

        // assert
        assertThat(orderedScanList).containsExactly(5, 10, 7, 13, 8, 14)
    }

}