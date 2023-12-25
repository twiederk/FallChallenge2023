import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DroneScansTest {

    @Test
    fun should_return_drone_scans_when_list_of_drone_ids_given() {
        // arrange
        val droneScans = DroneScans(
            listOf(
                DroneScan(0, 1),
                DroneScan(0, 2),
                DroneScan(1, 0),
                DroneScan(1, 4),
                DroneScan(2, 3),
                DroneScan(2, 4),
            )
        )

        // act
        val creaturesInDroneScans = droneScans.creatureIdsInDroneScans(listOf(0, 2))

        // assert
        assertThat(creaturesInDroneScans).containsExactly(1, 2, 3, 4)
    }

}