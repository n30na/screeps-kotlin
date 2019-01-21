package structure.spawn

import screeps.api.structures.StructureSpawn

var queueCache: MutableMap<String, SpawnQueue> = mutableMapOf()

val StructureSpawn.Queue : SpawnQueue
    get() {
        if (!queueCache.containsKey(room.name)) queueCache[room.name] = SpawnQueue(room.name)
        return queueCache[room.name] as SpawnQueue
    }

