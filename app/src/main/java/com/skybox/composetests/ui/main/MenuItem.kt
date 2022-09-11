package com.skybox.composetests.ui.main

import androidx.compose.runtime.Composable

data class MenuItem(
    val route: String,
    val label: String,
    val isGroup: Boolean = false,
    val children: List<MenuItem>? = null,
    val composable: @Composable () -> Unit
)