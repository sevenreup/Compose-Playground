package com.skybox.composetests.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skybox.composetests.R

@ExperimentalMaterialApi
@Composable
fun MainContent(menuItems: List<MenuItem>) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(menuItems) { route ->
                navController.navigate(route)
            }
        }
        repeat(menuItems.size) { size ->
            val item = menuItems[size]

            if (item.isGroup) {
                val children = item.children
                if (children != null) {
                    repeat(children.size) { length ->
                        val child = children[length]
                        composable(child.route) {
                            child.composable()
                        }
                    }
                }
            } else {
                composable(item.route) { item.composable() }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MainScreen(menuItems: List<MenuItem>, navigate: (String) -> Unit) {
    Scaffold(topBar = {
        TopAppBar {
            Text(text = stringResource(id = R.string.app_name))
        }
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { item ->
                if (item.isGroup) {
                    MenuGroup(menuItem = item, navigate = navigate)
                } else {
                    MenuItemCard(item, onClick = {
                        navigate(item.route)
                    })
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MenuGroup(menuItem: MenuItem, navigate: (String) -> Unit) {
    val expanded by remember {
        mutableStateOf(false)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth()) {
                Text(text = menuItem.label)
                Icon(
                    if (expanded) Icons.Default.ArrowDropDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = ""
                )
            }
            if (expanded) {
                val children = menuItem.children
                if (children != null) {
                    repeat(children.size) { size ->
                        val child = children[size]

                        MenuItemCard(menuItem = child) {
                            navigate(child.route)
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MenuItemCard(menuItem: MenuItem, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxSize()) {
        Text(text = menuItem.label, Modifier.padding(20.dp))
    }
}