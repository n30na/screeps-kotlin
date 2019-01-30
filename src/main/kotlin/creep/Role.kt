package creep.role

enum class CreepRole {
    NONE,WORK,SOURCE,CARRY,UPGRADE,DEFEND
}

open class Role(val type: CreepRole) {

}