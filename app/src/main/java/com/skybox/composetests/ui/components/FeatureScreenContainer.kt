package com.skybox.composetests.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun FeatureScreenContainer(
    title: String,
    navController: NavHostController? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(navigationIcon = {
            IconButton(onClick = { navController?.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back Button")
            }
        }, title = {
            Text(text = title)
        })
    }) { paddingValues ->
        content(paddingValues)
    }
}