package starter

import screeps.api.*
import screeps.utils.memory.memory


var CreepMemory.building: Boolean by memory { false }
var CreepMemory.pause: Int by memory { 0 }
var CreepMemory.role by memory(Role.UNASSIGNED)
var CreepMemory.harvesting: Boolean by memory { false }
var CreepMemory.harvestSourceIndex: Int by memory { 0 }
var CreepMemory.target: String by memory { "" }  //ID of the target
var CreepMemory.task by memory(WorkAction.WAITING)
var CreepMemory.homeRoom: String by memory { "" }


val Creep.homeRoom: Room
    get() = (if(Game.rooms[memory.homeRoom] != null) Game.rooms[memory.homeRoom] else room) as Room