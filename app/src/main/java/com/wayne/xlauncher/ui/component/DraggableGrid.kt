/* © 2024 Wayne Zhu. All rights reserved. */
package com.wayne.xlauncher.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.unit.dp
import androidx.core.view.DragStartHelper
import kotlin.properties.Delegates

const val iconSize = 100
class DraggableGrid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var rows by Delegates.notNull<Int>()
    private var cols by Delegates.notNull<Int>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        rows = heightMeasureSpec / iconSize
        cols = widthMeasureSpec / iconSize
    }

    fun setData() {}
}
