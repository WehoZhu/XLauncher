/* © 2024 Wayne Zhu. All rights reserved. */
package com.wayne.xlauncher.ui.component

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wayne.xlauncher.ui.theme.themeDark
import com.wayne.xlauncher.util.UITool
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


@Composable
fun PatternDialog(onDismissRequest: () -> Unit, onPatternConfirmed: (String) -> String) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(themeDark)
        ) {
            PatternCanvas(onPatternConfirmed = onPatternConfirmed)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalTextApi::class)
@Composable
fun PatternCanvas(
    dotSize: Dp = 4.dp,
    dotPadding: Int = 8, // Dp
    padding: Dp = 48.dp,
    onPatternConfirmed: (String) -> String
) {
    val dots = remember { mutableListOf<Dot>() }
    val linkedDots = remember { mutableStateListOf<Dot>() }
    val dotPaddingPx = UITool.dp2Pixel(dotPadding + 12)
    var errorTip = remember { mutableStateOf("") }

    val haptic = LocalHapticFeedback.current

    Box {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        linkedDots.clear()
                        errorTip.value = ""
                        linkDots(dots, linkedDots, it, dotPaddingPx, haptic)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        while (linkedDots.isNotEmpty() && linkedDots.last().value == -1) {
                            linkedDots.removeLast()
                        }
                        linkDots(dots, linkedDots, it, dotPaddingPx, haptic)
                        linkedDots.add(Dot(it.x, it.y, -1))
                    }

                    MotionEvent.ACTION_UP -> {
                        while (linkedDots.isNotEmpty() && linkedDots.last().value == -1) {
                            linkedDots.removeLast()
                        }
                        if (linkedDots.size > 1) {
                            errorTip.value =
                                onPatternConfirmed(linkedDots.joinToString { d -> d.value.toString() })
                        }
                    }
                }
                true
            }
        ) {
            val pSize = min(size.width, size.height)
            val paddingPx = padding.toPx()
            val offset = (pSize - 2 * paddingPx) / 2
            val lineColor = if (errorTip.value.isNotEmpty()) Color.Red else Color.White
            for (i in 0..2) {
                for (j in 0..2) {
                    dots.add(Dot(i * offset + paddingPx, j * offset + paddingPx, i * 3 + j))
                }
            }
            for (d in dots) {
                drawCircle(Color.White, radius = dotSize.toPx(), Offset(d.x, d.y))
                if (d in linkedDots) {
                    drawCircle(
                        Color.Gray.copy(alpha = 0.3f),
                        radius = (dotSize + dotPadding.dp).toPx(),
                        Offset(d.x, d.y)
                    )
                }
            }

            for (i in linkedDots.indices) {
                if (i == linkedDots.size - 1) continue
                val from = linkedDots[i]
                val to = linkedDots[i + 1]
                drawLine(
                    lineColor, Offset(from.x, from.y), Offset(to.x, to.y),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        Text(
            text = errorTip.value, style = TextStyle(fontSize = 14.sp, color = Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}

fun linkDots(
    dots: MutableList<Dot>,
    linked: MutableList<Dot>,
    offset: MotionEvent,
    padding: Float,
    haptic: HapticFeedback
) {
    val x = offset.x
    val y = offset.y
    for (d in dots) {
        val dst = sqrt((x - d.x).pow(2) + (y - d.y).pow(2))
        if (dst < padding && d !in linked) {
            linked.add(d)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}

data class Dot(val x: Float, val y: Float, val value: Int)
