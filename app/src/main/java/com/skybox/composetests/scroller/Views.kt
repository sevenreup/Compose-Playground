package com.skybox.composetests.scroller

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


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

