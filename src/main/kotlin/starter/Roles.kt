package starter

import screeps.api.*
import screeps.api.structures.StructureController
import screeps.api.structures.StructureExtension
import screeps.api.structures.StructureSpawn


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
    MOVING
}
val workPriorities = mapOf<WorkAction,Int>(
        WorkAction.HARVESTING to 1,
        WorkAction.REPAIRING to 2,
        WorkAction.BUILDING to 3,
        WorkAction.UPGRADING to 4,
        WorkAction.WAITING to 10
)

fun Creep.roleWorker() {

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