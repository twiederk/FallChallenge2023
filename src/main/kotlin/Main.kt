import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Score points by scanning valuable fish faster than your opponent.
 **/
fun main() {
    val input = Scanner(System.`in`)
    val creatureCount = input.nextInt()
    val creatures = mutableMapOf<Int, Creature>()
    for (i in 0 until creatureCount) {
        val creatureId = input.nextInt()
        val color = input.nextInt()
        val type = input.nextInt()
        creatures[creatureId] = Creature(creatureId, color, type)
    }

    val gameData = Creatures(
        creatureCount,
        creatures
    )
    val gameLogic = GameLogic(gameData)

    // game loop
    while (true) {
        val myScore = input.nextInt()
        val foeScore = input.nextInt()
        val myScanCount = input.nextInt()
        val myScannedCreatures = mutableListOf<Int>()
        for (i in 0 until myScanCount) {
            val creatureId = input.nextInt()
            myScannedCreatures.add(creatureId)
        }
        val foeScanCount = input.nextInt()
        val foeScannedCreatures = mutableListOf<Int>()
        for (i in 0 until foeScanCount) {
            val creatureId = input.nextInt()
            foeScannedCreatures.add(creatureId)
        }
        val myDroneCount = input.nextInt()
        val myDrones = mutableListOf<Drone>()
        for (i in 0 until myDroneCount) {
            val droneId = input.nextInt()
            val droneX = input.nextInt()
            val droneY = input.nextInt()
            val emergency = input.nextInt()
            val battery = input.nextInt()
            myDrones.add(
                Drone(
                    droneId = droneId,
                    dronePosition = Point2D(droneX, droneY),
                    emergency = emergency,
                    battery = battery
                )
            )
        }
        val foeDroneCount = input.nextInt()
        val foeDrones = mutableListOf<Drone>()
        for (i in 0 until foeDroneCount) {
            val droneId = input.nextInt()
            val droneX = input.nextInt()
            val droneY = input.nextInt()
            val emergency = input.nextInt()
            val battery = input.nextInt()
            foeDrones.add(
                Drone(
                    droneId = droneId,
                    dronePosition = Point2D(droneX, droneY),
                    emergency = emergency,
                    battery = battery
                )
            )
        }
        val droneScanCount = input.nextInt()
        val dronesScansList = mutableListOf<DroneScan>()
        for (i in 0 until droneScanCount) {
            val droneId = input.nextInt()
            val creatureId = input.nextInt()
            dronesScansList.add(DroneScan(droneId, creatureId))
        }
        val dronesScans = DroneScans(dronesScansList)
        val visibleCreatureCount = input.nextInt()
        val visibleCreatures = VisibleCreatures()
        for (i in 0 until visibleCreatureCount) {
            val creatureId = input.nextInt()
            val creatureX = input.nextInt()
            val creatureY = input.nextInt()
            val creatureVx = input.nextInt()
            val creatureVy = input.nextInt()
            visibleCreatures.add(
                VisibleCreature(
                    creatureId = creatureId,
                    creaturePosition = Point2D(creatureX, creatureY),
                    creatureVelocity = Point2D(creatureVx, creatureVy)
                )
            )
        }
        val radarBlipCount = input.nextInt()
        val radarBlips = mutableListOf<RadarBlip>()

        for (i in 0 until radarBlipCount) {
            val droneId = input.nextInt()
            val creatureId = input.nextInt()
            val radar = input.next()
            radarBlips.add(RadarBlip(droneId, creatureId, radar))
        }
        val turnData =
            TurnData(
                turnNumber = gameLogic.turnNumber,
                myScore,
                foeScore,
                myScannedCreatures,
                foeScannedCreatures,
                myDrones,
                foeDrones,
                dronesScans,
                visibleCreatures,
                radarBlips
            )

        gameLogic.turn(turnData).forEach { println(it) }
    }
}

