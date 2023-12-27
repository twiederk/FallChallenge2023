import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class DroneTest {

    private val allCreatures = mapOf(
        0 to Creature(0, 0, 0),
        1 to Creature(1, 1, 0),
        2 to Creature(2, 2, 0),
        3 to Creature(3, 3, 0),
        4 to Creature(4, 0, 1),
        5 to Creature(5, 1, 1),
        6 to Creature(6, 2, 1),
        7 to Creature(7, 3, 1),
        8 to Creature(8, 0, 2),
        9 to Creature(9, 1, 2),
        10 to Creature(10, 2, 2),
        11 to Creature(11, 3, 2),
    )

    @Test
    fun should_scan_next_creature() {
        // arrange
        val drone = Drone(droneId = 0, dronePosition = Point2D(2_000, 3_000))
        val turnData = TurnData(
            turnNumber = 2,
            myDrones = listOf(
                drone,
                Drone(droneId = 1, dronePosition = Point2D(4_000, 6_000))
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
        val creatures = Creatures(
            creatures = mapOf(
                0 to Creature(creatureId = 0, color = 0, type = 2), // left screen
                1 to Creature(creatureId = 1, color = 0, type = 1),
                2 to Creature(creatureId = 2, color = 1, type = 1),
                3 to Creature(creatureId = 3, color = 0, type = 0),
                4 to Creature(creatureId = 4, color = -1, type = -1), // monster
            )
        )

        // act
        val command = drone.turn(turnData, creatures)

        // assert
        assertThat(command).isEqualTo("MOVE 1500 2500 1 ${creatures.creature(3)} TL")

    }

    @Test
    fun should_turn_on_power_light_when_in_habitat_zone_of_type_of_creature() {
        // arrange
        val drone = Drone(dronePosition = Point2D(0, 3_000))
        drone.droneTarget = DroneTarget(
            creatureToScan = Creature(type = 0),
            targetPosition = Point2D(0, 0)
        )

        // act
        val command = drone.light()

        // assert
        assertThat(command).isEqualTo(1)
    }

    @Test
    fun should_turn_off_power_light_when_not_in_habitat_zone() {
        // arrange
        val drone = Drone(dronePosition = Point2D(0, 8_000))
        drone.droneTarget = DroneTarget(
            creatureToScan = Creature(type = 0),
            targetPosition = Point2D(0, 0)
        )

        // act
        val light = drone.light()

        // assert
        assertThat(light).isEqualTo(0)
    }

    @Test
    fun should_turn_off_power_light_when_no_creature_to_scan_is_set() {
        // arrange
        val drone = Drone(dronePosition = Point2D(0, 3_000))
        drone.droneTarget = DroneTarget(
            creatureToScan = null,
            targetPosition = Point2D(0, 0)
        )

        // act
        val light = drone.light()

        // assert
        assertThat(light).isEqualTo(0)
    }

    @Test
    fun should_turn_off_power_light_when_no_droneTarget_is_set() {
        // arrange
        val drone = Drone(dronePosition = Point2D(0, 3_000))
        drone.droneTarget = null

        // act
        val light = drone.light()

        // assert
        assertThat(light).isEqualTo(0)
    }

    @Test
    fun should_set_target_position_based_on_radar_blink() {
        // arrange
        val drone = Drone(dronePosition = Point2D(3_000, 2_000))
        val turnData = TurnData(
            radarBlips = listOf(
                RadarBlip(droneId = 0, creatureId = 0, radar = "TL")
            ),
        )
        val creature = Creature(creatureId = 0, color = 0, type = 0)

        // act
        val droneTargetPosition = drone.droneTargetPosition(turnData, creature)

        // assert
        assertThat(droneTargetPosition).isEqualTo(
            DroneTarget(
                creatureToScan = creature,
                targetPosition = Point2D(2_500, 1_500),
                comment = "TL"
            )
        )
    }

    @Test
    fun should_scan_creature_only_when_the_creature_is_on_screen() {
        // arrange
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
        val creature = Drone().creatureToScan(turnData, creatures)

        // assert
        assertThat(creature).isEqualTo(creatures.creature(1))

    }

    @Test
    fun should_find_creature_to_scan() {
        // arrange
        val creatures = Creatures(
            creatures = mapOf(
                0 to Creature(creatureId = 0, color = 0, type = 2),
                1 to Creature(creatureId = 1, color = 0, type = 1), // scanned by friendly drone
                2 to Creature(creatureId = 2, color = 1, type = 1), // scanned by enemy drone
                3 to Creature(creatureId = 3, color = 0, type = 0), // left screen
                4 to Creature(creatureId = 4, color = -1, type = -1), // monster
                5 to Creature(creatureId = 5, color = 0, type = 0) // scan target of drone1
            )
        )
        val drone0 = Drone(droneId = 0, dronePosition = Point2D(2_000, 3_000))
        val drone1 = Drone(droneId = 1, dronePosition = Point2D(4_000, 6_000)).apply {
            droneTarget = DroneTarget(
                creatureToScan = creatures.creature(5),
                targetPosition = Point2D(0, 0)
            )
        }
        val turnData = TurnData(
            myScannedCreatures = listOf(),
            myDrones = listOf(
                drone0,
                drone1,
            ),
            dronesScans = DroneScans(
                listOf(
                    DroneScan(1, 1), // friendly drone
                    DroneScan(2, 2), // enemy drone
                )
            ),
            radarBlips = listOf(
                RadarBlip(0, 0, "TL"),
                RadarBlip(0, 1, "TL"),
                RadarBlip(0, 2, "TL"),
                RadarBlip(0, 4, "TL"),
                RadarBlip(0, 5, "TL"),
            )
        )

        // act
        val creature = drone0.creatureToScan(turnData, creatures)

        // assert
        assertThat(creature).isEqualTo(creatures.creature(2))
    }

    @Test
    fun should_avoid_monster_when_monster_is_bottom_right() {
        // arrange
        val drone = Drone(dronePosition = Point2D(1_000, 2_000))
        val droneTarget = DroneTarget(targetPosition = Point2D(1_494, 2_494))
        val monsters = listOf(
            VisibleCreature(
                creaturePosition = Point2D(2_000, 3_000),
                creatureVelocity = Point2D(-100, -100)
            )
        )

        // act
        val command = drone.avoidMonster(droneTarget, monsters)

        // assert
        assertThat(command).isEqualTo(
            DroneTarget(
                targetPosition = Point2D(524, 1524),
                comment = "HUNTED"
            )
        )
    }

    @Test
    fun should_avoid_monster_when_monster_is_top_right() {
        // arrange
        val drone = Drone(dronePosition = Point2D(1_000, 4_000))
        val droneTarget = DroneTarget(targetPosition = Point2D(1_494, 3_506))
        val monsters = listOf(
            VisibleCreature(
                creaturePosition = Point2D(2_000, 3_000),
                creatureVelocity = Point2D(-100, 100)
            )
        )

        // act
        val command = drone.avoidMonster(droneTarget, monsters)

        // assert
        assertThat(command).isEqualTo(
            DroneTarget(
                targetPosition = Point2D(524, 4476),
                comment = "HUNTED"
            )
        )
    }

    @Test
    fun should_avoid_monster_when_monster_is_bottom_left() {
        // arrange
        val drone = Drone(dronePosition = Point2D(3_000, 2_000))
        val droneTarget = DroneTarget(targetPosition = Point2D(2_506, 2_506))
        val monsters = listOf(
            VisibleCreature(
                creaturePosition = Point2D(2_000, 3_000),
                creatureVelocity = Point2D(100, -100)
            )
        )

        // act
        val command = drone.avoidMonster(droneTarget, monsters)

        // assert
        assertThat(command).isEqualTo(
            DroneTarget(
                targetPosition = Point2D(3481, 1529),
                comment = "HUNTED"
            )
        )
    }

    @Test
    fun should_avoid_monster_when_monster_is_top_left() {
        // arrange
        val drone = Drone(dronePosition = Point2D(3_000, 4_000))
        val droneTarget = DroneTarget(targetPosition = Point2D(2_506, 3_506))
        val monsters = listOf(
            VisibleCreature(
                creaturePosition = Point2D(2_000, 3_000),
                creatureVelocity = Point2D(100, 100)
            )
        )

        // act
        val command = drone.avoidMonster(droneTarget, monsters)

        // assert
        assertThat(command).isEqualTo(
            DroneTarget(
                targetPosition = Point2D(3476, 4476),
                comment = "HUNTED"
            )
        )
    }

    @Test
    fun should_escape_vector_for_monster_bottom_left() {
        // arrange
        val drone = Drone(dronePosition = Point2D(2581, 2429))
        val monsterTargetPosition = Point2D(2100, 2900)

        // act
        val escapeVector = drone.escapeVector(monsterTargetPosition)

        // assert
        assertThat(escapeVector).isEqualTo(Point2D(481, -471))
    }

    @Test
    fun should_escape_vector_for_monster_bottom_right() {
        // arrange
        val drone = Drone(dronePosition = Point2D(1424, 2424))
        val monsterTargetPosition = Point2D(1900, 2900)

        // act
        val escapeVector = drone.escapeVector(monsterTargetPosition)

        // assert
        assertThat(escapeVector).isEqualTo(Point2D(-476, -476))
    }

    @Test
    fun should_escape_vector_for_monster_top_left() {
        // arrange
        val drone = Drone(dronePosition = Point2D(2576, 3576))
        val monsterTargetPosition = Point2D(2100, 3100)

        // act
        val escapeVector = drone.escapeVector(monsterTargetPosition)

        // assert
        assertThat(escapeVector).isEqualTo(Point2D(476, 476))
    }

    @Test
    fun should_escape_vector_for_monster_top_right() {
        // arrange
        val drone = Drone(dronePosition = Point2D(1424, 3576))
        val monsterTargetPosition = Point2D(1900, 3100)

        // act
        val escapeVector = drone.escapeVector(monsterTargetPosition)

        // assert
        assertThat(escapeVector).isEqualTo(Point2D(-476, 476))
    }

    @Test
    fun should_return_true_when_one_type_is_scanned() {
        // arrange
        val drone0 = Drone(0, Point2D(3000, 2000))
        val drone2 = Drone(2, Point2D(7000, 5000))

        val creatures = Creatures(creatures = allCreatures)
        val turnData = TurnData(
            myDrones = listOf(drone0, drone2),
            dronesScans = DroneScans(
                listOf
                    (
                    DroneScan(0, 0),
                    DroneScan(0, 1),
                    DroneScan(2, 2),
                    DroneScan(2, 3),
                )
            ),
            radarBlips = listOf(
                RadarBlip(0, 0),
                RadarBlip(0, 1),
                RadarBlip(0, 2),
                RadarBlip(0, 3),
            )
        )

        // act
        val result = drone0.isAllCreaturesOfTypeInDrohneScan(turnData, creatures)

        // assert
        assertThat(result).isTrue()
    }

    @Test
    fun should_return_true_when_one_type_is_scanned_and_one_creature_left_the_screen() {
        // arrange
        val drone0 = Drone(0, Point2D(3000, 2000))
        val drone2 = Drone(2, Point2D(7000, 5000))

        val creatures = Creatures(creatures = allCreatures)
        val turnData = TurnData(
            myDrones = listOf(drone0, drone2),
            dronesScans = DroneScans(
                listOf
                    (
                    DroneScan(0, 0),
                    DroneScan(0, 1),
                    DroneScan(2, 2),
                )
            ),
            radarBlips = listOf(
                RadarBlip(0, 0),
                RadarBlip(0, 1),
                RadarBlip(0, 2),
            )
        )

        // act
        val result = drone0.isAllCreaturesOfTypeInDrohneScan(turnData, creatures)

        // assert
        assertThat(result).isTrue()
    }

    @Test
    fun should_return_true_when_one_type_is_scanned_and_one_scan_is_already_saved() {
        // arrange
        val drone0 = Drone(0, Point2D(3000, 2000))
        val drone2 = Drone(2, Point2D(7000, 5000))

        val creatures = Creatures(creatures = allCreatures)
        val turnData = TurnData(
            myScannedCreatures = listOf(3),
            myDrones = listOf(drone0, drone2),
            dronesScans = DroneScans(
                listOf
                    (
                    DroneScan(0, 0),
                    DroneScan(0, 1),
                    DroneScan(2, 2),
                )
            ),
            radarBlips = listOf(
                RadarBlip(0, 0),
                RadarBlip(0, 1),
                RadarBlip(0, 2),
                RadarBlip(0, 3),
            )
        )

        // act
        val result = drone0.isAllCreaturesOfTypeInDrohneScan(turnData, creatures)

        // assert
        assertThat(result).isTrue()
    }

    @Test
    fun should_not_surface_when_own_drone_scans_are_empty() {
        // arrange
        val drone0 = Drone(0, Point2D(3000, 2000))
        val drone2 = Drone(2, Point2D(7000, 5000))

        val creatures = Creatures(creatures = allCreatures)
        val turnData = TurnData(
            myDrones = listOf(drone0, drone2),
            dronesScans = DroneScans(
                listOf
                    (
                    DroneScan(2, 0),
                    DroneScan(2, 1),
                    DroneScan(2, 2),
                    DroneScan(2, 3),
                )
            )
        )

        // act
        val result = drone0.isAllCreaturesOfTypeInDrohneScan(turnData, creatures)

        // assert
        assertThat(result).isFalse()

    }

    @Test
    fun should_surface_when_saved_and_own_would_complete_type_of_one_creature() {
        // arrange
        val drone0 = Drone(0, Point2D(3000, 2000))
        val drone2 = Drone(2, Point2D(7000, 5000))

        val creatures = Creatures(creatures = allCreatures)
        val turnData = TurnData(
            myDrones = listOf(drone0, drone2),
            dronesScans = DroneScans(
                listOf
                    (
                    DroneScan(2, 0),
                    DroneScan(2, 1),
                    DroneScan(2, 2),
                    DroneScan(2, 3),
                )
            )
        )

        // act
        val result = drone0.isAllCreaturesOfTypeInDrohneScan(turnData, creatures)

        // assert
        assertThat(result).isFalse()

    }

    @Test
    @Disabled("Feature is disabled")
    fun should_surface_when_all_creatures_of_one_type_are_in_drohne_scan() {
        // arrange
        val drone0 = Drone(0, Point2D(3000, 2000))
        val drone2 = Drone(2, Point2D(7000, 5000))
        val turnData = TurnData(
            myDrones = listOf(drone0, drone2),
            dronesScans = DroneScans(
                listOf(
                    DroneScan(0, 0),
                    DroneScan(0, 1),
                    DroneScan(2, 2),
                    DroneScan(2, 3),
                )
            ),
            radarBlips = listOf(
                RadarBlip(0, 0),
                RadarBlip(0, 1),
                RadarBlip(0, 2),
                RadarBlip(0, 3),
                RadarBlip(0, 4),
                RadarBlip(0, 5),
                RadarBlip(0, 6),
                RadarBlip(0, 7),
                RadarBlip(0, 8),
                RadarBlip(0, 9),
                RadarBlip(0, 10),
            )
        )
        val creatures = Creatures(creatures = allCreatures)

        // act
        val command = drone0.turn(turnData, creatures)

        // assert
        assertThat(command).isEqualTo("MOVE 3000 500 0 null SURFACE TYPE SCANNED")
    }

    @Test
    fun should_move_directly_down_when_in_first_turn() {
        // arrange
        val drone = Drone(dronePosition = Point2D(2000, 500))

        // act
        val command = drone.turn(TurnData(), Creatures())

        // assert
        assertThat(command).isEqualTo("MOVE 2000 1500 0")
    }

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

    private val creaturesScenario1 = listOf(
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


    @Test
    fun should_create_ordered_list_of_drones_to_scan_scenario_1() {

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
        val scanLists = GameLogic(creatures).createScanLists(turnData, creatures)

        // assert
        assertThat(scanLists).hasSize(2)
        assertThat(scanLists[0]).containsOnly(4, 10, 6, 12, 9, 14)
        assertThat(scanLists[2]).containsOnly(11, 5, 13, 7, 15, 8)
    }

    @Test
    fun should_order_scan_list_by_type() {
        // arrange
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
        val creatures = Creatures(creatures = creaturesScenario1.associateBy { it.creatureId })

        // act
        val orderedScanList = GameLogic(creatures).orderScanList(listOf(4, 6, 9, 10, 12, 14), creatures)

        // assert
        assertThat(orderedScanList).containsExactly(4, 10, 6, 12, 9, 14)
    }

    @Test
    fun should_create_ordered_list_of_drones_to_scan_scenario_2() {

        // arrange
        val turnData = TurnData(
            myDrones = listOf(
                Drone(droneId = 1, dronePosition = Point2D(4000, 500)),
                Drone(droneId = 3, dronePosition = Point2D(8000, 500)),
            ),
            radarBlips = listOf(
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
        )
        val creaturesScenario2 = listOf(
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
        val creatures = Creatures(creatures = creaturesScenario2.associateBy { it.creatureId })

        // act
        val scanLists = GameLogic(creatures).createScanLists(turnData, creatures)

        // assert
        assertThat(scanLists).hasSize(2)
        assertThat(scanLists[1]).containsOnly(5, 10, 13, 7, 8, 14)
        assertThat(scanLists[3]).containsOnly(4, 11, 12, 6, 15, 9)
    }

}