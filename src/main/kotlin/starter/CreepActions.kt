package starter

import screeps.api.*
import screeps.api.structures.StructureController
import screeps.api.structures.StructureExtension
import screeps.api.structures.StructureSpawn

val ERR_VOID = ERR_INVALID_ARGS

fun Creep.harvest(): ScreepsReturnCode {
    val sources = room.find(FIND_SOURCES)
    var code: ScreepsReturnCode = ERR_VOID

    if(!memory.harvesting) {
        if(sources[0].pos.checkClear() || sources.size == 1) {
            memory.harvestSourceIndex = 0
        } else  {
            memory.harvestSourceIndex = 1
        }
        memory.harvesting = true
    } else if (carryCapacity == carry.energy) {
        memory.harvesting = false
        return code
    }
    code = harvest(sources[0])
    if (code == ERR_NOT_IN_RANGE) {
        moveTo(sources[0].pos)
    }

    return code
}

fun Creep.idleRally(): ScreepsReturnCode { //TODO: not implemented
    var code = ERR_VOID
    val rallyFlags =  room.find(FIND_FLAGS).filter { it.color == COLOR_GREEN && it.secondaryColor == COLOR_GREEN}
    if(rallyFlags.isNotEmpty()) {
        code = moveTo(rallyFlags[0])
    } else code = ERR_INVALID_TARGET

    return code
}

fun Creep.goTo(target: RoomObject): ScreepsReturnCode { //TODO: not implemented
    var code = ERR_VOID

    return code
}

fun Creep.goToByRoad(target: RoomObject): ScreepsReturnCode {
    var code = ERR_VOID

    return code
}

fun Creep.pause() {  //TODO: check for redundancy and reworking
    if (memory.pause < 10) {
        //blink slowly
        if (memory.pause % 3 != 0) say("\uD83D\uDEAC")
        memory.pause++
    } else {
        memory.pause = 0
        memory.role = Role.HARVESTER
    }
}

fun Creep.runActionBuild(): ScreepsReturnCode {
    var returnCode = ERR_VOID

    val targets = homeRoom.find(FIND_MY_CONSTRUCTION_SITES)
    if (targets.isNotEmpty()) {
        returnCode = build(targets[0])
        if (returnCode == ERR_NOT_IN_RANGE) {
            returnCode = moveTo(targets[0].pos)
        }
    } else returnCode = ERR_NOT_FOUND

    return returnCode
}

fun Creep.runActionUpgrade(): ScreepsReturnCode {  // TODO: clean up casting mess
    var returnCode = ERR_VOID

    returnCode = upgradeController(homeRoom.controller!!)

    if (returnCode == ERR_NOT_IN_RANGE) {
        returnCode = moveTo(homeRoom.controller!!.pos)
    }

    return returnCode
}

fun Creep.runActionHarvest(): ScreepsReturnCode {  // TODO: actually decide which source to use
    var returnCode = ERR_VOID

    val sources = room.find(FIND_SOURCES)
    if (sources.isNotEmpty()) {
        returnCode = harvest(sources[0])
        if (returnCode == ERR_NOT_IN_RANGE) {
            returnCode = moveTo(sources[0].pos)
        }
    } else returnCode = ERR_NOT_FOUND

    return returnCode
}

fun Creep.runActionRepair(): ScreepsReturnCode {
    var returnCode = ERR_VOID

    val repairable = room.find(FIND_STRUCTURES).filter {
        it.hits != it.hitsMax
                && it.structureType != STRUCTURE_RAMPART
                && it.structureType != STRUCTURE_WALL
                && it.structureType != STRUCTURE_CONTROLLER
    }
    if(repairable.isNotEmpty()) {
        returnCode = repair(repairable[0])
        if(returnCode == ERR_NOT_IN_RANGE) {
            returnCode = moveTo(repairable[0])
        }
    } else returnCode = ERR_NOT_FOUND

    return returnCode
}

