package com.skybox.composetests.features.effects

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.*

@androidx.compose.runtime.Composable
fun RecompositionTest() {
    var increment by remember{mutableStateOf(0)}

    LaunchedEffect(increment) {
        increment++
        Log.d("Sample", "Increment: $increment")
    }

    Text("$increment")
}