package com.csi4999.lwjgl3.tests

import com.badlogic.gdx.math.RandomXS128
import com.csi4999.lwjgl3.systems.ai.SparseBrain
import java.util.*

fun main() {
    val r = RandomXS128(0)
    val s = SparseBrain(10, 5, 17, 0.5f, 0.1f, 0.25f, 0.7f, r)

    val input = FloatArray(10)
    Arrays.fill(input, 0f)
    println(s.run(input))

    // output shoudl be zeroes
}
