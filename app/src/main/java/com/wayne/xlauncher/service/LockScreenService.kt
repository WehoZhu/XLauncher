package com.wayne.xlauncher.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.wayne.xlauncher.util.AccessibilityTool

class LockScreenService : AccessibilityService() {
    companion object {
        const val ACTION_LOCK = "lock_screen"
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_LOCK) {
            performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onServiceConnected() {
        AccessibilityTool.isGranted = true
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_HAPTIC
            notificationTimeout = 100
            packageNames = arrayOf("com.wayne.xlauncher")
        }
        this.serviceInfo = info
    }
}