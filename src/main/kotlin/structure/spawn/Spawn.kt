package structure.spawn

import screeps.api.structures.StructureSpawn

val StructureSpawn.Queue : SpawnQueue
    get() = SpawnQueue(this.room.name)