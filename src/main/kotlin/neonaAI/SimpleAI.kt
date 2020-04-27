package neonaAI


import neonaAI.creep.*
import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.api.structures.StructureTower
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
            creep.preRole()
            when (creep.memory.role) {
                CreepRole.WORKER -> creep.roleWorker()
                CreepRole.DEFENDER -> creep.roleDefender()
                CreepRole.SOURCER -> creep.roleSourcer()
                CreepRole.CARRY -> creep.roleCarry()
                else -> creep.say("\uD83D\uDEAC")
            }
            creep.postRole()
        } catch(exception: dynamic) {
            println("Error executing ${creep.name}: $exception")
        }
    }

    val towers = mainSpawn.room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_TOWER }.map { it as StructureTower }

    for(tower in towers) {
        if (tower.store.getUsedCapacity(RESOURCE_ENERGY) >= 10) {
            val targetHostile = tower.pos.findClosestByRange(FIND_HOSTILE_CREEPS)
            if (targetHostile != null) {
                tower.attack(targetHostile)
            } else {
                val targetFriendly = tower.pos.findClosestByRange(FIND_MY_CREEPS, opts = jsObject { filter = { it.hits < it.hitsMax } })
                if (targetFriendly != null) {
                    tower.heal(targetFriendly)
                }
            }
        }
    }

}

private fun spawnCreeps(room: Room) {

    val genericBody = RatioBody(mapOf(WORK to 1, CARRY to 1, MOVE to 1), 6)
    val genericFighter = RatioBody(mapOf(TOUGH to 1, MOVE to 1, ATTACK to 1))

    if(room.find(FIND_HOSTILE_CREEPS).isNotEmpty() && room.roleCount(CreepRole.DEFENDER) < 2) {
        room.spawnCreep(genericFighter, CreepRole.DEFENDER)
    } else if(room.creepCount() < 1) {
        room.spawnCreep(FixedBody(arrayOf(WORK,CARRY,MOVE)),CreepRole.WORKER)
    } else  {
        val minimums = baseRoleMinimums[room.memory.roomStage]

        if (minimums != null) {
            for ((role, count) in minimums) {
                if (room.roleCount(role) < count || (role == CreepRole.SOURCER && room.roleCount(CreepRole.SOURCER) < room.find(FIND_SOURCES).size)) {
                    room.spawnCreep(standardBody[role] ?:FixedBody(arrayOf()), role)
                    break
                }
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
