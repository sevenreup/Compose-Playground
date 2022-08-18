package com.skybox.composetests.scroller

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skybox.composetests.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

private val BUBBLE_SIZE = 48.dp
private val BUBBLE_PADDING_TOP = 24.dp
private val BUBBLE_PADDING_BOTTOM = 56.dp

@ExperimentalTime
@Composable
fun Scroller(
    modifier: Modifier,
    progress: Float,
    txt: String,
    onDrag: (relativeDragYOffset: Float, maxHeight: Float) -> Unit,
    listState: LazyListState,
) {
    val scope = rememberCoroutineScope()
    //this padding is for overlap on the top and bottom of scroll bar
    val bubblePaddingTopInPx =
        with(LocalDensity.current) { BUBBLE_PADDING_TOP.toPx() }
    val bubblePaddingBottomInPx =
        with(LocalDensity.current) { BUBBLE_PADDING_BOTTOM.toPx() }
    val bubbleSizeInPx =
        with(LocalDensity.current) { BUBBLE_SIZE.toPx() }

    var scrollDragYOffset by remember {
        mutableStateOf(0f)
    }

    var isDragging by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.CenterEnd) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            val boxHeight = constraints.maxHeight.toFloat()
            val realBoxHeight = boxHeight - bubbleSizeInPx
            val visibleScrollingBoxHeight =
                boxHeight - bubblePaddingBottomInPx - bubblePaddingTopInPx - bubbleSizeInPx

            if (listState.isScrollInProgress)
                scrollDragYOffset = boxHeight * progress

            Box(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                val offset =
                                    if (scrollDragYOffset < 0) 0f else if (scrollDragYOffset > boxHeight) boxHeight else scrollDragYOffset
                                Log.e(
                                    "TAG",
                                    "${it.y} $scrollDragYOffset $offset: ${it.y in offset..(offset + bubbleSizeInPx)}"
                                )

                                if (it.y in offset..(offset + bubbleSizeInPx)) {
                                    isDragging = true
                                    onDrag(
                                        it.y,
                                        visibleScrollingBoxHeight
                                    )
                                }
                            },
                            onDragEnd = {
                                scope.launch {
                                    delay(2.seconds)
                                    isDragging = false
                                }
                            }
                        ) { _, dragAmount ->
                            if (isDragging) {
                                if (dragAmount > 0) { // drag slider down
                                    if (scrollDragYOffset >= (boxHeight - boxHeight / bubbleSizeInPx)) { // Bottom End
                                        scrollDragYOffset =
                                            boxHeight - boxHeight / bubbleSizeInPx
                                        scope.launch {
                                            listState.scrollToItem(listState.layoutInfo.totalItemsCount)
                                        }
                                    } else {
                                        scrollDragYOffset += dragAmount
                                    }
                                } else { // drag slider up
                                    if (scrollDragYOffset <= 0f) { // Top Start
                                        scrollDragYOffset = 0F
                                        scope.launch {
                                            listState.scrollToItem(0)
                                        }
                                    } else {
                                        scrollDragYOffset += dragAmount
                                    }
                                }
                                val yMaxValue =
                                    boxHeight - boxHeight / bubbleSizeInPx
                                val yPercentage = (100 * scrollDragYOffset) / yMaxValue

                                val renderedItemsNumberPerScroll =
                                    listState.layoutInfo.visibleItemsInfo.size - 2
                                val index =
                                    (((listState.layoutInfo.totalItemsCount - renderedItemsNumberPerScroll) * yPercentage) / 100).toInt()

                                scope.launch {
                                    if (index > 0) {
                                        listState.scrollToItem(index)
                                    }
                                }
                            }
                        }
                    },
            )
            ScrollingBubble(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                progress = progress,
                bubbleOffsetYFloat = scrollDragYOffset,
                isDragging = isDragging,
                txt = txt,
                maxYOffset = boxHeight - bubbleSizeInPx
            )
        }
    }
}

@ExperimentalTime
@Composable
private fun ScrollingBubble(
    modifier: Modifier,
    bubbleOffsetYFloat: Float,
    txt: String,
    progress: Float,
    maxYOffset: Float,
    isDragging: Boolean,
) {
    var bubbleVisibility by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = progress) {
        bubbleVisibility = true
        delay(3.seconds)
        bubbleVisibility = false
    }
    Box(
        modifier = modifier
            .graphicsLayer {
                translationY =
                    if (bubbleOffsetYFloat < 0) 0f else if (bubbleOffsetYFloat > maxYOffset) maxYOffset else bubbleOffsetYFloat
            }, content = {
            AnimatedVisibility(
                visible = bubbleVisibility,
                exit = slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(visible = isDragging && txt.isNotEmpty()) {
                        Text(
                            text = txt,
                            modifier = Modifier
                                .shadow(
                                    elevation = 2.dp,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .background(
                                    color = MaterialTheme.colors.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface
                        )
                    }


                    Image(
                        painter = painterResource(id = R.drawable.ic_scroll),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(
                                    bottomStart = 50.dp,
                                    topStart = 50.dp
                                )
                            )
                            .background(
                                shape = RoundedCornerShape(
                                    bottomStart = 50.dp,
                                    topStart = 50.dp
                                ),
                                color = MaterialTheme.colors.onPrimary
                            )
                            .size(BUBBLE_SIZE)
                            .padding(start = 8.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                        contentDescription = null
                    )
                }
            }
        }
    )
}
