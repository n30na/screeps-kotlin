package starter


import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.utils.unsafe.delete
import screeps.utils.unsafe.jsObject


private val minPopulations = arrayOf(Role.HARVESTER to 3, Role.UPGRADER to 1, Role.BUILDER to 3, Role.UPGRADER to 4)

fun gameLoop() {
    val mainSpawn: StructureSpawn = Game.spawns["Spawn1"] ?: return

    //delete memories of creeps that have passed away
    houseKeeping(Game.creeps)

    //make sure we have at least some creeps
    spawnCreeps(minPopulations, Game.creeps.values, mainSpawn)

    //spawn a big creep if we have plenty of energy
    /*for ((_, room) in Game.rooms) {
        if (room.energyAvailable > 549) {
            mainSpawn.spawnCreep(
                arrayOf(
                    WORK,
                    WORK,
                    WORK,
                    WORK,
                    CARRY,
                    MOVE,
                    MOVE
                ),
                "HarvesterBig_${Game.time}",
                options {
                    memory = jsObject<CreepMemory> {
                        this.role = Role.UPGRADER
                    }
                }
            )
        }
    }*/

    for ((_, creep) in Game.creeps) {
        when (creep.memory.role) {
            Role.HARVESTER -> creep.runActionHarvest()
            Role.BUILDER -> creep.runActionBuild()
            Role.UPGRADER -> creep.runActionUpgrade(mainSpawn.room.controller!!)
            else -> creep.pause()
        }
    }

}

private fun spawnCreeps(
    minPopulations: Array<Pair<Role, Int>>,
    creeps: Array<Creep>,
    spawn: StructureSpawn
) {
    val bodyBig: Array<BodyPartConstant> = arrayOf<BodyPartConstant>(WORK,WORK,WORK,CARRY,CARRY,MOVE,MOVE, MOVE)
    val bodySmall: Array<BodyPartConstant> = arrayOf<BodyPartConstant>(WORK,CARRY, MOVE)

    for ((role, min) in minPopulations) {
        val current = creeps.filter { it.memory.role == role }
        if (current.size < min) {
            val newName = "${role.name}_${Game.time}"
            val code = spawn.spawnCreep(bodyBig, newName, options {
                memory = jsObject<CreepMemory> { this.role = role }
            })

            when (code) {
                OK -> console.log("spawning $newName with body $bodyBig")
                ERR_BUSY -> run { } // do nothing
                ERR_NOT_ENOUGH_ENERGY -> run {
                    if (spawn.room.energyAvailable >= 200) {
                        spawn.spawnCreep(bodySmall, newName, options {
                            memory = jsObject<CreepMemory> { this.role = role }
                        })
                        console.log("spawning $newName with body $bodySmall")

                    }
                }
                else -> console.log("unhandled error code $code")
            }




        }
    }
}

private fun houseKeeping(creeps: Record<String, Creep>) {
    for ((creepName, _) in Memory.creeps) {
        if (creeps[creepName] == null) {
            console.log("deleting obsolete memory entry for creep $creepName")
            delete(Memory.creeps[creepName])
        }
    }
}
