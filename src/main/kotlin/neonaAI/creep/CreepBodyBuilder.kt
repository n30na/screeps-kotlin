package neonaAI.creep

import screeps.api.BODYPART_COST
import screeps.api.BodyPartConstant
import screeps.api.get

abstract class CreepBodyBuilder {
    abstract val minEnergyToSpawn: Int

    abstract fun minEnergyWithin(energy: Int): Int
    abstract fun genBody(energy: Int): Array<BodyPartConstant>

    fun canSpawn(energy: Int): Boolean {
        return minEnergyWithin(energy) != 0
    }

    companion object {
        fun calcBodyCost(body: Array<BodyPartConstant>): Int {
            var cost = 0

            for(part in body) cost += BODYPART_COST[part] ?: 0

            return cost
        }
    }
}

class FixedBody(public val body: Array<BodyPartConstant>): CreepBodyBuilder() {
    override val minEnergyToSpawn = calcBodyCost(body)
    val cost = minEnergyToSpawn

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

    fun genBodyByMultiplier(multiplier: Int): Array<BodyPartConstant> {
        val returnBody: MutableList<BodyPartConstant> = mutableListOf()

        for((part, count) in partRatios)
            for(x in 1..count*multiplier) returnBody.add(part)

        return returnBody.toTypedArray()
    }

    fun genBody(): Array<BodyPartConstant> = genBody(minEnergyToSpawn)

    override fun minEnergyWithin(energy: Int): Int {
        return (energy/minEnergyToSpawn)*minEnergyToSpawn
    }
}

class MixedBody(val prefix: FixedBody, val mainBody: RatioBody, val postfix: FixedBody, var maxMultiplier: Int = 0): CreepBodyBuilder() {
    init {
        maxMultiplier = minOf(maxMultiplier, mainBody.maxMultiplier)
        if(maxMultiplier == 0 || maxMultiplier > (50 - (prefix.body.size + postfix.body.size)) / mainBody.partRatios.values.sum())
            maxMultiplier =  (50 - (prefix.body.size + postfix.body.size)) / mainBody.partRatios.values.sum()
    }
    constructor(prefix: Array<BodyPartConstant>, mainBody: Map<BodyPartConstant,Int>, postfix: Array<BodyPartConstant>, maxMultiplier: Int = 0)
            : this(FixedBody(prefix), RatioBody(mainBody, maxMultiplier), FixedBody(postfix), maxMultiplier)

    override val minEnergyToSpawn: Int
        get() = prefix.cost + mainBody.minEnergyToSpawn + postfix.cost

    override fun minEnergyWithin(energy: Int): Int {
        return ( (energy - prefix.cost - postfix.cost) / minEnergyToSpawn) * minEnergyToSpawn + prefix.cost + postfix.cost
    }

    override fun genBody(energy: Int): Array<BodyPartConstant> {
        val returnBody: MutableList<BodyPartConstant> = mutableListOf()
        val usableEnergy = energy - prefix.cost - postfix.cost

        if(energy >= minEnergyToSpawn) {
            val multiplier = minOf(usableEnergy/mainBody.minEnergyToSpawn, maxMultiplier)
            returnBody.addAll(prefix.body)
            returnBody.addAll(mainBody.genBodyByMultiplier(multiplier))
            returnBody.addAll(postfix.body)
        }

        return returnBody.toTypedArray()
    }
}