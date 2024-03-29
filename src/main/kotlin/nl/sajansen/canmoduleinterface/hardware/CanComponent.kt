package nl.sajansen.canmoduleinterface.hardware

import java.util.*
import kotlin.math.max
import kotlin.math.min

class CanComponent(val id: Int) {
    var previousValue: Long? = null
    var value: Long = 0

    var minValue: Long = Long.MAX_VALUE
    var maxValue: Long = 0

    var activeBitMask: Long = 0

    var lastUpdateTime: Date = Date()

    fun update(value: Long) {
        minValue = min(minValue, value)
        maxValue = max(maxValue, value)

        updateBitMask(value)

        lastUpdateTime = Date()
        previousValue = this.value
        this.value = value
    }

    private fun updateBitMask(value: Long) {
        if (previousValue == null) {
            return
        }

        activeBitMask = activeBitMask.or(this.value.xor(value))
    }
}