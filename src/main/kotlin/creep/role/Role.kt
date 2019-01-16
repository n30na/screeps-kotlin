package creep.role

enum class CreepRole {
    WORK,SOURCE,CARRY,UPGRADE,DEFEND
}

open class Role(val type: CreepRole) {

}