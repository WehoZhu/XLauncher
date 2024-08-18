/* © 2024 Wayne Zhu. All rights reserved. */

package com.wayne.xlauncher.data

import android.graphics.drawable.Drawable

data class AppItem(val packageName: String) {
    var name: String? = null
    var icon: Drawable? = null
    var deepLink: String? = null
}
