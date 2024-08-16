package com.wayne.xlauncher.ui.component

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.wayne.xlauncher.MainViewModel
import com.wayne.xlauncher.R
import com.wayne.xlauncher.ui.theme.themeDark
import com.wayne.xlauncher.util.UITool

const val arrowHeight = 10
const val dialogHeight = arrowHeight + 36

@Composable
fun AppPopup(onDismissRequest: () -> Unit, viewModel: MainViewModel) {
    val offset = viewModel.popupOffset.observeAsState()
    val iconSizePx = UITool.dp2Pixel(58)
    val dialogHeightPx = UITool.dp2Pixel(dialogHeight)
    val showDown = offset.value!!.y - UITool.statusBarHeight - iconSizePx / 2 < dialogHeightPx
    val yOffset = (if (showDown) iconSizePx / 2 + UITool.dp2Pixel(10)
    else -iconSizePx / 2 - dialogHeightPx).toInt()

    val isLeft = viewModel.dialogPopLeft.observeAsState()
    val xOffset = if(isLeft.value == true) (-iconSizePx / 2).toInt() else (-iconSizePx).toInt()

    val activity = LocalContext.current as Activity

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0f)

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onDismissRequest()
            }) {
            Box(
                modifier = Modifier
                    .height(dialogHeight.dp)
                    .offset {
                        IntOffset(
                            offset.value!!.x.toInt() + xOffset,
                            offset.value!!.y.toInt() + yOffset
                        )
                    }
                    .clip(ArrowShape(18, arrowHeight, showDown, isLeft.value!!))
                    .background(Color.White)
                    .padding(
                        top = if (showDown) arrowHeight.dp else 0.dp,
                        bottom = if (!showDown) arrowHeight.dp else 0.dp,
                    )
                    .shadow((-3).dp, ArrowShape(18, arrowHeight, showDown, isLeft.value!!))
            ) {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 46.dp, height = 36.dp)
                            .clickable {
                                viewModel.jumpToAppSettingPage(activity)
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "",
                            modifier = Modifier.size(28.dp),
                            tint = themeDark
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 0.5.dp, height = 16.dp)
                            .background(Color.DarkGray)
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 46.dp, height = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_hide),
                            contentDescription = "",
                            modifier = Modifier.size(28.dp),
                            tint = themeDark
                        )
                    }
                }
            }
        }
    }
}

