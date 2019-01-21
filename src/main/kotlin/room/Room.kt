package room

import screeps.api.Room
import screeps.api.structures.StructureSpawn
import structure.spawn.SpawnQueue

val Room.Queue : SpawnQueue
    get() = SpawnQueue(this.name)