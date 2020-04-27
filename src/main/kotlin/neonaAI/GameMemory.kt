package neonaAI

import neonaAI.creep.*
import screeps.api.*
import screeps.utils.memory.memory

enum class RoomStage {
    HARVESTING,
    SOURCING,
    STORING,
    LINKING
}

var workPriorities = mapOf<WorkAction,Int>(
        WorkAction.FILLING to 1,
        WorkAction.REPAIRING to 2,
        WorkAction.BUILDING to 3,
        WorkAction.UPGRADING to 4,
        WorkAction.WAITING to 10
)
val baseRoleMinimums: Map<RoomStage,Map<CreepRole,Int>> = mapOf(
    RoomStage.HARVESTING to mapOf(
            CreepRole.WORKER to 8
    ),
    RoomStage.SOURCING to mapOf(
            CreepRole.WORKER to 6,
            CreepRole.SOURCER to 0
    ),
    RoomStage.STORING to mapOf(
            CreepRole.WORKER to 4,
            CreepRole.SOURCER to 0,
            CreepRole.CARRY to 2
    )
)

val standardBody: Map<CreepRole, CreepBodyBuilder> = mapOf(
        CreepRole.WORKER to RatioBody(mapOf(WORK to 1, CARRY to 1, MOVE to 1), 6),
        CreepRole.DEFENDER to RatioBody(mapOf(TOUGH to 1, MOVE to 1, ATTACK to 1)),
        CreepRole.SOURCER to FixedBody(arrayOf(WORK, WORK, WORK, WORK, WORK, WORK, MOVE, MOVE)),
        CreepRole.CARRY to RatioBody(mapOf(CARRY to 2, MOVE to 1), 8),
        CreepRole.RESERVER to FixedBody(arrayOf(CLAIM, CLAIM, MOVE, MOVE)),
        CreepRole.UPGRADER to RatioBody(mapOf(WORK to 2, CARRY to 2, MOVE to 1), 5),
        CreepRole.UNASSIGNED to RatioBody(mapOf(WORK to 1, CARRY to 1, MOVE to 1), 5)
)

var Memory.statusTimer: Int by memory { 0 }

var RoomMemory.roomStage by memory(RoomStage.HARVESTING)
var RoomMemory.stageUpdate: Int by memory { 0 }