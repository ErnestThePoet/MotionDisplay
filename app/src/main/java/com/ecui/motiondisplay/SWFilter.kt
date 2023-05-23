package com.ecui.motiondisplay

class SWFilter(private val size: Int) {
    private var filledSize = 0
    private var nextValueIndex = 0
    private val values: FloatArray

    init {
        values = FloatArray(size)
    }

    fun filter(value: Float): Float {
        values[nextValueIndex++] = value
        if (nextValueIndex == size) {
            nextValueIndex = 0
        }
        if (filledSize < size) {
            filledSize++
        }
        return calculateValueMean()
    }

    fun clear() {
        filledSize = 0
        nextValueIndex = 0
    }

    private fun calculateValueMean(): Float {
        var result = 0.0f
        for (i in 0 until filledSize) {
            result += values[i]
        }
        return result / filledSize
    }
}