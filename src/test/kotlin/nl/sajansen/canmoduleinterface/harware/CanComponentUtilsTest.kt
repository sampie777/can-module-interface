package nl.sajansen.canmoduleinterface.harware

import nl.sajansen.canmoduleinterface.hardware.CanComponent
import nl.sajansen.canmoduleinterface.hardware.CanComponentUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CanComponentUtilsTest {
    @Test
    fun `test calculating percentage`() {
        val component = CanComponent(0)
        component.update(0)
        assertEquals(1.0, CanComponentUtils.calculatePercentage(component))

        component.update(10)
        assertEquals(1.0, CanComponentUtils.calculatePercentage(component))

        component.update(5)
        assertEquals(0.5, CanComponentUtils.calculatePercentage(component))

        component.update(6)
        assertEquals(0.6, CanComponentUtils.calculatePercentage(component))

        component.update(0)
        assertEquals(0.0, CanComponentUtils.calculatePercentage(component))

        component.update(10)
        assertEquals(1.0, CanComponentUtils.calculatePercentage(component))
    }

    @Test
    fun `test getting active bits`() {
        val component = CanComponent(0)
        component.update(0)
        component.update(5)
        component.update(9)

        val activeBits = CanComponentUtils.getActiveBitValues(component).toList()
        assertEquals(3, activeBits.size)

        assertEquals(0, activeBits[0].first)
        assertTrue(activeBits[0].second)

        assertEquals(2, activeBits[1].first)
        assertFalse(activeBits[1].second)

        assertEquals(3, activeBits[2].first)
        assertTrue(activeBits[2].second)
    }
}