fun Creep.runActionFill(): ScreepsReturnCode {  // TODO: redo, make better, etc
    var returnCode = ERR_VOID

    val targetSpawns = room.find(FIND_MY_STRUCTURES)
            .filter { (it.structureType == STRUCTURE_SPAWN) }
            .map { (it as StructureSpawn) }
            .filter { it.energy < it.energyCapacity }

    if (targetSpawns.isNotEmpty()) {
        returnCode = transfer(targetSpawns[0], RESOURCE_ENERGY)
        if (returnCode == ERR_NOT_IN_RANGE) {
            returnCode = moveTo(targetSpawns[0].pos)
        }
    } else {
        val targetExtensions = room.find(FIND_MY_STRUCTURES)
                .filter { (it.structureType == STRUCTURE_EXTENSION) }
                .map { (it as StructureExtension) }
                .filter { it.energy < it.energyCapacity }

        if (targetExtensions.isNotEmpty()) {
            returnCode = transfer(targetExtensions[0], RESOURCE_ENERGY)
            if (returnCode == ERR_NOT_IN_RANGE) {
                returnCode = moveTo(targetExtensions[0].pos)
            }
        }
    }

    return returnCode
}

/*
*  TODO: deprecate this stupid bullshit
* */

fun Creep.runActionUpgradeOld(controller: StructureController) {
    if(memory.harvesting || carry.energy == 0) {
        memory.harvesting = true

        val sources = room.find(FIND_SOURCES)
        if (harvest(sources[0]) == ERR_NOT_IN_RANGE) {
            moveTo(sources[0].pos)
        }
    }
    if(!memory.harvesting || carry.energy == carryCapacity) {
        memory.harvesting = false

        if (upgradeController(controller) == ERR_NOT_IN_RANGE) {
            moveTo(controller.pos)
        }
    }
}

fun Creep.runActionBuildOld(assignedRoom: Room = this.room) {
    if (memory.building && carry.energy == 0) {
        memory.building = false
        say("🔄 harvest")
    }
    if (!memory.building && carry.energy == carryCapacity) {
        memory.building = true
        say("🚧 build")
    }

    if (memory.building) {
        val targets = assignedRoom.find(FIND_MY_CONSTRUCTION_SITES)
        if (targets.isNotEmpty()) {
            if (build(targets[0]) == ERR_NOT_IN_RANGE) {
                moveTo(targets[0].pos)
            }
        } else {
            idleRally()
        }
    } else {
        val sources = room.find(FIND_SOURCES)
        if (harvest(sources[0]) == ERR_NOT_IN_RANGE) {
            moveTo(sources[0].pos)
        }
    }
}

fun Creep.runActionHarvestOld(fromRoom: Room = this.room, toRoom: Room = this.room) {
    if (carry.energy < carryCapacity) {
        harvest()
    } else {
        val targetSpawns = toRoom.find(FIND_MY_STRUCTURES)
                .filter { (it.structureType == STRUCTURE_SPAWN) }
                .map { (it as StructureSpawn) }
                .filter { it.energy < it.energyCapacity }

        if (targetSpawns.isNotEmpty()) {
            if (transfer(targetSpawns[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                moveTo(targetSpawns[0].pos)
            }
        } else {
            val targetExtensions = toRoom.find(FIND_MY_STRUCTURES)
                    .filter { (it.structureType == STRUCTURE_EXTENSION) }
                    .map { (it as StructureExtension) }
                    .filter { it.energy < it.energyCapacity }

            if (targetExtensions.isNotEmpty()) {
                if (transfer(targetExtensions[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    moveTo(targetExtensions[0].pos)
                }
            } else if(toRoom.energyAvailable == toRoom.energyCapacityAvailable && carry.energy == carryCapacity) {
                /*val roomSpawns = toRoom.find(FIND_MY_STRUCTURES)
                        .filter { (it.structureType == STRUCTURE_SPAWN) }
                        .map { (it as StructureSpawn) }
                if(roomSpawns.isNotEmpty()) moveTo(roomSpawns[0].pos)*/

                idleRally()
            }
        }

    }
}