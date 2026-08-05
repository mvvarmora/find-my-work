package com.example.findmywork

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RobolectricSmokeTest {
    @Test
    fun appContext_packageName_isCorrect() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("com.example.findmywork", ctx.packageName)
    }
}
