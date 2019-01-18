import screeps.api.Memory
import screeps.api.MutableRecord
import screeps.api.set
import screeps.api.structures.Structure
import kotlin.reflect.KProperty

/**
 * Generic memory storage delegate, uses [DECIDE ON SOMETHING] as the class storage key
 */
class ScreepsMemory {
    fun set(path: String, value: Any) {

    }
    fun get(path: String) {

    }


    operator fun getValue(thisRef: Structure, property: KProperty<*>) {

    }
    operator fun setValue(thisRef: Structure, property: KProperty<*>, newValue: Any) {

    }


}