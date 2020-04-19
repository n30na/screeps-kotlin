package starter

import screeps.api.*
import screeps.api.structures.Structure
import screeps.api.structures.StructureExtension
import screeps.api.structures.StructureSpawn
import screeps.api.structures.StructureTower

fun Room.workerCount(action: WorkAction): Int {
    var count = 0

    for((_, creep) in Game.creeps) {
        if(creep.memory.homeRoom == name && creep.memory.task == action)
            count++
    }

    return count
}

fun Room.workerCarry(action: WorkAction): Int {
    var count = 0

    for((_, creep) in Game.creeps) {
        if(creep.memory.homeRoom == name && creep.memory.task == action)
            count += creep.carry.energy
    }

    return count
}

fun Room.roleCount(role: Role): Int {
    var count = 0

    for((_, creep) in Game.creeps) {
        if(creep.memory.homeRoom == name && creep.memory.role == role)
            count++
    }

    return count
}

fun Room.requiredEnergy(action: WorkAction): Int {
    var energy = 0

    when(action) {
        WorkAction.BUILDING -> {
            val constructionSites = find(FIND_MY_CONSTRUCTION_SITES)
            if (constructionSites.isNotEmpty()) {
                for(site in constructionSites) {
                    energy += site.progressTotal - site.progress
                }
            }
        }
        WorkAction.UPGRADING -> {
            energy = controller!!.progressTotal - controller!!.progress
        }
        WorkAction.REPAIRING -> {
            val repairable = find(FIND_STRUCTURES).filter {
                it.hits != it.hitsMax
                        && it.structureType != STRUCTURE_RAMPART
                        && it.structureType != STRUCTURE_WALL
                        && it.structureType != STRUCTURE_CONTROLLER
            }

            //println("repairable: ${repairable.count()}")
            if(repairable.isNotEmpty()) {
                for(structure in repairable)
                    energy += (structure.hitsMax - structure.hits)/ REPAIR_POWER
            }
        }
        WorkAction.FILLING -> {
            val holdEnergy = find(FIND_MY_STRUCTURES)
                    .filter { it.structureType == STRUCTURE_EXTENSION
                            || it.structureType == STRUCTURE_SPAWN
                            || it.structureType == STRUCTURE_TOWER }
            if(holdEnergy.isNotEmpty()) {
                for(structure in holdEnergy) {
                    when(structure.structureType) {
                        STRUCTURE_EXTENSION -> energy += (structure as StructureExtension).energyCapacity - structure.energy
                        STRUCTURE_SPAWN -> energy += (structure as StructureSpawn).energyCapacity - structure.energy
                        STRUCTURE_TOWER -> energy += (structure as StructureTower).energyCapacity - structure.energy
                    }
                }
            }
        }
        else -> {
            energy = 0
        }
    }

    return energy
}

fun Room.needsLabor(action: WorkAction): Boolean {
    return requiredEnergy(action) - workerCarry(action) > 0
}

fun Room.creepCount(): Int {
    var count = 0

    for((_, creep) in Game.creeps) {
        if(creep.memory.homeRoom == name)
            count++
    }

    return count
}

fun Room.availableSpawns(): List<StructureSpawn> {
    val spawns: MutableList<StructureSpawn> = ArrayList()

    for((_, spawn) in Game.spawns) {
        if(spawn.room.name == name && spawn.spawning == null)
            spawns.add(spawn)
    }

    return spawns
}