/* © 2024 Wayne Zhu. All rights reserved. */
package com.wayne.xlauncher.util

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import java.util.Arrays
import kotlin.properties.Delegates


object UITool {
    private var desity by Delegates.notNull<Float>()
    var screenHeight by Delegates.notNull<Int>()
    var statusBarHeight by Delegates.notNull<Int>()

    @RequiresApi(Build.VERSION_CODES.R)
    fun init(activity: Activity) {
        desity = activity.resources.displayMetrics.density

        screenHeight = activity.windowManager.currentWindowMetrics.bounds.height()
        statusBarHeight = activity.windowManager.currentWindowMetrics.windowInsets.getInsets(WindowInsets.Type.statusBars()).top
        val view = activity.window.decorView
        view.setOnApplyWindowInsetsListener { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsets.Type.systemBars())
            screenHeight = screenHeight - systemBarsInsets.top - systemBarsInsets.bottom
            insets
        }

    }

    fun dp2Pixel(i: Int): Float = i * desity
}
