package neonaAI.creep

import neonaAI.needsLabor
import neonaAI.requiredEnergy
import screeps.api.*


enum class Role {
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

}

fun Creep.roleCarry() {

}

fun Creep.roleReserver() {

}

fun Creep.roleDefender() {

}

fun Creep.roleAttacker() {

}