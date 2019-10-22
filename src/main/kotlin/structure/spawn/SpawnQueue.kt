package structure.spawn

import screeps.api.*
import screeps.utils.unsafe.delete

inline fun keys(json: dynamic) = js("Object").keys(json).unsafeCast<Array<String>>()


interface StoredSpawnQueueItem {
    var priority: Int
    var body: Array<BodyPartConstant>
    var tickAddedOn: Int
}

data class SpawnQueueItem(var name: String, var priority: Int, var body: List<BodyPartConstant>, val tickAddedOn: Int = Game.time, var room: String = "") {

    constructor(name: String, source: StoredSpawnQueueItem) : this(name, source.priority, source.body.toList(), source.tickAddedOn)



    fun toObject(): dynamic {
        var newObject: dynamic = object {}
        newObject.priority = priority
        newObject.body = body.toTypedArray()
        newObject.tickAddedOn = tickAddedOn
        return newObject
    }
    fun delete() {
        delete(Memory["rooms"][room]["queue"][name])
    }
    fun save() {
        Memory["rooms"][room]["queue"][name] = toObject()
    }
}

class SpawnQueue(val roomName: String) {
    private var cachedQueue: MutableList<SpawnQueueItem> = mutableListOf()
    private var cacheInitialized = false
    var sorted = false

    val queue: List<SpawnQueueItem>
        get() {
            if (cacheInitialized) return cachedQueue
            cachedQueue =  mutableListOf()
            for (name in keys(Memory["rooms"][roomName]["queue"])) {
                if(Memory["rooms"][roomName]["queue"][name] as? StoredSpawnQueueItem == null) {
                    delete(Memory["rooms"][roomName]["queue"][name])
                } else {
                    cachedQueue.add( SpawnQueueItem(name, Memory["rooms"][roomName]["queue"][name] as StoredSpawnQueueItem) )
                }
            }
            cacheInitialized = true
            return cachedQueue.toList()
        }

    private fun sort() {
        if(!sorted) {
            cachedQueue = queue.sortedWith(compareBy(SpawnQueueItem::priority,SpawnQueueItem::tickAddedOn)).toMutableList()
        }
        sorted = true
    }

    operator fun get(name: String): SpawnQueueItem =
            queue.find {it.name == name} ?: throw Exception("out of bounds")

    operator fun get(index: Int): SpawnQueueItem {
        sort()
        return queue[index]
    }

    fun contains(name: String): Boolean =
            queue.find {it.name == name} != null

    fun add(item: SpawnQueueItem) {
        sorted = false;
    }
}