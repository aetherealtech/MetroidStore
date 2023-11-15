package com.example.metroidstore.root

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.metroidstore.cart.CartView
import com.example.metroidstore.cart.CartViewModel
import com.example.metroidstore.datasources.DataSource
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.productdetail.ProductDetailView
import com.example.metroidstore.productdetail.ProductDetailViewModel
import com.example.metroidstore.productlist.ProductListView
import com.example.metroidstore.productlist.ProductListViewModel
import com.example.metroidstore.repositories.CartRepository
import com.example.metroidstore.repositories.ProductRepository
import com.example.metroidstore.settings.SettingsView
import com.example.metroidstore.settings.SettingsViewModel
import com.example.metroidstore.utilities.ProductIDType
import com.example.metroidstore.utilities.getProductID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class Screen(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val content: @Composable (NavHostController, RootViewModel) -> Unit
) {
    data object ProductList : Screen(
        title = "Browse",
        icon = Icons.Filled.List,
        route = "productList",
        content = { navController, rootViewModel ->
            val productListViewModel = viewModel<ProductListViewModel>(
                factory = rootViewModel.productList
            )

            ProductListView(
                viewModel = productListViewModel,
                openProductDetails = { productID ->
                    navController.navigate("productDetails/${productID.value}")
                }
            )
        }
    )

    data object Cart : Screen(
        title = "Cart",
        icon = Icons.Filled.ShoppingCart,
        route = "cart",
        content = { navController, rootViewModel ->
            val cartViewModel = viewModel<CartViewModel>(
                factory = rootViewModel.cart
            )

            CartView(
                viewModel = cartViewModel,
                openProductDetails = { productID ->
                    navController.navigate("productDetails/${productID.value}")
                }
            )
        }
    )
    data object Settings : Screen(
        title = "Settings",
        icon = Icons.Filled.Settings,
        route = "settings",
        content = { _, rootViewModel ->
            val settingsViewModel = viewModel<SettingsViewModel>(
                factory = rootViewModel.settings
            )

            SettingsView(viewModel = settingsViewModel)
        }
    )

    companion object {
        val all: ImmutableList<Screen>
            get() {
                return persistentListOf(
                    ProductList,
                    Cart,
                    Settings
                )
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
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            CenterAlignedTopAppBar(
                title = { Text("Blah") },
                navigationIcon = {
                    if(navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
            )
        },
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
                                    inclusive = true
                                }
                                navController.graph.setStartDestination(screen.route)
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
                composable(screen.route) { screen.content(navController, viewModel) }
            }

            composable(
                "productDetails/{productID}",
                arguments = listOf(navArgument("productID") { type = NavType.ProductIDType })
            ) {backstackEntry ->
                val productID = backstackEntry.arguments!!.getProductID("productID")

                val detailsViewModel = viewModel<ProductDetailViewModel>(
                    factory = viewModel.productDetails(productID)
                )

                ProductDetailView(
                    viewModel = detailsViewModel
                )
            }
        }
    }
}

class RootViewModel(
    val dataSource: DataSource
): ViewModel() {
    private val productRepository = ProductRepository(
        dataSource = dataSource
    )

    private val cartRepository = CartRepository(
        dataSource = dataSource.cart
    )

    val productList: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return ProductListViewModel(
                productRepository = productRepository
            ) as T
        }
    }

    val cart: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return CartViewModel(
                repository = cartRepository
            ) as T
        }
    }

    fun productDetails(id: ProductID): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return ProductDetailViewModel(
                productID = id,
                repository = productRepository
            ) as T
        }
    }

    val settings: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return SettingsViewModel() as T
        }
    }
}