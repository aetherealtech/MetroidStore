package aetherealtech.metroidstore.customerclient.root

import aetherealtech.metroidstore.customerclient.addresses.AddressesView
import aetherealtech.metroidstore.customerclient.addresses.AddressesViewModel
import aetherealtech.metroidstore.customerclient.cart.CartViewModel
import aetherealtech.metroidstore.customerclient.checkout.CheckoutView
import aetherealtech.metroidstore.customerclient.checkout.CheckoutViewModel
import aetherealtech.metroidstore.customerclient.datasources.DataSource
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.orderdetails.OrderDetailsView
import aetherealtech.metroidstore.customerclient.orderdetails.OrderDetailsViewModel
import aetherealtech.metroidstore.customerclient.orders.OrdersViewModel
import aetherealtech.metroidstore.customerclient.productdetail.ProductDetailView
import aetherealtech.metroidstore.customerclient.productdetail.ProductDetailViewModel
import aetherealtech.metroidstore.customerclient.productlist.ProductListViewModel
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.repositories.ProductRepository
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.routing.Screen
import aetherealtech.metroidstore.customerclient.routing.rememberRouter
import aetherealtech.metroidstore.customerclient.settings.SettingsViewModel
import aetherealtech.metroidstore.customerclient.utilities.OrderIDType
import aetherealtech.metroidstore.customerclient.utilities.ProductIDType
import aetherealtech.metroidstore.customerclient.utilities.getOrderID
import aetherealtech.metroidstore.customerclient.utilities.getProductID
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootView(
    viewModel: RootViewModel
) {
    val router = rememberRouter()

    val setAppBarState: (AppBarState) -> Unit = { newState -> router.setAppBarState(newState) }

    Scaffold(
        topBar = {
            val navBackStackEntry by router.currentBackStackEntryAsState()
            @Suppress("UNUSED_VARIABLE") val currentDestination = navBackStackEntry?.destination // This is needed to trigger recomposition when the destination changes

            CenterAlignedTopAppBar(
                title = router.topBarState.title,
                navigationIcon = {
                    if(router.previousBackStackEntry != null) {
                        IconButton(onClick = { router.back() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = router.topBarState.actions
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by router.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Screen.all.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon(), contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { router.selectTab(screen) }
                    )
                }
            }
        }
    ) { innerPadding ->
        router.NavHost(startDestination = Screen.ProductList.route, Modifier.padding(innerPadding)) {
            Screen.all.forEach { screen ->
                composable(screen.route) { screen.content(
                    router,
                    setAppBarState,
                    viewModel
                ) }
            }

            composable(
                "productDetails/{productID}",
                arguments = listOf(navArgument("productID") { type = NavType.ProductIDType })
            ) {backstackEntry ->
                val productID = backstackEntry.arguments!!.getProductID("productID")

                val detailsViewModel = viewModel<ProductDetailViewModel>(
                    factory = viewModel.productDetails(
                        id = productID,
                        viewCart = { router.selectTab(Screen.Cart) }
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
                        viewOrder = { orderID ->
                            router.selectTab(Screen.Orders)
                            router.viewOrder(orderID)
                        }
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
                        viewProductDetails = { productID -> router.viewProductDetails(productID) }
                    )
                )

                OrderDetailsView(
                    viewModel = detailsViewModel
                )
            }

            composable(
                "addresses"
            ) {backstackEntry ->
                val addressesViewModel = viewModel<AddressesViewModel>(
                    factory = viewModel.addresses(
                        openAddAddress = {
                            println("TEST")
                        }
                    )
                )

                AddressesView(
                    setAppBarState = setAppBarState,
                    viewModel = addressesViewModel
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

    fun settings(
        openAddresses: () -> Unit,
        openPaymentMethods: () -> Unit,
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return SettingsViewModel(
                openAddresses = openAddresses,
                openPaymentMethods = openPaymentMethods
            ) as T
        }
    }

    fun addresses(
        openAddAddress: () -> Unit
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T: ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            return AddressesViewModel(
                repository = userRepository,
                openAddAddress = openAddAddress
            ) as T
        }
    }
}