/* © 2024 Wayne Zhu. All rights reserved. */
package com.wayne.xlauncher.ui.component

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.wayne.xlauncher.util.UITool

class ArrowShape(
    private val corner: Int,
    private val arrowSize: Int,
    private val isDown: Boolean,
    private val isLeft: Boolean
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerPx = UITool.dp2Pixel(corner)
        val arrowSizePx = UITool.dp2Pixel(arrowSize)
        val iconSize = UITool.dp2Pixel(58)
        val a1 = if (isLeft) iconSize / 2 else size.width - iconSize / 2 - arrowSizePx / 2
        val a2 = a1 + arrowSizePx / 2
        val a3 = a1 + arrowSizePx
        return Outline.Generic(
            if (!isDown)
                Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(0f, 0f, size.width, size.height - arrowSizePx),
                            topLeft = CornerRadius(cornerPx),
                            topRight = CornerRadius(cornerPx),
                            bottomRight = CornerRadius(cornerPx),
                            bottomLeft = CornerRadius(cornerPx),
                        )
                    )
                    moveTo(a1, size.height - arrowSizePx)
                    lineTo(a2, size.height)
                    lineTo(a3, size.height - arrowSizePx)
                    close()
                }
            else
                Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(0f, arrowSizePx, size.width, size.height),
                            topLeft = CornerRadius(cornerPx),
                            topRight = CornerRadius(cornerPx),
                            bottomRight = CornerRadius(cornerPx),
                            bottomLeft = CornerRadius(cornerPx),
                        )
                    )
                    moveTo(a1, arrowSizePx)
                    lineTo(a2, 0f)
                    lineTo(a3, arrowSizePx)
                    close()
                }
        )
    }
}
