package aetherealtech.metroidstore.customerclient.root

import aetherealtech.metroidstore.customerclient.addaddress.AddAddressView
import aetherealtech.metroidstore.customerclient.addaddress.AddAddressViewModel
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
import aetherealtech.metroidstore.customerclient.utilities.ViewModelFactory
import aetherealtech.metroidstore.customerclient.utilities.getOrderID
import aetherealtech.metroidstore.customerclient.utilities.getProductID
import aetherealtech.metroidstore.customerclient.utilities.viewModel
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

                val detailsViewModel = viewModel(
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
                val detailsViewModel = viewModel(
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

                val detailsViewModel = viewModel(
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
                val addressesViewModel = viewModel(
                    factory = viewModel.addresses(
                        openAddAddress = { router.openAddAddress() }
                    )
                )

                AddressesView(
                    setAppBarState = setAppBarState,
                    viewModel = addressesViewModel
                )
            }

            composable(
                "addaddress"
            ) {backstackEntry ->
                val addAddressViewModel = viewModel(
                    factory = viewModel.addAddress(
                    )
                )

                AddAddressView(
                    setAppBarState = setAppBarState,
                    viewModel = addAddressViewModel
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
    ) = object : ViewModelFactory<ProductListViewModel>() {
        override fun create() = ProductListViewModel(
            productRepository = productRepository,
            selectProduct = openProductDetails
        )
    }

    fun cart(
        openProductDetails: (ProductID) -> Unit,
        openCheckout: () -> Unit,
    ) = object : ViewModelFactory<CartViewModel>() {
        override fun create() = CartViewModel(
            repository = cartRepository,
            selectItem = openProductDetails,
            proceedToCheckout = openCheckout
        )
    }

    fun productDetails(
        id: ProductID,
        viewCart: () -> Unit
    ) = object : ViewModelFactory<ProductDetailViewModel>() {
        override fun create() = ProductDetailViewModel(
            productID = id,
            repository = productRepository,
            viewCart = viewCart
        )
    }

    fun checkout(
        viewOrder: (OrderID) -> Unit
    ) = object : ViewModelFactory<CheckoutViewModel>() {
        override fun create() = CheckoutViewModel(
            cartRepository = cartRepository,
            userRepository = userRepository,
            viewOrder = viewOrder
        )
    }

    fun orders(
        viewOrder: (OrderID) -> Unit
    ) = object : ViewModelFactory<OrdersViewModel>() {
        override fun create() = OrdersViewModel(
            repository = orderRepository,
            viewOrder = viewOrder
        )
    }

    fun orderDetails(
        id: OrderID,
        viewProductDetails: (ProductID) -> Unit
    ) = object : ViewModelFactory<OrderDetailsViewModel>() {
        override fun create() = OrderDetailsViewModel(
            orderID = id,
            repository = orderRepository,
            selectItem = viewProductDetails
        )
    }

    fun settings(
        openAddresses: () -> Unit,
        openPaymentMethods: () -> Unit,
    ) = object : ViewModelFactory<SettingsViewModel>() {
        override fun create() = SettingsViewModel(
            openAddresses = openAddresses,
            openPaymentMethods = openPaymentMethods
        )
    }

    fun addresses(
        openAddAddress: () -> Unit
    ) = object : ViewModelFactory<AddressesViewModel>() {
        override fun create() = AddressesViewModel(
            repository = userRepository,
            openAddAddress = openAddAddress
        )
    }

    fun addAddress() = object : ViewModelFactory<AddAddressViewModel>() {
        override fun create() = AddAddressViewModel(
            repository = userRepository
        )
    }
}