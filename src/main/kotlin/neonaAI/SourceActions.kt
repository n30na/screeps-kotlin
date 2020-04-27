package neonaAI

import neonaAI.creep.permanentTargetRoom
import neonaAI.creep.permanentTargetX
import neonaAI.creep.permanentTargetY
import screeps.api.*
import screeps.api.structures.StructureContainer
import screeps.utils.memory.memory
import screeps.utils.toMap
import screeps.utils.unsafe.jsObject

val Source.container: StructureContainer?
    get() = pos.findClosestByRange(FIND_STRUCTURES, opts = jsObject { filter = { it.structureType == STRUCTURE_CONTAINER} }) as StructureContainer

val Source.sourcer: Creep?
    get() {
        val sourceContainer = container
        if(sourceContainer != null) {
            val creeps = Game.creeps.toMap().filter { (_, it) -> it.memory.permanentTargetX == sourceContainer.pos.x && it.memory.permanentTargetY == sourceContainer.pos.y && it.memory.permanentTargetRoom == sourceContainer.pos.roomName }
            if(creeps.isNotEmpty()) {
                return creeps.values.first()
            } else return null
        } else return null
    }

//fun Source.costFrom(pos: RoomPosition): Int {  //TODO: implement
//    var cost: Int = 0
//
//    return cost
//}

//val Source.slotCount: Int
//        get() {
//            var slots: Int = 0
//            val
//
//            return slots
//        }