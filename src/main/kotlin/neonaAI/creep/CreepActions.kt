package neonaAI.creep

import neonaAI.checkClear
import screeps.api.*
import screeps.api.structures.*
import screeps.utils.unsafe.jsObject

val ERR_VOID = ERR_INVALID_ARGS

fun Creep.harvest(): ScreepsReturnCode {
    val sources = room.find(FIND_SOURCES).filter { it.energy > 0 }
    var code: ScreepsReturnCode = ERR_VOID

    if(!memory.harvesting) {
        if(sources[0].pos.checkClear() || sources.size == 1) {
            memory.harvestSourceIndex = 0
        } else  {
            memory.harvestSourceIndex = 1
        }
        memory.harvesting = true
    } else if (store.getFreeCapacity(RESOURCE_ENERGY) == 0) {
        memory.harvesting = false
        return code
    }
    if(sources.isNotEmpty()) {
        code = harvest(sources[0])
        if (code != OK) {
            moveTo(sources[0].pos)
        }
    }

    return code
}

fun Creep.idleRally(): ScreepsReturnCode {
    val code: ScreepsReturnCode
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

fun Creep.goToByRoad(target: RoomObject): ScreepsReturnCode {//TODO: not implemented
    var code = ERR_VOID

    return code
}

fun Creep.runActionBuild(): ScreepsReturnCode {
    var returnCode: ScreepsReturnCode

    val target = pos.findClosestByPath(FIND_MY_CONSTRUCTION_SITES)
    if (target != null) {
        returnCode = build(target)
        if (returnCode == ERR_NOT_IN_RANGE) {
            returnCode = moveTo(target.pos)
        }
    } else returnCode = ERR_NOT_FOUND

    return returnCode
}

fun Creep.runActionUpgrade(): ScreepsReturnCode {  // TODO: clean up casting mess
    var returnCode: ScreepsReturnCode

    returnCode = upgradeController(homeRoom.controller!!)

    if (returnCode == ERR_NOT_IN_RANGE) {
        returnCode = moveTo(homeRoom.controller!!.pos)
    }

    return returnCode
}

fun Creep.runActionHarvest(): ScreepsReturnCode {  // TODO: actually decide which source to use
    var returnCode: ScreepsReturnCode

    val source = pos.findClosestByPath(FIND_SOURCES_ACTIVE)
    if (source != null) {
        returnCode = harvest(source)
        if (returnCode == ERR_NOT_IN_RANGE) {
            returnCode = moveTo(source.pos)
        }
    } else returnCode = ERR_NOT_FOUND

    return returnCode
}

fun Creep.runActionRepair(): ScreepsReturnCode {
    var returnCode: ScreepsReturnCode

    val toRepair = pos.findClosestByPath(FIND_STRUCTURES,
            jsObject { filter = { it.hits != it.hitsMax
            && it.structureType != STRUCTURE_RAMPART
            && it.structureType != STRUCTURE_WALL
            && it.structureType != STRUCTURE_CONTROLLER }})

    if(toRepair != null) {
        returnCode = repair(toRepair)
        if(returnCode == ERR_NOT_IN_RANGE) {
            returnCode = moveTo(toRepair)
        }
    } else returnCode = ERR_NOT_FOUND

    return returnCode
}

fun Creep.runActionFill(): ScreepsReturnCode {  // TODO: redo, make better, etc
    var returnCode = ERR_VOID

    val targetSpawns = room.find(FIND_MY_STRUCTURES)
            .filter { (it.structureType == STRUCTURE_SPAWN) }
            .map { (it as StructureSpawn) }
            .filter { it.store.getFreeCapacity(RESOURCE_ENERGY) > 0 }

    if (targetSpawns.isNotEmpty()) {
        returnCode = transfer(targetSpawns[0], RESOURCE_ENERGY)
        if (returnCode == ERR_NOT_IN_RANGE) {
            returnCode = moveTo(targetSpawns[0].pos)
        }
    } else {
        val extension = pos.findClosestByPath(FIND_MY_STRUCTURES, jsObject { filter = { it.structureType == STRUCTURE_EXTENSION && (it as StructureExtension).store.getFreeCapacity(RESOURCE_ENERGY) > 0 } })

        if (extension != null) {
            returnCode = transfer(extension as StructureExtension, RESOURCE_ENERGY)
            if (returnCode == ERR_NOT_IN_RANGE) {
                returnCode = moveTo(extension.pos)
            }
        } else {
            val targetTowers = room.find(FIND_MY_STRUCTURES)
                    .filter { (it.structureType == STRUCTURE_TOWER) }
                    .map { (it as StructureTower) }
                    .filter { it.store.getFreeCapacity(RESOURCE_ENERGY) > 0 }
            if(targetTowers.isNotEmpty()) {
                returnCode = transfer(targetTowers[0], RESOURCE_ENERGY)
                if (returnCode == ERR_NOT_IN_RANGE) {
                    returnCode = moveTo(targetTowers[0].pos)
                }
            }
        }
    }

    return returnCode
}

fun Creep.cajole() { // TODO: implement

}

fun Creep.startMoveByTimer() {

}

fun Creep.moveByTimer(): Boolean { //TODO: implement
    if(memory.moveTimer > 0) {
        val lTargetPos = targetPos
        if(lTargetPos != null) {
            if(moveTo(lTargetPos) == OK) memory.lastMoved = Game.time
            memory.moveTimer--
            memory.movingByTimer = true
            return true
        } else memory.moveTimer = 0
    } else {
        val lTargetPos = targetPos
        if(lTargetPos != null) {
            if((memory.targetRanged && pos.inRangeTo(lTargetPos, 3)) || (!memory.targetRanged && pos.isNearTo(lTargetPos))) {
                memory.movingByTimer = false
                return false
            } else {

            }
        } else {
            memory.movingByTimer = false
            return false
        }
    }
}