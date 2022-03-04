package com.tesisunl.appod

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val list = listOf<Int>(1,2,3,4,5,6)
        for(i in 0 until list.size)
            println(i)
    }
}