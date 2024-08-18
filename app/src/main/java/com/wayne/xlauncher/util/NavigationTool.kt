/* © 2024 Wayne Zhu. All rights reserved. */
package com.wayne.xlauncher.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.wayne.xlauncher.data.AppItem
import com.wayne.xlauncher.service.LockScreenService


@SuppressLint("InvalidWakeLockTag")
fun jumpToApp(item: AppItem, activity: Activity) {
    if (item.packageName.isEmpty()) {
        item.deepLink?.let {
            if (it == "lock:") {
                if (AccessibilityTool.isGranted) {
                    val intent = Intent(activity, LockScreenService::class.java)
                    intent.action = LockScreenService.ACTION_LOCK
                    activity.startService(intent)
                } else {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    activity.startActivity(intent)
                }
                return
            }
            val intent =
                activity.packageManager.getLaunchIntentForPackage("com.android.contacts")?.apply {
                    data = Uri.parse(it)
                }

            intent.let { activity.startActivity(intent) }
        }
        return
    }
    val i = activity.packageManager.getLaunchIntentForPackage(item.packageName)
    activity.startActivity(i)
}
