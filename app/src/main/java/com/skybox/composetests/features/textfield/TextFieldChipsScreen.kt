package com.skybox.composetests.features.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.skybox.composetests.ui.components.FeatureScreenContainer

@Composable
fun TextFieldChipsScreen(controller: NavHostController) {
    FeatureScreenContainer(title = "TextField with Chips", navController = controller) { padding ->
        Column(Modifier.padding(padding)) {

        }
    }
}