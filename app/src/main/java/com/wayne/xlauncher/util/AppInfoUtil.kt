package com.wayne.xlauncher.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.wayne.xlauncher.R
import com.wayne.xlauncher.data.AppItem

fun getInstalledApps(context: Context): List<AppItem> {
    val packageManager = context.packageManager
    val installedApps = mutableListOf<AppItem>()

    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
    for (resolveInfo in resolveInfoList) {
        val activityInfo = resolveInfo.activityInfo
        val appInfo = packageManager.getApplicationInfo(activityInfo.packageName, 0)
        val appName = resolveInfo.loadLabel(packageManager).toString()
        val appIcon = packageManager.getApplicationIcon(appInfo)
        installedApps.add(AppItem(activityInfo.packageName).apply {
            name = appName
            icon = appIcon
        })
    }

    return installedApps
}

fun getDefaultHotSeatApps(context: Context): List<String> {
    val packageManager = context.packageManager

    val intents = arrayOf(
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://aaaa")
        },
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:")
        },
    )
    val res = mutableListOf<String>()

    for (intent in intents) {
        val info = packageManager.queryIntentActivities(intent, 0)
        if (info.isEmpty()) continue
        res.add(info.first().activityInfo.packageName)
    }
    return res
}

fun getDialItem(context: Context): AppItem = AppItem("").apply {
    deepLink = "tel:"
    icon = ContextCompat.getDrawable(context, R.drawable.phone)
}

fun getLockItem(context: Context): AppItem = AppItem("").apply {
    deepLink = "lock:"
    icon = ContextCompat.getDrawable(context, R.drawable.lock)
}