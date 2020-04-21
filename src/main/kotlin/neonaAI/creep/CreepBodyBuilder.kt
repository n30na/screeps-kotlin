package neonaAI.creep

import screeps.api.BODYPART_COST
import screeps.api.BodyPartConstant
import screeps.api.get

abstract class CreepBodyBuilder {
    abstract val minEnergyToSpawn: Int

    abstract fun minEnergyWithin(energy: Int): Int
    abstract fun genBody(energy: Int): Array<BodyPartConstant>

    fun calcBodyCost(body: Array<BodyPartConstant>): Int {
        var cost = 0

        for(part in body) cost += BODYPART_COST[part] ?: 0

        return cost
    }
    fun canSpawn(energy: Int): Boolean {
        return minEnergyWithin(energy) != 0
    }
}

class FixedBody(val body: Array<BodyPartConstant>): CreepBodyBuilder() {
    override val minEnergyToSpawn = calcBodyCost(body)

    override fun genBody(energy: Int): Array<BodyPartConstant> {
        return if (energy >= minEnergyToSpawn)
            body
        else
            emptyArray()
    }

    override fun minEnergyWithin(energy: Int): Int {
        return if (energy >= minEnergyToSpawn)
            minEnergyToSpawn
        else
            0
    }
}

class RatioBody(val partRatios: Map<BodyPartConstant, Int>, val maxMultiplier: Int = 0): CreepBodyBuilder() {
    override val minEnergyToSpawn: Int
        get() {
            var energy = 0

            for((part, count) in partRatios) {
                energy += (BODYPART_COST[part] ?: 0) * count
            }

            return energy
        }

    override fun genBody(energy: Int): Array<BodyPartConstant> {
        val returnBody: MutableList<BodyPartConstant> = mutableListOf()

        if (energy >= minEnergyToSpawn) {
            val multiplier: Int =
                    if(maxMultiplier != 0 && energy/minEnergyToSpawn > maxMultiplier) maxMultiplier
                    else energy/minEnergyToSpawn

            for((part, count) in partRatios)
                for(x in 1..count*multiplier) returnBody.add(part)
        }
        return returnBody.toTypedArray()
    }

    override fun minEnergyWithin(energy: Int): Int {
        return (energy/minEnergyToSpawn)*minEnergyToSpawn
    }
}