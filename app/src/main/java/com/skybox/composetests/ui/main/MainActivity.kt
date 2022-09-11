package com.skybox.composetests.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.skybox.composetests.features.effects.RecompositionTest
import com.skybox.composetests.features.scroller.LazyColumnView
import com.skybox.composetests.features.textfield.TextFieldChipsScreen
import com.skybox.composetests.ui.theme.ComposeTestsTheme

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()

                    MainContent(navController, getContent(navController))
                }
            }
        }
    }

    private fun getContent(navHostController: NavHostController): List<MenuItem> {
        return listOf(
            MenuItem(
                route = "effect", label = "Launched Effect",
                composable = {
                    RecompositionTest()
                },
            ),
            MenuItem(
                route = "scroller", label = "Scroller",
                composable = {
                    LazyColumnView()
                },
            ),
            MenuItem(
                "", label = "Text Fields",
                isGroup = true,
                children = listOf(
                    MenuItem(
                        route = "textFiled_chips", label = "Text Field With Chips",
                        composable = {
                            TextFieldChipsScreen(navHostController)
                        },
                    )
                ),
            ),
        )
    }
}
