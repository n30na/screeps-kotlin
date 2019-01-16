package structure.spawn

import creep.CreepBody
import screeps.api.CreepMemory
import screeps.utils.memory.memory

data class SpawnQueueItem(var name: String, var priority: Int, var body: CreepBody, val tickAddedOn: Int)

class SpawnQueue() {
    var queueItems = listOf<SpawnQueueItem>()
    
}