package com.wayne.xlauncher.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wayne.xlauncher.MainViewModel
import com.wayne.xlauncher.R

@Composable
fun PagerIndicator(viewModel: MainViewModel) {
    val data = viewModel.data.observeAsState()
    val index = viewModel.currentPage.observeAsState()
    val showIndicator = viewModel.showIndicator.observeAsState()

    AnimatedVisibility(
        visible = showIndicator.value!!,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
    ) {
        Box(
            modifier = Modifier
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray.copy(alpha = 0.55f))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row {
                List(data.value!!.size) { i ->
                    Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (i == index.value) Color.White else Color.White.copy(
                                        alpha = 0.3f
                                    )
                                )
                        )
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = !showIndicator.value!!,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
    ) {
        Box(
            modifier = Modifier
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray.copy(alpha = 0.55f))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(12.dp),
                painter = painterResource(id = R.drawable.btn_logo), contentDescription = ""
            )
        }
    }
}
