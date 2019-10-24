package structure.spawn

import screeps.api.OK
import screeps.api.structures.SpawnOptions
import screeps.api.structures.StructureSpawn

var queueCache: MutableMap<String, SpawnQueue> = mutableMapOf()

val StructureSpawn.Queue : SpawnQueue
    get() {
        if (!queueCache.containsKey(room.name)) queueCache[room.name] = SpawnQueue(room.name)
        return queueCache[room.name] as SpawnQueue
    }

fun StructureSpawn.spawnNext() {
    var spawnOptions: SpawnOptions = object {} as SpawnOptions
    spawnOptions.dryRun = true

    if(spawning == null && spawnCreep(Queue.next.body.toTypedArray(),Queue.next.name, spawnOptions) == OK) {  // TODO: add cd to prevent thrashing
        spawnOptions.dryRun = false
        val toSpawn: SpawnQueueItem = Queue.pop()
        spawnCreep(toSpawn.body.toTypedArray(), toSpawn.name, spawnOptions)
    }
}
