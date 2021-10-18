package com.mattg.rovergallery

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mattg.rovergallery.utils.toLocalDate

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.mattg.rovergallery", appContext.packageName)
    }

    @Test
    fun stringToDateMilli(){
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val failString = ""
        val failStringTwo = "2020-1-"
        val passString = "2020-04-12"
        val expected = LocalDate.of(2020, 4, 12)
        assertEquals(null, failString.toLocalDate())
        assertEquals(null, failStringTwo.toLocalDate())
        assertEquals(expected, passString.toLocalDate())
    }

}