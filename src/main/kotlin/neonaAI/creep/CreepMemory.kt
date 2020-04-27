package neonaAI.creep

import screeps.api.*
import screeps.api.structures.Structure
import screeps.utils.memory.memory

var CreepMemory.role by memory(CreepRole.UNASSIGNED)
var CreepMemory.harvesting: Boolean by memory { false }
var CreepMemory.harvestSourceIndex: Int by memory { 0 }
var CreepMemory.target: String by memory { "" }  //ID of the target
var CreepMemory.targetX: Int by memory { -1 }
var CreepMemory.targetY: Int by memory { -1 }
var CreepMemory.targetRoom: String by memory { "" }
var CreepMemory.targetRanged: Boolean by memory { false }
var CreepMemory.task by memory(WorkAction.WAITING)
var CreepMemory.homeRoom: String by memory { "" }
var CreepMemory.taskTimeout: Int by memory { 0 }
var CreepMemory.cajole: Boolean by memory { false }
var CreepMemory.permanentTargetX: Int by memory { -1 }
var CreepMemory.permanentTargetY by memory { -1 }
var CreepMemory.permanentTargetRoom: String by memory { "" }
var CreepMemory.moveTimer: Int by memory { 0 }
var CreepMemory.movingByTimer: Boolean by memory { false }
var CreepMemory.lastMoved: Int by memory { 0 } //last tick moved on


val Creep.homeRoom: Room
    get() = (if(Game.rooms[memory.homeRoom] != null) Game.rooms[memory.homeRoom] else room) as Room

var Creep.permanentTarget: RoomPosition?
    get() {
        return if (memory.permanentTargetY != -1 && memory.permanentTargetY != -1 && memory.permanentTargetRoom != "")
            RoomPosition(memory.permanentTargetX, memory.permanentTargetY, memory.permanentTargetRoom)
        else null
    }
    set(value) {
        if (value != null) {
            memory.permanentTargetX = value.x
            memory.permanentTargetY = value.y
            memory.permanentTargetRoom = value.roomName
        }
    }

val Creep.hasTarget: Boolean
    get() = target != null

var Creep.target: RoomObject?
    get() = if(memory.target.isNotBlank()) Game.getObjectById(memory.target) else null
    set(value) {
        if (value != null && (value is Creep || value is Structure)) {
            targetPos = value.pos
            if (value is Creep)
                memory.target = value.id
            else if (value is Structure)
                memory.target = value.id
        } else {
            memory.target = ""
            targetPos = null
        }
    }

val Creep.hasTargetPos: Boolean
    get() = targetPos != null

var Creep.targetPos: RoomPosition?
    get() = RoomPosition(memory.targetX, memory.targetY, memory.targetRoom)
    set(value) {
        memory.target = ""
        if (value != null) {
            memory.targetX = value.x
            memory.targetY = value.y
            memory.targetRoom = value.roomName
        } else {
            memory.targetX = -1
            memory.targetY = -1
            memory.targetRoom = ""
        }
    }