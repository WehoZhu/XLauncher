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
import com.wayne.xlauncher.util.UITool
import com.wayne.xlauncher.util.getDefaultHotSeatApps
import com.wayne.xlauncher.util.getDialItem
import com.wayne.xlauncher.util.getInstalledApps
import com.wayne.xlauncher.util.getLockItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor

class MainViewModel : ViewModel() {
    private val _pageData: MutableLiveData<List<List<AppItem>>> = MutableLiveData(listOf())
    val data: MutableLiveData<List<List<AppItem>>>
        get() = _pageData

    private val _hotSeats: MutableLiveData<List<AppItem>> = MutableLiveData(listOf())
    val hotSeats: MutableLiveData<List<AppItem>>
        get() = _hotSeats

    val currentPage: MutableLiveData<Int> = MutableLiveData(0)
    val showIndicator: MutableLiveData<Boolean> = MutableLiveData(false)

    val showAppPopup = MutableLiveData(false)

    fun doOnCreate(activity: MainActivity) {
        viewModelScope.launch {
            val list = withContext(Dispatchers.Default) {
                getInstalledApps(activity)
            }
            val hots = withContext(Dispatchers.Default) {
                getDefaultHotSeatApps(activity)
            }
            val filteredList = list.filter { it.packageName !in hots }
            val h = UITool.screenHeight - UITool.dp2Pixel(160)

            val cntPerPage = floor(h / UITool.dp2Pixel(100)) * 4
            _pageData.value = filteredList.chunked(cntPerPage.toInt())
            val hotApps = mutableListOf(
                getDialItem(activity),
            )
            hotApps.addAll(list.filter { it.packageName in hots })
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
    val dialogPopLeft = MutableLiveData(true)
    private var curLongPressItem: AppItem? = null
    fun toggleAppPopup(show: Boolean, offset: Offset? = null, item: AppItem?) {
        popupOffset.value = offset
        showAppPopup.value = show
        curLongPressItem = item
        item?.let { i ->
            val curData = currentPage.value?.let { _pageData.value?.get(it) }
            curData?.let { d ->
                val index = d.indexOf(i)
                dialogPopLeft.value = (index % 4) < 2
            }
        }
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