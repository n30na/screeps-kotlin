package neonaAI


import neonaAI.creep.*
import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.utils.unsafe.delete
import screeps.utils.unsafe.jsObject

fun gameLoop() {
    val mainSpawn: StructureSpawn = Game.spawns["Spawn1"] ?: return

    //print room task stats every while
    Memory.statusTimer++
    if (Memory.statusTimer > 49) {
        Memory.statusTimer = 0
        println("fill: ${mainSpawn.room.requiredEnergy(WorkAction.FILLING)}  repair: ${mainSpawn.room.requiredEnergy(WorkAction.REPAIRING)}  build: ${mainSpawn.room.requiredEnergy(WorkAction.BUILDING)}  upgrade: ${mainSpawn.room.requiredEnergy(WorkAction.UPGRADING)}")
    }

    //delete memories of creeps that have passed away
    houseKeeping(Game.creeps)

    //make sure we have at least some creeps
    spawnCreeps(mainSpawn.room)

    for ((_, creep) in Game.creeps) {
        try {
            when (creep.memory.role) {
                Role.WORKER -> creep.roleWorker()
                Role.DEFENDER -> creep.roleDefender()
                else -> creep.say("\uD83D\uDEAC")
            }
        } catch(exception: dynamic) {
            println("Error executing ${creep.name}: $exception")
        }
    }

}

private fun spawnCreeps(room: Room) {

    val genericBody = RatioBody(mapOf(WORK to 1, CARRY to 1, MOVE to 1), 5)
    val genericFighter = RatioBody(mapOf(TOUGH to 1, MOVE to 1, ATTACK to 1))

    if(room.find(FIND_HOSTILE_CREEPS).isNotEmpty() && room.roleCount(Role.DEFENDER) < 2) {
        val role = Role.DEFENDER
        val newName = "${role.name}_${Game.time}"
        val spawns = room.availableSpawns()
        val body: CreepBodyBuilder =  genericFighter

        if(spawns.isNotEmpty() && body.minEnergyToSpawn <= room.energyAvailable) {
            val code = spawns[0].spawnCreep(body.genBody(room.energyAvailable), newName, options {
                memory = jsObject<CreepMemory> { this.role = role; this.homeRoom = room.name }
            })
            when (code) {
                OK -> console.log("spawning $newName with body ${body.genBody(room.energyAvailable)}")
                ERR_BUSY -> run { } // do nothing
                ERR_NOT_ENOUGH_ENERGY -> run {}
                else -> console.log("unhandled error code $code")
            }
        }
    } else if(room.roleCount(Role.WORKER) < 8) {
            val role = Role.WORKER
            val newName = "${role.name}_${Game.time}"
            val spawns = room.availableSpawns()
            val body: CreepBodyBuilder =
                if(room.creepCount() < 1) FixedBody(arrayOf(WORK, CARRY, MOVE))
                else genericBody

            if(spawns.isNotEmpty() && body.minEnergyWithin(room.energyCapacityAvailable) <= room.energyAvailable) {
                val code = spawns[0].spawnCreep(body.genBody(room.energyCapacityAvailable), newName, options {
                    memory = jsObject<CreepMemory> { this.role = role; this.homeRoom = room.name }
                })
                when (code) {
                    OK -> console.log("spawning $newName with body ${body.genBody(room.energyCapacityAvailable)}")
                    ERR_BUSY -> run { } // do nothing
                    ERR_NOT_ENOUGH_ENERGY -> run {}
                    else -> console.log("unhandled error code $code")
                }
            }

        }
    }

private fun houseKeeping(creeps: Record<String, Creep>) {
    for ((creepName, _) in Memory.creeps) {
        if (creeps[creepName] == null) {
            console.log("deleting obsolete memory entry for creep $creepName")
            delete(Memory.creeps[creepName])
        }
    }
}
