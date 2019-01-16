package creep

import screeps.api.Creep

class CreepBody() {
    var body: List<Creep.BodyPart> = listOf()

    constructor(newBody: List<Creep.BodyPart>) : this() {
        body = newBody
    }
}