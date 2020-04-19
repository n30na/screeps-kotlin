package starter

import screeps.api.*


enum class Role {
    UNASSIGNED,
    HARVESTER,
    BUILDER,
    UPGRADER,
    WORKER,
    SOURCER,
    CARRY,
    RESERVER
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
    if(carry.energy == 0) {
        memory.task = WorkAction.HARVESTING
    } else if (memory.task == WorkAction.HARVESTING && carry.energy == carryCapacity) {
        // TODO: make this use priorities instead of being hard coded
        if (homeRoom.needsLabor(WorkAction.FILLING))
            memory.task = WorkAction.FILLING
        else if (homeRoom.requiredEnergy(WorkAction.REPAIRING) > 49 )
            memory.task = WorkAction.REPAIRING
        else if (homeRoom.needsLabor(WorkAction.BUILDING))
            memory.task = WorkAction.BUILDING
        else
            memory.task = WorkAction.UPGRADING

        say(memory.task.toString())
    }

    when(memory.task) {
        WorkAction.HARVESTING -> runActionHarvest()
        WorkAction.REPAIRING -> runActionRepair()
        WorkAction.UPGRADING -> runActionUpgrade()
        WorkAction.BUILDING -> runActionBuild()
        WorkAction.FILLING -> runActionFill()
        WorkAction.WAITING -> pause()  // TODO: rework
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