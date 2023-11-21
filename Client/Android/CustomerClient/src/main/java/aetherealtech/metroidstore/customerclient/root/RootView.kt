package aetherealtech.metroidstore.customerclient.root

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import aetherealtech.metroidstore.customerclient.cart.CartView
import aetherealtech.metroidstore.customerclient.cart.CartViewModel
import aetherealtech.metroidstore.customerclient.checkout.CheckoutView
import aetherealtech.metroidstore.customerclient.checkout.CheckoutViewModel
import aetherealtech.metroidstore.customerclient.datasources.DataSource
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.orderdetails.OrderDetailsView
import aetherealtech.metroidstore.customerclient.orderdetails.OrderDetailsViewModel
import aetherealtech.metroidstore.customerclient.orders.OrdersView
import aetherealtech.metroidstore.customerclient.orders.OrdersViewModel
import aetherealtech.metroidstore.customerclient.productdetail.ProductDetailView
import aetherealtech.metroidstore.customerclient.productdetail.ProductDetailViewModel
import aetherealtech.metroidstore.customerclient.productlist.ProductListView
import aetherealtech.metroidstore.customerclient.productlist.ProductListViewModel
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.repositories.ProductRepository
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.settings.SettingsView
import aetherealtech.metroidstore.customerclient.settings.SettingsViewModel
import aetherealtech.metroidstore.customerclient.utilities.OrderIDType
import aetherealtech.metroidstore.customerclient.utilities.ProductIDType
import aetherealtech.metroidstore.customerclient.utilities.getOrderID
import aetherealtech.metroidstore.customerclient.utilities.getProductID
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
                factory = rootViewModel.productList(
                    openProductDetails = { productID -> navController.viewProductDetails(productID) }
                )
            )

            ProductListView(
                viewModel = productListViewModel
            )
        }
    )

    data object Cart : Screen(
        title = "Cart",
        icon = Icons.Filled.ShoppingCart,
        route = "cart",
        content = { navController, rootViewModel ->
            val cartViewModel = viewModel<CartViewModel>(
                factory = rootViewModel.cart(
                    openProductDetails = { productID -> navController.viewProductDetails(productID) },
                    openCheckout = { navController.navigate("checkout") }
                )
            )

            CartView(
                viewModel = cartViewModel
            )
        }
    )

    data object Orders : Screen(
        title = "Orders",
        icon = Icons.Filled.ArrowForward,
        route = "orders",
        content = { navController, rootViewModel ->
            val ordersViewModel = viewModel<OrdersViewModel>(
                factory = rootViewModel.orders(
                    viewOrder = { orderID -> navController.viewOrder(orderID) }
                )
            )

            OrdersView(viewModel = ordersViewModel)
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
                    Orders,
                    Settings
                )
            }
    }
}

fun NavHostController.selectTab(screen: Screen) {
    navigate(screen.route) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = true
        }
        graph.setStartDestination(screen.route)
        launchSingleTop = true
        restoreState = true
    }
}

fun NavHostController.viewProductDetails(productID: ProductID) {
    navigate("productDetails/${productID.value}")
}

fun NavHostController.viewOrder(orderID: OrderID) {
    selectTab(Screen.Orders)
    navigate("orders/${orderID.value}")
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
                title = { Text("Metroid Store") },
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
                            navController.selectTab(screen)
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
                    factory = viewModel.productDetails(
                        id = productID,
                        viewCart = {
                            navController.selectTab(Screen.Cart)
                        }
                    )
                )

                ProductDetailView(
                    viewModel = detailsViewModel
                )
            }

            composable(
                "checkout"
            ) {backstackEntry ->
                val detailsViewModel = viewModel<CheckoutViewModel>(
                    factory = viewModel.checkout(
                        viewOrder = { orderID -> navController.viewOrder(orderID) }
                    )
                )

                CheckoutView(
                    viewModel = detailsViewModel
                )
            }

            composable(
                "orders/{orderID}",
                arguments = listOf(navArgument("orderID") { type = NavType.OrderIDType })
            ) {backstackEntry ->
                val orderID = backstackEntry.arguments!!.getOrderID("orderID")

                val detailsViewModel = viewModel<OrderDetailsViewModel>(
                    factory = viewModel.orderDetails(
                        id = orderID,
                        viewProductDetails = { productID -> navController.viewProductDetails(productID) }
                    )
                )

                OrderDetailsView(
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

    private val userRepository = UserRepository(
        dataSource = dataSource.user
    )

    private val orderRepository = OrderRepository(
        dataSource = dataSource.orders
    )

    fun productList(
        openProductDetails: (ProductID) -> Unit,
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return ProductListViewModel(
                productRepository = productRepository,
                selectProduct = openProductDetails
            ) as T
        }
    }

    fun cart(
        openProductDetails: (ProductID) -> Unit,
        openCheckout: () -> Unit,
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return CartViewModel(
                repository = cartRepository,
                selectItem = openProductDetails,
                proceedToCheckout = openCheckout
            ) as T
        }
    }

    fun productDetails(
        id: ProductID,
        viewCart: () -> Unit
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return ProductDetailViewModel(
                productID = id,
                repository = productRepository,
                viewCart = viewCart
            ) as T
        }
    }

    fun checkout(
        viewOrder: (OrderID) -> Unit
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return CheckoutViewModel(
                cartRepository = cartRepository,
                userRepository = userRepository,
                viewOrder = viewOrder
            ) as T
        }
    }

    fun orders(
        viewOrder: (OrderID) -> Unit
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return OrdersViewModel(
                repository = orderRepository,
                viewOrder = viewOrder
            ) as T
        }
    }

    fun orderDetails(
        id: OrderID,
        viewProductDetails: (ProductID) -> Unit
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return OrderDetailsViewModel(
                orderID = id,
                repository = orderRepository,
                selectItem = viewProductDetails
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