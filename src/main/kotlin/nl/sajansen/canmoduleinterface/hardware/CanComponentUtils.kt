package nl.sajansen.canmoduleinterface.hardware

object CanComponentUtils {

    fun calculatePercentage(component: CanComponent): Double {
        if (component.maxValue == component.minValue) {
            return 1.0
        }

        return (component.value - component.minValue).toDouble() / (component.maxValue - component.minValue)
    }

    fun getActiveBitValues(component: CanComponent): HashMap<Int, Boolean> {
        val activeBits = hashMapOf<Int, Boolean>()

        val mask = component.activeBitMask
        val values = component.value
        for (i in Long.SIZE_BITS - 1 downTo 0) {
            if (mask.shr(i).and(1) == 1L) {
                val bit = values.shr(i).and(1) == 1L
                activeBits[i] = bit
            }
        }
        return activeBits
    }
}