package com.wayne.xlauncher.ui.component

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wayne.xlauncher.MainViewModel
import com.wayne.xlauncher.data.AppItem
import com.wayne.xlauncher.util.UITool
import com.wayne.xlauncher.util.onItemClick

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(viewModel: MainViewModel) {
    val data = viewModel.data.observeAsState()
    val insets = WindowInsets.safeContent
    val pageState = rememberPagerState()
    val showAppPopup = viewModel.showAppPopup.observeAsState()

    LaunchedEffect(pageState) {
        snapshotFlow { pageState.currentPageOffsetFraction }.collect { f ->
            if (f == 0f) {
                viewModel.onPageChanged(pageState.currentPage)
            }
            viewModel.onPageScroll()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(insets.asPaddingValues())
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pageState,
                pageCount = data.value!!.size,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                beyondBoundsPageCount = 1
            ) { page ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    items(data.value!![page].size) { i ->
                        AppCell(data.value!![page][i], viewModel)
                    }
                }
            }
            PagerIndicator(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            HotSeats(viewModel)
        }

        if (showAppPopup.value == true)
            AppPopup(
                viewModel = viewModel,
                onDismissRequest = { viewModel.toggleAppPopup(false, null, null) })
    }
}

@Composable
fun HotSeats(viewModel: MainViewModel) {
    val hots = viewModel.hotSeats.observeAsState()
    val activity = LocalContext.current as Activity
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.DarkGray.copy(alpha = 0.55f))
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(hots.value!!) {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = interactionSource, indication = null
                        ) {
                            onItemClick(it, activity)
                        }) {
                        Image(
                            bitmap = drawableToBitmap(it.icon!!),
                            contentDescription = "",
                            modifier = Modifier.size(58.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppCell(item: AppItem, viewModel: MainViewModel) {
    val activity = LocalContext.current as Activity
    var position by remember { mutableStateOf(Offset.Zero) }

    Box(contentAlignment = Alignment.Center, modifier = Modifier
        .size(84.dp, 100.dp)
        .onGloballyPositioned {
            position = Offset(it.positionInRoot().x, it.positionInRoot().y)
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    viewModel.toggleAppPopup(true, position, item)
                },
                onTap = {
                    onItemClick(item, activity)
                }
            )
        }) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
                Image(
                    bitmap = drawableToBitmap(item.icon!!),
                    contentDescription = "",
                    modifier = Modifier.size(58.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.name ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                style = TextStyle(
                    fontSize = 12.sp,
                    shadow = Shadow(Color.Black, Offset(1f, 1f), 2f)
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}


fun drawableToBitmap(drawable: Drawable, size: Int = 58): ImageBitmap {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap.asImageBitmap()
    }
    val sizePx = UITool.dp2Pixel(size).toInt()
    val bitmap = Bitmap.createBitmap(
        sizePx, sizePx, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
}
