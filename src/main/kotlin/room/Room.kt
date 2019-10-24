package room

import memory.ScreepsMemory
import screeps.api.Room
import screeps.api.structures.StructureSpawn
import structure.spawn.SpawnQueue
import structure.spawn.queueCache

val Room.Queue : SpawnQueue
    get() {
        if (!queueCache.containsKey(this.name)) queueCache[this.name] = SpawnQueue(this.name)
        return queueCache[this.name] as SpawnQueue
    }

