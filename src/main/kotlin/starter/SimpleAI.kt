package starter


import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.utils.memory.memory
import screeps.utils.unsafe.delete
import screeps.utils.unsafe.jsObject

private val minPopulations = arrayOf(Role.HARVESTER to 3, Role.UPGRADER to 1, Role.BUILDER to 3, Role.UPGRADER to 4)

var Memory.update: Int by memory { 0 }

var workPriorities = mapOf<WorkAction,Int>(
        WorkAction.FILLING to 1,
        WorkAction.REPAIRING to 2,
        WorkAction.BUILDING to 3,
        WorkAction.UPGRADING to 4,
        WorkAction.WAITING to 10
)

fun gameLoop() {
    val mainSpawn: StructureSpawn = Game.spawns["Spawn1"] ?: return

    Memory.update++
    if (Memory.update > 49) {
        Memory.update = 0
        println("fill: ${mainSpawn.room.requiredEnergy(WorkAction.FILLING)}  repair: ${mainSpawn.room.requiredEnergy(WorkAction.REPAIRING)}  build: ${mainSpawn.room.requiredEnergy(WorkAction.BUILDING)}  upgrade: ${mainSpawn.room.requiredEnergy(WorkAction.UPGRADING)}")
    }

    //delete memories of creeps that have passed away
    houseKeeping(Game.creeps)

    //make sure we have at least some creeps
    spawnCreeps(mainSpawn.room)

    for ((_, creep) in Game.creeps) {
        try {
            when (creep.memory.role) {
                Role.HARVESTER -> creep.runActionHarvestOld()
                Role.BUILDER -> creep.runActionBuildOld()
                Role.UPGRADER -> creep.runActionUpgradeOld(mainSpawn.room.controller!!)
                Role.WORKER -> creep.roleWorker()
                else -> creep.pause()
            }
        } catch(exception: dynamic) {
            println("Error executing ${creep.name}: $exception")
        }
    }

}

private fun spawnCreeps(room: Room) {
    val bodyBig: Array<BodyPartConstant> = arrayOf<BodyPartConstant>(WORK,WORK,WORK,CARRY,CARRY,MOVE,MOVE, MOVE)
    val bodySmall: Array<BodyPartConstant> = arrayOf<BodyPartConstant>(WORK,CARRY, MOVE)

    val genericBody = ratioBody(mapOf(WORK to 1, CARRY to 1, MOVE to 1))

    if(room.creepCount() < 8) {
        val role = Role.WORKER
        val newName = "${role.name}_${Game.time}"
        val spawns = room.availableSpawns()
        var body: CreepBodyBuilder  =
            if(room.creepCount() < 1) fixedBody(arrayOf(WORK, CARRY, MOVE))
            else genericBody

        if(spawns.isNotEmpty() && body.minEnergyWithin(room.energyCapacityAvailable) <= room.energyAvailable) {
            val code = spawns[0].spawnCreep(body.genBody(room.energyCapacityAvailable), newName, options {
                memory = jsObject<CreepMemory> { this.role = role; this.homeRoom = room.name }
            })
            when (code) {
                OK -> console.log("spawning $newName with body $bodyBig")
                ERR_BUSY -> run { } // do nothing
                ERR_NOT_ENOUGH_ENERGY -> run {}
                else -> console.log("unhandled error code $code")
            }
        }

    }

//    for ((role, min) in minPopulations) {
//        val current = creeps.filter { it.memory.role == role }
//        if (current.size < min) {
//            val newName = "${role.name}_${Game.time}"
//            val code = spawn.spawnCreep(bodyBig, newName, options {
//                memory = jsObject<CreepMemory> { this.role = role }
//            })
//
//            when (code) {
//                OK -> console.log("spawning $newName with body $bodyBig")
//                ERR_BUSY -> run { } // do nothing
//                ERR_NOT_ENOUGH_ENERGY -> run {
//                    if (spawn.room.energyAvailable >= 200) {
//                        spawn.spawnCreep(bodySmall, newName, options {
//                            memory = jsObject<CreepMemory> { this.role = role }
//                        })
//                        console.log("spawning $newName with body $bodySmall")
//
//                    }
//                }
//                else -> console.log("unhandled error code $code")
//            }
//
//
//
//
//        }
//    }
}

private fun houseKeeping(creeps: Record<String, Creep>) {
    for ((creepName, _) in Memory.creeps) {
        if (creeps[creepName] == null) {
            console.log("deleting obsolete memory entry for creep $creepName")
            delete(Memory.creeps[creepName])
        }
    }
}
