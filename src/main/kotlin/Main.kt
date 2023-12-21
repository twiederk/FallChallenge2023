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
    val creatures = mutableMapOf<Int, Creature>()
    for (i in 0 until creatureCount) {
        val creatureId = input.nextInt()
        val color = input.nextInt()
        val type = input.nextInt()
        creatures[creatureId] = Creature(creatureId, color, type)
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
        val dronesScans = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until droneScanCount) {
            val droneId = input.nextInt()
            val creatureId = input.nextInt()
            dronesScans.add(Pair(droneId, creatureId))
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

        for (i in 0 until myDroneCount) {

            val command = gameLogic.turn(turnData)
            println(command) // MOVE <x> <y> <light (1|0)> | WAIT <light (1|0)>
        }
    }
}

class GameLogic(
    val gameData: GameData
) {
    fun turn(turnData: TurnData): String {
        return turnData.myDrones.fold("") { acc, drone -> acc + drone.turn(turnData, gameData.creatures) }
    }
}

data class VisibleCreature(
    val id: Int,
    val creatureId: Int,
    val creaturePosition: Point2D,
    val creatureVelocity: Point2D,
)

data class GameData(
    val creatureCount: Int = 0,
    val creatures: Map<Int, Creature> = mapOf()
)

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
    val dronesScans: List<Pair<Int, Int>> = listOf(),
    val visibleCreatures: List<VisibleCreature> = listOf(),
    val radarBlips: List<RadarBlip> = listOf(),
)

/*
Drones move towards the given point, with a maximum distance per turn of 600u. If the motors are not activated in a turn, the drone will sink by 300u.

At the end of the turn, fish within a radius of 800u will be automatically scanned.

If you have increased the power of your light, this radius becomes 2000u, but the battery drains by 5 points.
If the powerful light is not activated, the battery recharges by 1. The battery has a capacity of 30 and is fully charged at the beginning of the game.
*/
data class Drone(
    val droneId: Int = 0,
    val dronePosition: Point2D = Point2D(0, 0),
    val emergency: Int = 0,
    val battery: Int = 30,
) {

    var state: State = State.SEARCH

    fun turn(turnData: TurnData, creatures: Map<Int, Creature>): String {
        return when (state) {
            State.SEARCH -> search(turnData, creatures)
            State.SURFACE -> surface()
        }
    }

    private fun search(turnData: TurnData, creatures: Map<Int, Creature>): String {
        if (hasScannedCreature(turnData)) {
            state = State.SURFACE
            return "MOVE ${dronePosition.x} 500 0"
        }

        val direction = searchDirection(turnData, creatures) ?: return "WAIT 0"
        val light = light()
        return "MOVE ${direction.x} ${direction.y} $light"
    }

    fun searchDirection(turnData: TurnData, creatures: Map<Int, Creature>): Point2D? {
        val creature = nextCreatureToScan(creatures, turnData.myScannedCreatures) ?: return null
        val radarBlip = turnData.radarBlips.find { it.creatureId == creature.creatureId }
            ?: throw IllegalArgumentException("Can't find radar blip of creature [$creature]")
        return dronePosition + RadarBlip.RADAR_BLIP_TO_DIRECTION[radarBlip.radar] as Point2D
    }

    private fun hasScannedCreature(turnData: TurnData) = turnData.dronesScans.map { it.first }.contains(droneId)

    private fun isHabitatZone(): Boolean {
        return dronePosition.y >= 2500
    }

    private fun surface(): String {
        if (isSurfaced()) {
            state = State.SEARCH
            return "WAIT 0"
        }
        return "MOVE ${dronePosition.x} 500 0"
    }

    private fun isSurfaced(): Boolean = dronePosition.y <= 500

    fun nearestCreatureToScan(visibleCreatures: List<VisibleCreature>, scannedCreatures: List<Int>): VisibleCreature {
        check(visibleCreatures.isNotEmpty()) { "No visible creatures for drone $droneId" }
        val (creature, _) = visibleCreatures
            .filter { it.creatureId !in scannedCreatures }
            .map { Pair(it, dronePosition.manhattenDistance(it.creaturePosition)) }
            .minBy { it.second }
        return creature
    }

    fun nextCreatureToScan(creatures: Map<Int, Creature>, myScannedCreatures: List<Int>): Creature? {
        val notScannedCreatures = creatures.values.filterNot { it.creatureId in myScannedCreatures }
        val sortedCreatures = notScannedCreatures.sortedBy { it.type }
        if (sortedCreatures.isEmpty()) return null
        return sortedCreatures.first()
    }

    fun light(): Int {
        if (isHabitatZone()) return 1
        return 0
    }

    enum class State { SEARCH, SURFACE }

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

data class RadarBlip(
    val droneId: Int = 0,
    val creatureId: Int = 0,
    val radar: String = "TL"
) {
    companion object {
        val RADAR_BLIP_TO_DIRECTION = mapOf<String, Point2D>(
            "TL" to Point2D(-500, -500),
            "TR" to Point2D(500, -500),
            "BL" to Point2D(-500, 500),
            "BR" to Point2D(500, 500),
        )
    }
}

fun printErr(errorMsg: String) {
    System.err.println(errorMsg)
}
