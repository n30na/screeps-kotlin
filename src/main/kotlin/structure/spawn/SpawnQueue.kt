package structure.spawn

import creep.CreepBody
import screeps.api.BodyPartConstant
import screeps.api.CreepMemory
import screeps.utils.memory.memory

data class SpawnQueueItem(var name: String, var priority: Int, var body: List<BodyPartConstant>, val tickAddedOn: Int)

class SpawnQueue(val roomName: String) {


}