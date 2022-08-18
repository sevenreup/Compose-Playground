package com.skybox.composetests.scroller

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skybox.composetests.R
import kotlinx.coroutines.launch
import kotlin.math.floor


private const val BUBBLE_SIZE = 48f
private const val BUBBLE_MINI_HEIGHT: Float = 0.08f

@Composable
fun Scroller(
    listState: LazyListState,
    txt: String,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var isDragging by remember { mutableStateOf(false) }

    var dragOffset by remember { mutableStateOf(0f) }

    val realFirstVisibleItem by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.firstOrNull {
                it.index == listState.firstVisibleItemIndex
            }
        }
    }

    fun LazyListItemInfo.fractionHiddenTop() =
        if (size == 0) 0f else -offset.toFloat() / size.toFloat()

    fun LazyListItemInfo.fractionVisibleBottom(viewportEndOffset: Int) =
        if (size == 0) 0f else (viewportEndOffset - offset).toFloat() / size.toFloat()

    val normalizedBubbleSizeReal by remember {
        derivedStateOf {
            listState.layoutInfo.let {
                if (it.totalItemsCount == 0)
                    return@let 0f

                val firstItem = realFirstVisibleItem ?: return@let 0f
                val firstPartial = firstItem.fractionHiddenTop()
                val lastPartial =
                    1f - it.visibleItemsInfo.last().fractionVisibleBottom(it.viewportEndOffset)

                val realVisibleSize =
                    it.visibleItemsInfo.size.toFloat() - firstPartial - lastPartial
                realVisibleSize / it.totalItemsCount.toFloat()
            }
        }
    }

    val normalizedBubbleSize by remember {
        derivedStateOf {
            normalizedBubbleSizeReal.coerceAtLeast(BUBBLE_MINI_HEIGHT)
        }
    }

    fun offsetCorrection(top: Float): Float {
        if (normalizedBubbleSizeReal >= BUBBLE_MINI_HEIGHT)
            return top
        val topRealMax = 1f - normalizedBubbleSizeReal
        val topMax = 1f - BUBBLE_MINI_HEIGHT
        return top * topMax / topRealMax
    }

    fun offsetCorrectionInverse(top: Float): Float {
        if (normalizedBubbleSizeReal >= BUBBLE_MINI_HEIGHT)
            return top
        val topRealMax = 1f - normalizedBubbleSizeReal
        val topMax = 1f - BUBBLE_MINI_HEIGHT
        return top * topRealMax / topMax
    }

    val normalizedOffsetPosition by remember {
        derivedStateOf {
            listState.layoutInfo.let {
                if (it.totalItemsCount == 0 || it.visibleItemsInfo.isEmpty())
                    return@let 0f

                val firstItem = realFirstVisibleItem ?: return@let 0f
                val top = firstItem
                    .run { index.toFloat() + fractionHiddenTop() } / it.totalItemsCount.toFloat()
                offsetCorrection(top)
            }
        }
    }

    fun setDragOffset(value: Float) {
        val maxValue = (1f - normalizedBubbleSize).coerceAtLeast(0f)
        dragOffset = value.coerceIn(0f, maxValue)
    }

    fun setScrollOffset(newOffset: Float) {
        setDragOffset(newOffset)
        val totalItemsCount = listState.layoutInfo.totalItemsCount.toFloat()
        val exactIndex = offsetCorrectionInverse(totalItemsCount * dragOffset)
        val index: Int = floor(exactIndex).toInt()
        val remainder: Float = exactIndex - floor(exactIndex)

        coroutineScope.launch {
            listState.scrollToItem(index = index, scrollOffset = 0)
            val offset = realFirstVisibleItem
                ?.size
                ?.let { it.toFloat() * remainder }
                ?.toInt() ?: 0
            Log.e(
                "TAG",
                "${index == exactIndex.toInt()} $index $totalItemsCount $exactIndex $newOffset, $offset $remainder"
            )
            listState.scrollToItem(
                index = index,
                scrollOffset = offset
            )
        }
    }

    val isScrolling = listState.isScrollInProgress || isDragging

    val alpha by animateFloatAsState(
        targetValue = if (isScrolling) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (isScrolling) 75 else 500,
            delayMillis = if (isScrolling) 0 else 500
        )
    )

    val displacement by animateFloatAsState(
        targetValue = if (isScrolling) 0f else 14f,
        animationSpec = tween(
            durationMillis = if (isScrolling) 75 else 500,
            delayMillis = if (isScrolling) 0 else 500
        )
    )

    BoxWithConstraints(
        Modifier
            .alpha(alpha)
            .fillMaxWidth()
    ) {
        BoxWithConstraints(
            Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
                .draggable(
                    state = rememberDraggableState { delta ->
                        if (isDragging) {
                            setScrollOffset(dragOffset + delta / (constraints.maxHeight.toFloat()))
                        }
                    },
                    orientation = Orientation.Vertical,
                    startDragImmediately = true,
                    onDragStarted = { offset ->
                        val newOffset = offset.y / constraints.maxHeight.toFloat()
                        if (newOffset in normalizedOffsetPosition..(normalizedOffsetPosition + normalizedBubbleSize)) {
                            setDragOffset(normalizedOffsetPosition)
                            isDragging = true
                        }
                    },
                    onDragStopped = {
                        isDragging = false
                    }
                )
                .graphicsLayer {
                    translationX = displacement.dp.toPx()
                }
        ) {
            ScrollingBubble(
                txt = txt,
                isDragging = isDragging,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight()
                    .graphicsLayer {
                        translationX = (displacement.dp).toPx()
                        translationY =
                            constraints.maxHeight.toFloat() * normalizedOffsetPosition
                    },
            )
        }

    }
}

@Composable
private fun ScrollingBubble(
    txt: String,
    isDragging: Boolean,
    modifier: Modifier,
) {
    BoxWithConstraints(
        modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(
                visible = isDragging && txt.isNotEmpty(),
                exit = fadeOut(animationSpec = keyframes {
                    this.durationMillis = 500
                })
            ) {
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
                    .size(BUBBLE_SIZE.dp)
                    .background(
                        shape = RoundedCornerShape(
                            bottomStart = 50.dp,
                            topStart = 50.dp
                        ),
                        color = MaterialTheme.colors.onPrimary
                    ),
                contentDescription = null
            )
        }

    }
}