class GameLogic(
    private val creatures: Creatures = Creatures()
) {
    var turnNumber: Int = 1
    var initialScanLists: Map<Int, List<Int>> = mapOf()

    fun turn(turnData: TurnData): List<String> {
        if (turnData.turnNumber == 1) {
            initialScanLists = initialScanLists(turnData, creatures)
        }
        turnData.myDrones.forEach { it.initialScanList = initialScanLists[it.droneId] ?: listOf() }
        val commands = turnData.myDrones.map { it.turn(turnData, creatures) }
        turnNumber++
        return commands
    }

    fun createScanLists(turnData: TurnData, creatures: Creatures): Map<Int, List<Int>> {
        val monsterIds = creatures.monsterIds()
        val associatedScansList: MutableList<Map<Int, Char>> = mutableListOf()
        for (droneId in turnData.myDrones.map { it.droneId }) {
            val associatedScans = turnData.radarBlips
                .filter { it.droneId == droneId }
                .filter { it.creatureId !in monsterIds }
                .associateBy({ it.creatureId }, { it.radar[1] })
            associatedScansList.add(associatedScans)
        }
        val mergedScanLists: MutableMap<Int, String> = mutableMapOf()
        for (key in associatedScansList[0].keys) {
            val char1 = associatedScansList[0][key]
            val char2 = associatedScansList[1][key]
            mergedScanLists[key] = "$char1$char2"
        }
        val numberOfLL = mergedScanLists.values.count { it == "LL" }
        val numberOfRR = mergedScanLists.values.count { it == "RR" }
        val leftList: List<Int>
        val rightList: List<Int>
        if (numberOfLL < numberOfRR) {
            leftList = mergedScanLists.filter { it.value.contains("L") }.map { it.key }
            rightList = mergedScanLists.filter { it.key !in leftList }.map { it.key }
        } else {
            rightList = mergedScanLists.filter { it.value.contains("R") }.map { it.key }
            leftList = mergedScanLists.filter { it.key !in rightList }.map { it.key }

        }
        val idOfLeftDrone = turnData.myDrones.first { it.dronePosition.x < 5000 }.droneId
        val idOfRightDrone = turnData.myDrones.first { it.dronePosition.x > 5000 }.droneId
        return mapOf(
            idOfLeftDrone to leftList,
            idOfRightDrone to rightList
        )
    }

    fun orderScanList(scanList: List<Int>, creatures: Creatures): List<Int> {
        return creatures.values
            .filter { it.creatureId in scanList }
            .sortedBy { it.type }
            .map { it.creatureId }
    }

    fun initialScanLists(turnData: TurnData, creatures: Creatures): Map<Int, List<Int>> {
        val scanLists = createScanLists(turnData, creatures)
        val orderedScanLists = mutableMapOf<Int, List<Int>>()
        scanLists.forEach { orderedScanLists[it.key] = orderScanList(it.value, creatures) }
        return scanLists
    }
}

data class VisibleCreature(
    val creatureId: Int = 0,
    val creaturePosition: Point2D = Point2D(0, 0),
    val creatureVelocity: Point2D = Point2D(0, 0),
)

data class Creatures(
    private val creatureCount: Int = 0,
    private val creatures: Map<Int, Creature> = mapOf()
) {
    fun creature(creatureId: Int): Creature {
        return creatures[creatureId] ?: throw IllegalArgumentException("Can't find creature with id: $creatureId")
    }

    fun monsterIds(): List<Int> {
        return creatures.values.filter { it.type == -1 }.map { it.creatureId }
    }

    fun onScreenIdsWithoutMonsters(radarBlips: List<RadarBlip>): List<Int> {
        val creaturesOnRadar = radarBlips.map { it.creatureId }.toSet()
        return creatures.values.filter { it.type != -1 }.filter { it.creatureId in creaturesOnRadar }
            .map { it.creatureId }
    }

    fun ofTypeOnScreen(type: Int, radarBlips: List<RadarBlip>): List<Int> {
        val creaturesOnRadar = radarBlips.map { it.creatureId }.toSet()
        return creatures.values.filter { it.creatureId in creaturesOnRadar }.filter { it.type == type }
            .map { it.creatureId }
    }

    val values: Collection<Creature> = creatures.values
}

data class Creature(
    val creatureId: Int = 0,
    val color: Int = 0,
    val type: Int = 0
)

data class TurnData(
    val turnNumber: Int = 1,
    val myScore: Int = 0,
    val foeScore: Int = 0,
    val myScannedCreatures: List<Int> = listOf(),
    val foeScannedCreatures: List<Int> = listOf(),
    val myDrones: List<Drone> = listOf(),
    val foeDrones: List<Drone> = listOf(),
    val dronesScans: DroneScans = DroneScans(),
    val visibleCreatures: VisibleCreatures = VisibleCreatures(),
    val radarBlips: List<RadarBlip> = listOf(),
)

