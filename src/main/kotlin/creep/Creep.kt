package creep

import screeps.api.*

class CreepBody(var body: MutableList<BodyPartConstant> = mutableListOf()) {

    operator fun get(index: Int):BodyPartConstant = body[index]

    fun add(part: BodyPartConstant, count: Int) {
        repeat(count) {
            body.add(part)
        }
    }
    fun insert(part: BodyPartConstant, count: Int, pos: Int) {
        repeat(count) {
            body.add(pos, part)
        }
    }
    fun partCount(part: BodyPartConstant): Int = body.filter { it == part }.count()

    fun has(part: BodyPartConstant): Boolean = partCount(part) > 0

    fun toBuilder(): CreepBodyBuilder =
            CreepBodyBuilder(
                    work = partCount(WORK),
                    carry = partCount(CARRY),
                    move =  partCount(MOVE),
                    attack = partCount(ATTACK),
                    rangedAttack = partCount(RANGED_ATTACK),
                    tough = partCount(TOUGH),
                    heal = partCount(HEAL),
                    claim = partCount(CLAIM)
            )

    fun toArray(): Array<BodyPartConstant> {
        return body.toTypedArray()
    }
}

class CreepBodyBuilder(
        var work: Int = 0,
        var carry: Int = 0,
        var move: Int = 0,
        var attack: Int = 0,
        var rangedAttack: Int = 0,
        var tough: Int = 0,
        var heal: Int = 0,
        var claim: Int = 0) {
    val count: Int get() = work+carry+move+attack+rangedAttack+tough+heal+claim

    operator fun get(part: BodyPartConstant): Int =
        when (part) {
            WORK -> work
            CARRY -> carry
            MOVE -> move
            ATTACK -> attack
            RANGED_ATTACK -> rangedAttack
            TOUGH -> tough
            HEAL -> heal
            CLAIM -> claim
            else -> 0
        }
    operator fun set(part: BodyPartConstant, value: Int) {
        when(part) {
            WORK -> work = value
            CARRY -> carry = value
            MOVE -> move = value
            ATTACK -> attack = value
            RANGED_ATTACK -> rangedAttack = value
            TOUGH -> tough = value
            HEAL -> heal = value
            CLAIM -> claim = value
        }
    }
    fun build(buildFunction: (body: CreepBodyBuilder) -> CreepBody): CreepBody =
            buildFunction(this)
    fun build(): CreepBody = build(arrayOf(TOUGH, CARRY, WORK, CLAIM, ATTACK, RANGED_ATTACK, MOVE, HEAL))
    fun build(partOrder: Array<BodyPartConstant>): CreepBody {
        var body = CreepBody()
        for (part in partOrder) {
            body.add(part,this[part])
        }
        return body
    }
}

