package neonaAI.creep

import neonaAI.container
import neonaAI.needsLabor
import neonaAI.requiredEnergy
import neonaAI.sourcer
import screeps.api.*
import screeps.api.structures.StructureContainer


enum class CreepRole {
    UNASSIGNED,
    UPGRADER,
    WORKER,
    SOURCER,
    CARRY,
    RESERVER,
    DEFENDER
}
enum class WorkAction {
    HARVESTING,
    WAITING,
    BUILDING,
    UPGRADING,
    CARRYING,
    SOURCING,
    REPAIRING,
    ATTACKING,
    RESERVING,
    MOVING,
    WALLREPAIR,
    FILLING
}


fun Creep.preRole() {

}

fun Creep.postRole() {

}


fun Creep.roleWorker() {
    memory.taskTimeout++
    if(store.getUsedCapacity(RESOURCE_ENERGY) == 0 || (memory.task != WorkAction.HARVESTING && memory.taskTimeout > 20 && homeRoom.requiredEnergy(memory.task as WorkAction) == 0)) {
        memory.task = WorkAction.HARVESTING
        memory.taskTimeout = 0
    } else if (memory.task == WorkAction.HARVESTING && store.getFreeCapacity() == 0) {
        // TODO: make this use priorities instead of being hard coded
        if(homeRoom.controller!!.ticksToDowngrade < 1000)
            memory.task = WorkAction.UPGRADING
        else if (homeRoom.needsLabor(WorkAction.FILLING))
            memory.task = WorkAction.FILLING
        else if (homeRoom.requiredEnergy(WorkAction.REPAIRING) > 99 && homeRoom.needsLabor(WorkAction.REPAIRING))
            memory.task = WorkAction.REPAIRING
        else if (homeRoom.needsLabor(WorkAction.BUILDING))
            memory.task = WorkAction.BUILDING
        else
            memory.task = WorkAction.UPGRADING

        memory.taskTimeout = 0
        say(memory.task.toString())
    }


    when(memory.task) {
        WorkAction.HARVESTING -> runActionHarvest()
        WorkAction.REPAIRING -> runActionRepair()
        WorkAction.UPGRADING -> runActionUpgrade()
        WorkAction.BUILDING -> runActionBuild()
        WorkAction.FILLING -> runActionFill()
        WorkAction.WAITING -> say("\uD83D\uDEAC")
        else -> {  }
    }
}

fun Creep.roleSourcer() {
    if(permanentTarget == null) {
        val sources = room.find(FIND_SOURCES)
        for (source in sources) {
            val container = source.container
            if(source.sourcer == null && container != null) {
                permanentTarget = container.pos
                break
            }
        }
        if (permanentTarget == null) {
            idleRally()
            return
        }
    }
    val target = permanentTarget
    if (target != null) {
        if (pos.inRangeTo(target, 0)) {
            val source = pos.findInRange(FIND_SOURCES_ACTIVE, 1)
            if(source.isNotEmpty()) {
                harvest(source.first())
            }
        } else moveTo(target)
    }
}

fun Creep.roleCarry() { // TODO: implement


}

fun Creep.roleReserver() { //TODO: impement

}

fun Creep.roleDefender() {
    val target = pos.findClosestByPath(FIND_HOSTILE_CREEPS)

    if(target != null)
        if(pos.inRangeTo(target.pos,1)) {
            attack(target)
        } else {
            moveTo(target)
        }
    else
        idleRally()
}

fun Creep.roleAttacker() { // TODO: implement

}