data class Drone(
    val droneId: Int = 0,
    val dronePosition: Point2D = Point2D(0, 0),
    val emergency: Int = 0,
    val battery: Int = 30,
) {

    companion object {
        const val DRONE_SPEED = 600
        const val DRONE_SECURITY_DISTANCE = 750
        const val SURFACE = 500
    }

    var initialScanList: List<Int> = listOf()
    var droneTarget: DroneTarget? = null

    fun turn(turnData: TurnData, creatures: Creatures): String {
        if (turnData.turnNumber == 1) {
            return "MOVE ${dronePosition.x} ${dronePosition.y + 1000} 0"
        }
        val creatureToScan = creatureToScan(turnData, creatures)
        var droneTarget = droneTargetPosition(turnData, creatureToScan)

        if (!isTargetPositionSafe(turnData, creatures, droneTarget.targetPosition)) {
            val monsters = turnData.visibleCreatures.monsters(creatures)
            droneTarget = avoidMonster(droneTarget, monsters)
        }
        this.droneTarget = droneTarget
        val light = light()
        return "MOVE ${droneTarget.targetPosition.x} ${droneTarget.targetPosition.y} $light $initialScanList ${droneTarget.comment}"
    }

    fun creatureToScan(
        turnData: TurnData,
        creatures: Creatures
    ): Creature? {
        val creaturesOnRadar = turnData.radarBlips.map { it.creatureId }.toSet()
        val myDroneIds = turnData.myDrones.map { it.droneId }
        val creaturesToScanByOtherDrones =
            turnData.myDrones.filter { it.droneId != droneId }.mapNotNull { it.droneTarget?.creatureToScan?.creatureId }
        val creaturesInMyDroneScan = turnData.dronesScans.creatureIdsInDroneScans(myDroneIds)
        val sortedCreatures = creatures.values.asSequence()
            .filterNot { it.type == -1 } // no monsters
            .filterNot { it.creatureId in turnData.myScannedCreatures } // no creatures from saved scans
            .filterNot { it.creatureId in creaturesInMyDroneScan } // no creatures form scans in drone
            .filterNot { it.creatureId in creaturesToScanByOtherDrones } // no creatures to scan from my other drones
            .filter { it.creatureId in creaturesOnRadar } // only creatures on radar (screen)
            .sortedBy { it.type }.toList()
        return sortedCreatures.firstOrNull()
    }

    fun droneTargetPosition(turnData: TurnData, creatureToScan: Creature?): DroneTarget {
        return if (creatureToScan == null) {
            DroneTarget(
                targetPosition = Point2D(dronePosition.x, SURFACE),
                comment = "SURFACE ALL SCANNED"
            )
        } else {
            val radarBlip =
                turnData.radarBlips.find { it.creatureId == creatureToScan.creatureId && it.droneId == droneId }
                    ?: throw IllegalArgumentException("Can't find radar blip of creature [$creatureToScan]")
            DroneTarget(
                creatureToScan = creatureToScan,
                targetPosition = dronePosition + RadarBlip.RADAR_BLIP_TO_DIRECTION[radarBlip.radar] as Point2D,
                comment = radarBlip.radar
            )
        }
    }

    private fun isTargetPositionSafe(turnData: TurnData, creatures: Creatures, targetPosition: Point2D): Boolean {
        val monsters = turnData.visibleCreatures.monsters(creatures)
        if (monsters.isNotEmpty()) {
            val droneVelocity = (targetPosition - dronePosition).scaledLength(DRONE_SPEED)
            val droneTargetPosition = dronePosition + droneVelocity
            for (monster in monsters) {
                val monsterTargetPosition = monster.creaturePosition + monster.creatureVelocity
                val distance = droneTargetPosition.distance(monsterTargetPosition)
                if (distance < DRONE_SECURITY_DISTANCE) {
                    return false
                }
            }
        }
        return true
    }

    fun avoidMonster(droneTarget: DroneTarget, monsters: List<VisibleCreature>): DroneTarget {
        System.err.println("************** $droneId **************")
        System.err.println("dronePosition = $dronePosition")
        System.err.println("droneTarget.targetPosition = ${droneTarget.targetPosition}")
        val droneVelocity = (droneTarget.targetPosition - dronePosition).scaledLength(DRONE_SPEED)
        System.err.println("droneVelocity = $droneVelocity")
        var droneTargetPosition = dronePosition + droneVelocity
        System.err.println("* droneTargetPosition = $droneTargetPosition")

        for (monster in monsters) {
            System.err.println("monster.creaturePosition = ${monster.creaturePosition}")
            System.err.println("monster.creatureVelocity = ${monster.creatureVelocity}")
            val monsterTargetPosition = monster.creaturePosition + monster.creatureVelocity
            System.err.println("* monsterTargetPosition = $monsterTargetPosition")

            System.err.println("distance = ${droneTargetPosition.distance(monsterTargetPosition)}")

            if (droneTargetPosition.distance(monsterTargetPosition) > DRONE_SECURITY_DISTANCE) {
                continue // the monster is not after me
            }
            val escapeVector = escapeVector(monsterTargetPosition)
            System.err.println("* escapeVector = $escapeVector")

            var counter = 0
            while (droneTargetPosition.distance(monsterTargetPosition) <= DRONE_SECURITY_DISTANCE && counter < 10) {
                droneTargetPosition += escapeVector
                counter++
                System.err.println("NEW droneTargetPosition = $droneTargetPosition")
            }
            return droneTarget.copy(
                targetPosition = droneTargetPosition,
                comment = "HUNTED"
            )
        }
        return droneTarget.copy(comment = "${droneTarget.comment} NOT HUNTED")
    }

    fun light(): Int {
        val type = droneTarget?.creatureToScan?.type
        type?.let {
            when (type) {
                0 -> if (dronePosition.y in 500..7000) return 1 else 0
                1 -> if (dronePosition.y in 3000..10000) return 1 else 0
                2 -> if (dronePosition.y in 5500..10000) return 1 else 0
                else -> return 0

            }
        }
        return 0
    }

    fun escapeVector(monsterTargetPosition: Point2D): Point2D {
        return dronePosition - monsterTargetPosition
    }

    fun isAllCreaturesOfTypeInDrohneScan(turnData: TurnData, creatures: Creatures): Boolean {
        val myScannedCreatureIds = turnData.dronesScans.creatureIdsInDroneScans(listOf(droneId))
        if (myScannedCreatureIds.isEmpty()) {
            return false
        }
        val myCreaturesOfDroneScans = creatures.values.filter { it.creatureId in myScannedCreatureIds }
        val myGroupedAndCountedByType = myCreaturesOfDroneScans.groupingBy { it.type }.eachCount()

        val otherDroneId = turnData.myDrones.filterNot { it.droneId == droneId }.first().droneId
        val otherScannedCreatureIds = turnData.dronesScans.creatureIdsInDroneScans(listOf(otherDroneId))
        val otherCreaturesOfDroneScans = creatures.values.filter { it.creatureId in otherScannedCreatureIds }
        val otherGroupedAndCountedByType = otherCreaturesOfDroneScans.groupingBy { it.type }.eachCount()


        for (type in 0..2) {
            // if type is already saved => skip it
            val creaturesOfTypeOnScreen = creatures.ofTypeOnScreen(type, turnData.radarBlips)
            if (creaturesOfTypeOnScreen.none { it !in turnData.myScannedCreatures }) {
                continue
            }

            val myScannedCreaturesCount = myGroupedAndCountedByType.getOrDefault(type, 0)
            val otherScannedCreaturesCount = otherGroupedAndCountedByType.getOrDefault(type, 0)
            val savedScannedCreaturesCount =
                turnData.myScannedCreatures.map { creatures.creature(it) }.count { it.type == type }

            val totalScannedCreatures =
                myScannedCreaturesCount + otherScannedCreaturesCount + savedScannedCreaturesCount

            val creaturesOfTypeOnScreenCount = creatures.ofTypeOnScreen(type, turnData.radarBlips).count()
            if (totalScannedCreatures == creaturesOfTypeOnScreenCount) {
                return true
            }
        }
        return false
    }

}

