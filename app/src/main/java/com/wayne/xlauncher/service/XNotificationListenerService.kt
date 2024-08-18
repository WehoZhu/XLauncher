/* © 2024 Wayne Zhu. All rights reserved. */
package com.wayne.xlauncher.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class XNotificationListenerService: NotificationListenerService() {
    override fun onCreate() {
        super.onCreate()
        Log.d("qwe","XNotificationListenerService created")
    }
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Log.d("qwe","onNotificationPosted: ${sbn?.packageName}")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d("qwe","onNotificationRemoved: ${sbn?.packageName}")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("qwe","onListenerConnected")
    }

}
