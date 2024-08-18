/* © 2024 Wayne Zhu. All rights reserved. */
package com.wayne.xlauncher

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wayne.xlauncher.data.AppItem
import com.wayne.xlauncher.data.HiddenItem
import com.wayne.xlauncher.data.addHiddenItem
import com.wayne.xlauncher.data.getAllHiddenItem
import com.wayne.xlauncher.util.UITool
import com.wayne.xlauncher.util.getDefaultHotSeatApps
import com.wayne.xlauncher.util.getDialItem
import com.wayne.xlauncher.util.getInstalledApps
import com.wayne.xlauncher.util.getLockItem
import com.wayne.xlauncher.util.jumpToApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor
import kotlin.properties.Delegates

class MainViewModel : ViewModel() {
    private val _pageData: MutableLiveData<List<List<AppItem>>> = MutableLiveData(listOf())
    val data: MutableLiveData<List<List<AppItem>>>
        get() = _pageData

    private var appCntPerPage by Delegates.notNull<Int>()

    private val _hotSeats: MutableLiveData<List<AppItem>> = MutableLiveData(listOf())
    val hotSeats: MutableLiveData<List<AppItem>>
        get() = _hotSeats

    val currentPage: MutableLiveData<Int> = MutableLiveData(0)
    val showIndicator: MutableLiveData<Boolean> = MutableLiveData(false)

    val showAppPopup = MutableLiveData(false)
    val showPatternDialog = MutableLiveData(false)
    private lateinit var hiddenList: MutableList<HiddenItem>
    private lateinit var allAppsList: List<AppItem>

    fun doOnCreate(activity: MainActivity) {
        viewModelScope.launch {
            allAppsList = withContext(Dispatchers.Default) {
                getInstalledApps(activity)
            }
            val hots = withContext(Dispatchers.Default) {
                getDefaultHotSeatApps(activity)
            }
            hiddenList = withContext(Dispatchers.Default) {
                getAllHiddenItem(activity)
            }.toMutableList()

            val hiddenPackages = hiddenList.map { it.packageName }
            val filteredList =
                allAppsList.filter { it.packageName !in hots && it.packageName !in hiddenPackages }


            val h = UITool.screenHeight - UITool.dp2Pixel(160)
            appCntPerPage = floor(h / UITool.dp2Pixel(100)).toInt() * 4
            _pageData.value = filteredList.chunked(appCntPerPage)
            val hotApps = mutableListOf(
                getDialItem(activity),
            )
            hotApps.addAll(allAppsList.filter { it.packageName in hots })
            hotApps.add(getLockItem(activity))
            _hotSeats.value = hotApps
        }
    }

    fun onPageChanged(i: Int) {
        currentPage.value = i
    }

    private var job: Job? = null
    fun onPageScroll() {
        job?.cancel()
        showIndicator.value = true
        job = viewModelScope.launch {
            delay(1500)
            showIndicator.value = false
        }
    }

    val popupOffset = MutableLiveData(Offset.Zero)
    val appInfoDialogPopLeft = MutableLiveData(true)
    private var curLongPressItem: AppItem? = null
    fun toggleAppPopup(show: Boolean, offset: Offset? = null, item: AppItem?) {
        popupOffset.value = offset
        showAppPopup.value = show
        curLongPressItem = item
        item?.let { i ->
            val curData = currentPage.value?.let { _pageData.value?.get(it) }
            curData?.let { d ->
                val index = d.indexOf(i)
                appInfoDialogPopLeft.value = (index % 4) < 2
            }
        }
    }

    fun togglePatternDialog(show: Boolean) {
        showPatternDialog.value = show
    }

    fun onPatternConfirmed(s: String, activity: Activity): String {
        if (curLongPressItem == null) {
            val hiddenItem = hiddenList.find { it.code == s }
            hiddenItem?.let { item ->
                val appItem = allAppsList.find { it.packageName == item.packageName}
                appItem?.let { ai ->
                    jumpToApp(ai, activity)
                }
            }
        } else { // hide app item
            hiddenList.find { it.code == s }?.let {
                return activity.getString(R.string.pattern_taken)
            }

            val packageName = curLongPressItem!!.packageName
            val list = mutableListOf<AppItem>()
            for (page in _pageData.value!!) list.addAll(page)
            list.removeIf { it.packageName == packageName }
            _pageData.value = list.chunked(appCntPerPage)
            // save to db
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    addHiddenItem(packageName, s, activity.applicationContext)
                }
            }
            hiddenList.add(HiddenItem(packageName, s))
            curLongPressItem = null
        }
        showPatternDialog.value = false
        showAppPopup.value = false
        return ""
    }

    fun jumpToAppSettingPage(activity: Activity) {
        curLongPressItem?.let {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${it.packageName}")
            }
            activity.startActivity(intent)
        }
    }
}
