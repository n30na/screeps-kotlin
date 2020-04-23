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

class RatioBody(val partRatios: Map<BodyPartConstant, Int>, var maxMultiplier: Int = 50/partRatios.values.sum(), val mixParts: Boolean = false): CreepBodyBuilder() {
    init {
        if(maxMultiplier > 50/partRatios.values.sum())
            maxMultiplier = 50/partRatios.values.sum()
    }

    override val minEnergyToSpawn: Int
        get() {
            var energy = 0

            for((part, count) in partRatios) {
                energy += (BODYPART_COST[part] ?: 0) * count
            }

            return energy
        }

    override fun genBody(energy: Int): Array<BodyPartConstant> {  // TODO: add body mixing
        val returnBody: MutableList<BodyPartConstant> = mutableListOf()

        if (energy >= minEnergyToSpawn) {
            val multiplier: Int = minOf(energy/minEnergyToSpawn, maxMultiplier)

            for((part, count) in partRatios)
                for(x in 1..count*multiplier) returnBody.add(part)
        }
        return returnBody.toTypedArray()
    }

    override fun minEnergyWithin(energy: Int): Int {
        return (energy/minEnergyToSpawn)*minEnergyToSpawn
    }
}

class MixedBody(val prefix: Array<BodyPartConstant>, val partRatios: Map<BodyPartConstant, Int>, val postfix: Array<BodyPartConstant>, var maxMultiplier: Int = 0): CreepBodyBuilder() {
    init {
        if(maxMultiplier == 0 || maxMultiplier > (50 - (prefix.size + postfix.size)) / partRatios.values.sum())
            maxMultiplier =  (50 - (prefix.size + postfix.size)) / partRatios.values.sum()
    }

    override val minEnergyToSpawn: Int
        get() {

        }
}

