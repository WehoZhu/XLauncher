/* © 2024 Wayne Zhu. All rights reserved. */
package com.wayne.xlauncher.util

import android.content.Context
import android.content.Intent
import android.provider.Settings

// request access to notification listener
fun requestNotificationAccess(context: Context) {
    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
}
