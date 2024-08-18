/* © 2024 Wayne Zhu. All rights reserved. */
package com.wayne.xlauncher

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.wayne.xlauncher.service.LockScreenService
import com.wayne.xlauncher.ui.component.Home
import com.wayne.xlauncher.util.UITool

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //startService(Intent(this, XNotificationListenerService::class.java))
        startService(Intent(this, LockScreenService::class.java))

        UITool.init(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.doOnCreate(this)

        setContent {
            Home(viewModel = viewModel)
        }
    }
}
