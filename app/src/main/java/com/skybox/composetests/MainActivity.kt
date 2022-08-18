package com.skybox.composetests

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skybox.composetests.scroller.LazyColumnScrollbarSettings
import com.skybox.composetests.scroller.LazyColumnWithScrollbar
import com.skybox.composetests.scroller.Scroller
import com.skybox.composetests.scroller.scrollbar
import com.skybox.composetests.ui.theme.ComposeTestsTheme
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LazyColumnView()
//                    Jeff()
//                    Jeffff()
                }
            }
        }
    }
}

@Composable
fun Jeffff() {
    val scrollbarSettings = remember {
        mutableStateOf(LazyColumnScrollbarSettings())
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumnWithScrollbar(
            data = (0..1000).toList(),
            settings = scrollbarSettings.value,
            modifier = Modifier.height(500.dp)
        ) {
            items((0..1000).toList()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .clickable { },
                    elevation = 10.dp
                ) {
                    Column {
                        Text(
                            text = it.toString(),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        }

        Row() {
            Button(modifier = Modifier
                .fillMaxWidth(0.5F)
                .padding(4.dp),
                contentPadding = PaddingValues(4.dp),
                onClick = {
                    scrollbarSettings.value = scrollbarSettings.value.copy(
                        thumbColor = Color.Green,
                        trailColor = Color.Transparent,
                        thumbWidth = LazyColumnScrollbarSettings.ThumbWidth.X_LARGE,
                        thumbHeight = LazyColumnScrollbarSettings.ThumbHeight.SMALL
                    )
                }
            ) {
                Text(text = "Green + Small + Thick")
            }

            Button(modifier = Modifier
                .fillMaxWidth(1F)
                .padding(4.dp),
                contentPadding = PaddingValues(4.dp),
                onClick = {
                    scrollbarSettings.value = scrollbarSettings.value.copy(
                        thumbColor = Color.Red,
                        trailColor = Color.Yellow,
                        thumbWidth = LazyColumnScrollbarSettings.ThumbWidth.SMALL,
                        thumbHeight = LazyColumnScrollbarSettings.ThumbHeight.X_LARGE
                    )
                }
            ) {
                Text("Red + Yellow + XL + Thin")
            }
        }
        Button(modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
            contentPadding = PaddingValues(4.dp),
            onClick = {
                scrollbarSettings.value = LazyColumnScrollbarSettings()
            }
        ) {
            Text("Default")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Jeff() {
    val listData = (0..1000).toList()
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .padding(16.dp)
            .border(width = 1.dp, MaterialTheme.colors.primary)
            .padding(1.dp)
    ) {
        LazyColumnScrollbar(
            listState,
            selectionMode = ScrollbarSelectionMode.Thumb,
            indicatorContent = { index, isThumbSelected ->
                Surface {
                    Text(
                        text = "i: $index",
                        Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 20.dp,
                                    bottomStart = 20.dp,
                                    bottomEnd = 16.dp
                                )
                            )
                            .background(Color.Green)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(if (isThumbSelected) MaterialTheme.colors.surface else MaterialTheme.colors.background)
                            .padding(12.dp)
                    )
                }
            }
        ) {
            LazyColumn(state = listState) {
                (0..3).forEach { number ->
                    stickyHeader {
                        Surface {
                            Text(
                                text = "HEADER $number",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                items(listData) {
                    Text(
                        text = "Item $it",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyColumnView() {
    val listData = (0..5000).toList()
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        LazyVerticalGrid(
            state = listState,
            cells = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listData) {
                Card(
                    modifier = Modifier
                        .height(128.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Item $it",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }

            }
        }
        Scroller(
            listState = listState,
            txt = "Jefff",
            modifier = Modifier,
        )
    }
}

