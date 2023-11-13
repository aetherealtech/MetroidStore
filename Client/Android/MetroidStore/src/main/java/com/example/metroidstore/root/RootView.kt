package com.example.metroidstore.root

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.metroidstore.datasources.DataSource
import com.example.metroidstore.productlist.ProductListView
import com.example.metroidstore.productlist.ProductListViewModel
import com.example.metroidstore.repositories.ProductRepository
import com.example.metroidstore.settings.SettingsView
import com.example.metroidstore.settings.SettingsViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class Screen(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val content: @Composable (RootViewModel) -> Unit
) {
    data object ProductList : Screen(
        title = "ProductList",
        icon = Icons.Filled.List,
        route = "productList",
        content = { viewModel -> ProductListView(viewModel = viewModel.productList) }
    )

    data object Settings : Screen(
        title = "Settings",
        icon = Icons.Filled.Settings,
        route = "settings",
        content = { viewModel -> SettingsView(viewModel = viewModel.settings) }
    )

    companion object {
        val all: ImmutableList<Screen>
            get() {
                return persistentListOf(ProductList, Settings)
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootView(
    viewModel: RootViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Screen.all.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.ProductList.route, Modifier.padding(innerPadding)) {
            Screen.all.forEach { screen ->
                composable(screen.route) { screen.content(viewModel) }
            }
        }
    }
}

class RootViewModel(
    private val dataSource: DataSource
): ViewModel() {
    val productList: ProductListViewModel
        get() {
            return ProductListViewModel(
                productRepository = ProductRepository(
                    dataSource = dataSource.products
                )
            )
        }

    val settings: SettingsViewModel
        get() {
            return SettingsViewModel()
        }
}