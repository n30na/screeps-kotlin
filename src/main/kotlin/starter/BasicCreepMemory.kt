package starter

import screeps.api.Creep
import screeps.api.CreepMemory
import screeps.utils.memory.memory


var CreepMemory.building: Boolean by memory { false }
var CreepMemory.pause: Int by memory { 0 }
var CreepMemory.role by memory(Role.UNASSIGNED)
var CreepMemory.harvesting: Boolean by memory { false }
var CreepMemory.harvestSourceIndex: Int by memory { 0 }
var CreepMemory.target: String by memory { "" }  //ID of the target
var CreepMemory.task: WorkAction by memory { WorkAction.WAITING }

fun Creep.WorkerCount(action: WorkAction): Int {

}

fun Creep.RoleCount(role: Role) {

}