data class DroneTarget(
    val creatureToScan: Creature? = null,
    val targetPosition: Point2D,
    val comment: String = "",
)

data class Point2D(
    val x: Int,
    val y: Int
) {
    operator fun minus(other: Point2D): Point2D =
        Point2D(x - other.x, y - other.y)

    operator fun plus(other: Point2D): Point2D =
        Point2D(x + other.x, y + other.y)

    fun distance(other: Point2D): Double {
        return sqrt((other.x - x).toDouble().pow(2) + (other.y - y).toDouble().pow(2))
    }

    private fun length(): Double {
        return sqrt(x.toDouble().pow(2) + y.toDouble().pow(2))
    }

    fun scaledLength(desiredLength: Int): Point2D {
        val scaleFactor = desiredLength.toDouble() / length()
        val scaledX = x.toDouble() * scaleFactor
        val scaledY = y.toDouble() * scaleFactor
        return Point2D(scaledX.toInt(), scaledY.toInt())
    }


}

data class RadarBlip(
    val droneId: Int = 0,
    val creatureId: Int = 0,
    val radar: String = "TL"
) {
    companion object {
        val RADAR_BLIP_TO_DIRECTION = mapOf(
            "TL" to Point2D(-500, -500),
            "TR" to Point2D(500, -500),
            "BL" to Point2D(-500, 500),
            "BR" to Point2D(500, 500),
        )
    }
}

data class DroneScan(
    val droneId: Int = 0,
    val creatureId: Int = 0
)

class VisibleCreatures {

    private val visibleCreatures = mutableListOf<VisibleCreature>()

    fun add(visibleCreature: VisibleCreature) {
        visibleCreatures.add(visibleCreature)
    }

    fun monsters(creatures: Creatures): List<VisibleCreature> {
        return visibleCreatures.filter { it.creatureId in creatures.monsterIds() }
    }

}

class DroneScans(
    val dronesScans: List<DroneScan> = listOf()
) {
    fun creatureIdsInDroneScans(droneIds: List<Int>): List<Int> {
        return dronesScans.filter { it.droneId in droneIds }.map { it.creatureId }
    }
}
