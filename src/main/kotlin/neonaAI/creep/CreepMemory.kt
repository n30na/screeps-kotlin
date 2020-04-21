package neonaAI.creep

import screeps.api.*
import screeps.utils.memory.memory

var CreepMemory.role by memory(Role.UNASSIGNED)
var CreepMemory.harvesting: Boolean by memory { false }
var CreepMemory.harvestSourceIndex: Int by memory { 0 }
var CreepMemory.target: String by memory { "" }  //ID of the target
var CreepMemory.task by memory(WorkAction.WAITING)
var CreepMemory.homeRoom: String by memory { "" }
var CreepMemory.taskTimeout: Int by memory { 0 }
var CreepMemory.cajole: Boolean by memory { false }


val Creep.homeRoom: Room
    get() = (if(Game.rooms[memory.homeRoom] != null) Game.rooms[memory.homeRoom] else room) as Room