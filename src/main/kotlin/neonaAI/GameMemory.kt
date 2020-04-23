package neonaAI

import neonaAI.creep.FixedBody
import neonaAI.creep.CreepRole
import neonaAI.creep.WorkAction
import screeps.api.MOVE
import screeps.api.Memory
import screeps.api.RoomMemory
import screeps.api.WORK
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
val baseRoleMinimums: Map<RoomStage,Map<CreepRole,Int>> = mapOf<RoomStage,Map<CreepRole,Int>>(
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
val sourcerBody = FixedBody(arrayOf(WORK,WORK,WORK,WORK,WORK,MOVE))

var Memory.statusTimer: Int by memory { 0 }

var RoomMemory.roomStage by memory(RoomStage.HARVESTING)
var RoomMemory.stageUpdate: Int by memory { 0 }