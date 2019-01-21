package memory

import screeps.api.Creep
import screeps.api.Memory
import screeps.api.RoomMemory
import screeps.api.get
import screeps.api.structures.Structure
import screeps.api.structures.StructureSpawn
import kotlin.reflect.KProperty
import screeps.utils.memory.*

/**
 * Add structures property to room memory so we can put things in it
 */
var RoomMemory.structures: dynamic by memory { object{} }


/**
 * Generic memory storage delegate
 */
class ScreepsMemory<T>(private val default: T) {
    operator fun getValue(thisRef: Structure, property: KProperty<*>): T {
        val structureType = thisRef.structureType.toString()

        if (thisRef.room.memory.structures[structureType][property.name] == null ||
                thisRef.room.memory.structures[structureType][property.name] as? T == null) {
            setValue(thisRef, property, default)
        }

        return thisRef.room.memory.structures[structureType][property.name] as T
    }
    operator fun setValue(thisRef: Structure, property: KProperty<*>, newValue: T) {
        val structureType = thisRef.structureType.toString()

        if (thisRef.room.memory.structures[structureType] == null) {
            thisRef.room.memory.structures[structureType] = object {}
        }

        thisRef.room.memory.structures[structureType][property.name] = newValue
    }


    operator fun getValue(thisRef: StructureSpawn, property: KProperty<*>): T {
        val spawnName = thisRef.name

        if (Memory["spawns"][spawnName][property.name] == null ||
                Memory["spawns"][spawnName][property.name] as? T == null) {
            setValue(thisRef, property, default)
        }

        return Memory["spawns"][spawnName][property.name] as T
    }
    operator fun setValue(thisRef: StructureSpawn, property: KProperty<*>, newValue: T) {
        val spawnName = thisRef.name

        if (Memory["spawns"][spawnName] == null) {
            Memory["spawns"][spawnName] = object {}
        }

        Memory["spawns"][spawnName][property.name] = newValue
    }


    operator fun getValue(thisRef: Creep, property: KProperty<*>): T {
        val creepName = thisRef.name

        if (Memory["creeps"][creepName][property.name] == null ||
                Memory["creeps"][creepName][property.name] as? T == null) {
            setValue(thisRef, property, default)
        }

        return Memory["creeps"][creepName][property.name] as T
    }

    operator fun setValue(thisRef: Creep, property: KProperty<*>, newValue: T) {
        val creepName = thisRef.name

        if (Memory["creeps"][creepName] == null) {
            Memory["creeps"][creepName] = object {}
        }

        Memory["creeps"][creepName][property.name] = newValue
    }
}