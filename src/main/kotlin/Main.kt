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
        val dronesScans = mutableListOf<DroneScan>()
        for (i in 0 until droneScanCount) {
            val droneId = input.nextInt()
            val creatureId = input.nextInt()
            dronesScans.add(DroneScan(droneId, creatureId))
        }
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
    private val creatures: Creatures
) {
    fun turn(turnData: TurnData): List<String> {
        return turnData.myDrones.map { it.turn(turnData, creatures) }
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

    val values: Collection<Creature> = creatures.values
}

data class Creature(
    val creatureId: Int = 0,
    val color: Int = 0,
    val type: Int = 0
)

data class TurnData(
    val myScore: Int = 0,
    val foeScore: Int = 0,
    val myScannedCreatures: List<Int> = listOf(),
    val foeScannedCreatures: List<Int> = listOf(),
    val myDrones: List<Drone> = listOf(),
    val foeDrones: List<Drone> = listOf(),
    val dronesScans: List<DroneScan> = listOf(),
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

    private var droneTarget: DroneTarget? = null

    fun turn(turnData: TurnData, creatures: Creatures): String {
        val creatureToScan = creatureToScan(turnData, creatures)
        var droneTarget = droneTargetPosition(turnData, creatureToScan)

        if (!isTargetPositionSafe(turnData, creatures, droneTarget.targetPosition)) {
            val monsters = turnData.visibleCreatures.monsters(creatures)
            droneTarget = avoidMonster(droneTarget, monsters)
        }
        this.droneTarget = droneTarget
        val light = light()
        return "MOVE ${droneTarget.targetPosition.x} ${droneTarget.targetPosition.y} $light ${droneTarget.creatureToScan} ${droneTarget.comment}"


//        if (isAllCreaturesScanned(turnData)) {
//            return "MOVE ${dronePosition.x} 500 0 $creatureToScan"
//        }

    }

    fun creatureToScan(
        turnData: TurnData,
        creatures: Creatures
    ): Creature? {
        val creaturesOnRadar = turnData.radarBlips.map { it.creatureId }
        val myDroneIds = turnData.myDrones.map { it.droneId }
        val creaturesInMyDroneScan = turnData.dronesScans.filter { it.droneId in myDroneIds }.map { it.creatureId }
        val sortedCreatures = creatures.values.asSequence()
            .filterNot { it.type == -1 } // no monsters
            .filterNot { it.creatureId in turnData.myScannedCreatures } // no creatures from saved scans
            .filterNot { it.creatureId in creaturesInMyDroneScan } // no creatures form scans in drone
            .filter { it.creatureId in creaturesOnRadar } // only creatures on radar (screen)
            .sortedBy { it.type }.toList()
        if (sortedCreatures.isEmpty()) return null
        return sortedCreatures.first()
    }

    fun droneTargetPosition(turnData: TurnData, creatureToScan: Creature?): DroneTarget {
        return if (creatureToScan == null) {
            DroneTarget(
                targetPosition = Point2D(dronePosition.x, SURFACE),
                comment = "SURFACE"
            )
        } else {
            val radarBlip =
                turnData.radarBlips.find { it.creatureId == creatureToScan.creatureId && it.droneId == droneId }
                    ?: throw IllegalArgumentException("Can't find radar blip of creature [$creatureToScan]")
            DroneTarget(
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
        val droneVelocity = (droneTarget.targetPosition - dronePosition).scaledLength(DRONE_SPEED)
        var droneTargetPosition = dronePosition + droneVelocity
        System.err.println("droneTargetPosition = $droneTargetPosition")

        for (monster in monsters) {
            val monsterTargetPosition = monster.creaturePosition + monster.creatureVelocity
            if (droneTargetPosition.distance(monsterTargetPosition) > DRONE_SECURITY_DISTANCE) {
                continue // the monster is not after me
            }
            val offset = monsterTargetPosition - droneTargetPosition
            while (droneTargetPosition.distance(monsterTargetPosition) <= DRONE_SECURITY_DISTANCE) {
                droneTargetPosition += offset
                System.err.println("NEW droneTargetPosition = $droneTargetPosition")
            }
            return droneTarget.copy(
                targetPosition = Point2D(droneTargetPosition.x, droneTargetPosition.y),
                comment = "HUNTED"
            )
        }
        return droneTarget.copy(comment = "${droneTarget.comment} NOT HUNTED")
    }

    private fun isHabitatZone(): Boolean {
        return dronePosition.y >= 2500
    }

    fun light(): Int {
        if (isHabitatZone()) return 1
        return 0
    }

    fun isAllCreaturesScanned(turnData: TurnData): Boolean {
//        creature must be on screen (in radarBlips)
//        must be (in saved scans or in droneScans)
        val scannedCreatures = mutableSetOf<Int>()
        scannedCreatures.addAll(turnData.myScannedCreatures)
        val myDroneIds = turnData.myDrones.map { it.droneId }
        val scansInMyDrones = turnData.dronesScans.filter { it.droneId in myDroneIds }.map { it.creatureId }
        scannedCreatures.addAll(scansInMyDrones)
        val creaturesOnScreen = turnData.radarBlips.map { it.creatureId }.toSet()
        return scannedCreatures.size >= creaturesOnScreen.size
    }

}

data class DroneTarget(
    val creatureToScan: Creature? = null,
    val targetPosition: Point2D,
    val comment: String = ""
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