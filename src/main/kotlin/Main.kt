import java.util.*
import kotlin.math.abs

/**
 * Score points by scanning valuable fish faster than your opponent.
 **/
fun main() {
    // First line: creatureCount an integer for the number of creatures in the game zone. Will always be 12.
    // Next creatureCount lines: 3 integers describing each creature:
    // creatureId for this creature's unique id.
    // color (0 to 3) and type (0 to 2).
    val input = Scanner(System.`in`)
    val creatureCount = input.nextInt()
    val creatures = mutableListOf<Creature>()
    for (i in 0 until creatureCount) {
        val creatureId = input.nextInt()
        val color = input.nextInt()
        val type = input.nextInt()
        creatures.add(Creature(creatureId, color, type))
    }

    val gameData = GameData(
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
        for (i in 0 until droneScanCount) {
            val droneId = input.nextInt()
            val creatureId = input.nextInt()
        }
        val visibleCreatureCount = input.nextInt()
        val visibleCreatures = mutableListOf<VisibleCreature>()
        for (i in 0 until visibleCreatureCount) {
            val creatureId = input.nextInt()
            val creatureX = input.nextInt()
            val creatureY = input.nextInt()
            val creatureVx = input.nextInt()
            val creatureVy = input.nextInt()
            visibleCreatures.add(
                VisibleCreature(
                    id = i,
                    creatureId = creatureId,
                    creaturePosition = Point2D(creatureX, creatureY),
                    creatureVelocity = Point2D(creatureVx, creatureVy)
                )
            )
        }
        val radarBlipCount = input.nextInt()
        for (i in 0 until radarBlipCount) {
            val droneId = input.nextInt()
            val creatureId = input.nextInt()
            val radar = input.next()
        }
        val turnData =
            TurnData(myScore, foeScore, myScannedCreatures, foeScannedCreatures, myDrones, foeDrones, visibleCreatures)

        for (i in 0 until myDroneCount) {

            val command = gameLogic.turn(turnData)
            println(command) // MOVE <x> <y> <light (1|0)> | WAIT <light (1|0)>
        }
    }
}

data class GameLogic(
    val gameData: GameData
) {
    fun turn(turnData: TurnData): String {
        val drone = turnData.myDrones[0]
        val nearestCreature = drone.nearestCreatureToScan(turnData.visibleCreatures, turnData.myScannedCreatures)
        return "MOVE ${nearestCreature.creaturePosition.x} ${nearestCreature.creaturePosition.y} 0"
    }
}

data class VisibleCreature(
    val id: Int,
    val creatureId: Int,
    val creaturePosition: Point2D,
    val creatureVelocity: Point2D,
) {
    var distanceToDrone: Int = 0
}

data class GameData(
    val creatureCount: Int,
    val creatures: List<Creature>
)

data class Creature(
    val creatureId: Int,
    val color: Int,
    val type: Int
)

data class TurnData(
    val myScore: Int,
    val foeScore: Int,
    val myScannedCreatures: List<Int>,
    val foeScannedCreatures: List<Int>,
    val myDrones: List<Drone>,
    val foeDrones: List<Drone>,
    val visibleCreatures: List<VisibleCreature>,
)

/*
Drones move towards the given point, with a maximum distance per turn of 600u. If the motors are not activated in a turn, the drone will sink by 300u.

At the end of the turn, fish within a radius of 800u will be automatically scanned.

If you have increased the power of your light, this radius becomes 2000u, but the battery drains by 5 points.
If the powerful light is not activated, the battery recharges by 1. The battery has a capacity of 30 and is fully charged at the beginning of the game.
*/
data class Drone(
    val droneId: Int,
    val dronePosition: Point2D,
    val emergency: Int,
    val battery: Int,
) {
    fun nearestCreatureToScan(visibleCreatures: List<VisibleCreature>, scannedCreatures: List<Int>): VisibleCreature {
        val notScannedCreatures = visibleCreatures
            .filter { it.creatureId !in scannedCreatures }
        notScannedCreatures.forEach { it.distanceToDrone = dronePosition.manhattenDistance(it.creaturePosition) }
        return notScannedCreatures.minBy { it.distanceToDrone }
    }
}

data class Point2D(
    val x: Int,
    val y: Int
) {
    operator fun minus(other: Point2D): Point2D =
        Point2D(x - other.x, y - other.y)

    operator fun plus(other: Point2D): Point2D =
        Point2D(x + other.x, y + other.y)

    fun manhattenDistance(other: Point2D): Int =
        abs(x - other.x) + abs(y - other.y)
}