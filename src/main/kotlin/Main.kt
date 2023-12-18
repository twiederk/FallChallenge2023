import java.util.*

/**
 * Score points by scanning valuable fish faster than your opponent.
 **/
fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val creatureCount = input.nextInt()
    for (i in 0 until creatureCount) {
        val creatureId = input.nextInt()
        val color = input.nextInt()
        val type = input.nextInt()
    }

    // game loop
    while (true) {
        val myScore = input.nextInt()
        val foeScore = input.nextInt()
        val myScanCount = input.nextInt()
        for (i in 0 until myScanCount) {
            val creatureId = input.nextInt()
        }
        val foeScanCount = input.nextInt()
        for (i in 0 until foeScanCount) {
            val creatureId = input.nextInt()
        }
        val myDroneCount = input.nextInt()
        for (i in 0 until myDroneCount) {
            val droneId = input.nextInt()
            val droneX = input.nextInt()
            val droneY = input.nextInt()
            val emergency = input.nextInt()
            val battery = input.nextInt()
        }
        val foeDroneCount = input.nextInt()
        for (i in 0 until foeDroneCount) {
            val droneId = input.nextInt()
            val droneX = input.nextInt()
            val droneY = input.nextInt()
            val emergency = input.nextInt()
            val battery = input.nextInt()
        }
        val droneScanCount = input.nextInt()
        for (i in 0 until droneScanCount) {
            val droneId = input.nextInt()
            val creatureId = input.nextInt()
        }
        val visibleCreatureCount = input.nextInt()
        for (i in 0 until visibleCreatureCount) {
            val creatureId = input.nextInt()
            val creatureX = input.nextInt()
            val creatureY = input.nextInt()
            val creatureVx = input.nextInt()
            val creatureVy = input.nextInt()
        }
        val radarBlipCount = input.nextInt()
        for (i in 0 until radarBlipCount) {
            val droneId = input.nextInt()
            val creatureId = input.nextInt()
            val radar = input.next()
        }
        val gameLogic = GameLogic()
        for (i in 0 until myDroneCount) {
            val command = gameLogic.turn()
            println(command) // MOVE <x> <y> <light (1|0)> | WAIT <light (1|0)>
        }
    }
}

class GameLogic {

    fun turn(): String {
        return "WAIT 1"
    }
}