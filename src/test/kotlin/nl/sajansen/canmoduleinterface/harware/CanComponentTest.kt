package nl.sajansen.canmoduleinterface.harware

import nl.sajansen.canmoduleinterface.hardware.CanComponent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CanComponentTest {
    @Test
    fun `test updating value updates the value and previous value`() {
        val component = CanComponent(0)
        component.value = 0
        assertNull(component.previousValue)

        // When
        component.update(10)
        assertEquals(10, component.value)
        assertEquals(0, component.previousValue)

        component.update(0)
        assertEquals(0, component.value)
        assertEquals(10, component.previousValue)
    }

    @Test
    fun `test updating value updates min value`() {
        val component = CanComponent(0)

        component.update(10)
        assertEquals(10, component.minValue)

        component.update(20)
        assertEquals(10, component.minValue)

        component.update(0)
        assertEquals(0, component.minValue)
    }

    @Test
    fun `test updating value updates max value`() {
        val component = CanComponent(0)

        component.update(10)
        assertEquals(10, component.maxValue)

        component.update(0)
        assertEquals(10, component.maxValue)

        component.update(20)
        assertEquals(20, component.maxValue)
    }

    @Test
    fun `test setting first value doesn't update bit mask`() {
        val component = CanComponent(0)

        assertEquals(0, component.activeBitMask)
        component.update(10)
        assertEquals(0, component.activeBitMask)
    }

    @Test
    fun `test updating value doesn't updates bit mask when value doesn't change`() {
        val component = CanComponent(0)

        component.update(1)
        assertEquals(0, component.activeBitMask)

        component.update(1)
        assertEquals(0, component.activeBitMask)

        component.update(1)
        assertEquals(0, component.activeBitMask)

        component.update(1)
        assertEquals(0, component.activeBitMask)
    }

    @Test
    fun `test updating value updates bit mask when value changes`() {
        val component = CanComponent(0)

        component.update(1)
        assertEquals(0, component.activeBitMask)

        component.update(1)
        assertEquals(0, component.activeBitMask)

        component.update(3)
        assertEquals(2, component.activeBitMask)

        component.update(2)
        assertEquals(3, component.activeBitMask)

        component.update(1)
        assertEquals(3, component.activeBitMask)
    }
}