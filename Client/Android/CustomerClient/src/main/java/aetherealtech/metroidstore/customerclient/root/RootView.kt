package aetherealtech.metroidstore.customerclient.root

import aetherealtech.metroidstore.customerclient.editaddress.EditAddressView
import aetherealtech.metroidstore.customerclient.editaddress.EditAddressViewModel
import aetherealtech.metroidstore.customerclient.addresses.AddressesView
import aetherealtech.metroidstore.customerclient.addresses.AddressesViewModel
import aetherealtech.metroidstore.customerclient.cart.CartViewModel
import aetherealtech.metroidstore.customerclient.checkout.CheckoutView
import aetherealtech.metroidstore.customerclient.checkout.CheckoutViewModel
import aetherealtech.metroidstore.customerclient.datasources.DataSource
import aetherealtech.metroidstore.customerclient.editpaymentmethod.EditPaymentMethodView
import aetherealtech.metroidstore.customerclient.editpaymentmethod.EditPaymentMethodViewModel
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.orderdetails.OrderDetailsView
import aetherealtech.metroidstore.customerclient.orderdetails.OrderDetailsViewModel
import aetherealtech.metroidstore.customerclient.orders.OrdersViewModel
import aetherealtech.metroidstore.customerclient.paymentmethods.PaymentMethodsView
import aetherealtech.metroidstore.customerclient.paymentmethods.PaymentMethodsViewModel
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
import aetherealtech.metroidstore.customerclient.ui.theme.Colors
import aetherealtech.metroidstore.customerclient.utilities.AddressIDType
import aetherealtech.metroidstore.customerclient.utilities.OrderIDType
import aetherealtech.metroidstore.customerclient.utilities.PaymentMethodIDType
import aetherealtech.metroidstore.customerclient.utilities.ProductIDType
import aetherealtech.metroidstore.customerclient.utilities.ViewModelFactory
import aetherealtech.metroidstore.customerclient.utilities.getAddressID
import aetherealtech.metroidstore.customerclient.utilities.getOrderID
import aetherealtech.metroidstore.customerclient.utilities.getPaymentMethodID
import aetherealtech.metroidstore.customerclient.utilities.getProductID
import aetherealtech.metroidstore.customerclient.utilities.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

            val barModifier = Modifier
                .height(72.dp)

            CenterAlignedTopAppBar(
                title = {
                    Box(
                        modifier = barModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        router.topBarState.title()
                    }
                },
                modifier = barModifier,
                navigationIcon = {
                    if(router.previousBackStackEntry != null) {
                        IconButton(onClick = { router.back() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                tint = Colors.BarForeground,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = router.topBarState.actions,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Colors.BarBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Colors.BarBackground
            ) {
                val navBackStackEntry by router.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Screen.all.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon(), contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { router.selectTab(screen) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Colors.BarForegroundSelected,
                            selectedTextColor = Colors.BarForegroundSelected,
                            indicatorColor = Colors.BarBackgroundSelected,
                            unselectedIconColor = Colors.BarForeground,
                            unselectedTextColor = Colors.BarForeground
                        )
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
                    setAppBarState = setAppBarState,
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
                    setAppBarState = setAppBarState,
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
                    setAppBarState = setAppBarState,
                    viewModel = detailsViewModel
                )
            }

            composable(
                "addresses"
            ) {backstackEntry ->
                val addressesViewModel = viewModel(
                    factory = viewModel.addresses(
                        openAddAddress = { router.openAddAddress() },
                        openEditAddress = { id -> router.openEditAddress(id) }
                    )
                )

                AddressesView(
                    setAppBarState = setAppBarState,
                    viewModel = addressesViewModel
                )
            }

            composable(
                "addAddress"
            ) {backstackEntry ->
                val addAddressViewModel = viewModel(
                    factory = viewModel.addAddress(
                        onSaveComplete = { router.back() }
                    )
                )

                EditAddressView(
                    setAppBarState = setAppBarState,
                    viewModel = addAddressViewModel
                )
            }

            composable(
                "editAddress/{addressID}",
                arguments = listOf(navArgument("addressID") { type = NavType.AddressIDType })
            ) {backstackEntry ->
                val addressID = backstackEntry.arguments!!.getAddressID("addressID")

                val addAddressViewModel = viewModel(
                    factory = viewModel.editAddress(
                        id = addressID,
                        onSaveComplete = { router.back() }
                    )
                )

                EditAddressView(
                    setAppBarState = setAppBarState,
                    viewModel = addAddressViewModel
                )
            }

            composable(
                "paymentMethods"
            ) { backstackEntry ->
                val paymentMethodsViewModel = viewModel(
                    factory = viewModel.paymentMethods(
                        openAddPaymentMethod = { router.openAddPaymentMethod() },
                        openEditPaymentMethod = { id -> router.openEditPaymentMethod(id) }
                    )
                )

                PaymentMethodsView(
                    setAppBarState = setAppBarState,
                    viewModel = paymentMethodsViewModel
                )
            }

            composable(
                "addPaymentMethod"
            ) {backstackEntry ->
                val addPaymentMethodViewModel = viewModel(
                    factory = viewModel.addPaymentMethod(
                        onSaveComplete = { router.back() }
                    )
                )

                EditPaymentMethodView(
                    setAppBarState = setAppBarState,
                    viewModel = addPaymentMethodViewModel
                )
            }

            composable(
                "editPaymentMethod/{paymentMethodID}",
                arguments = listOf(navArgument("paymentMethodID") { type = NavType.PaymentMethodIDType })
            ) {backstackEntry ->
                val paymentMethodID = backstackEntry.arguments!!.getPaymentMethodID("paymentMethodID")

                val addPaymentMethodViewModel = viewModel(
                    factory = viewModel.editPaymentMethod(
                        id = paymentMethodID,
                        onSaveComplete = { router.back() }
                    )
                )

                EditPaymentMethodView(
                    setAppBarState = setAppBarState,
                    viewModel = addPaymentMethodViewModel
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
        openAddAddress: () -> Unit,
        openEditAddress: (Address.ID) -> Unit
    ) = object : ViewModelFactory<AddressesViewModel>() {
        override fun create() = AddressesViewModel(
            repository = userRepository,
            openAddAddress = openAddAddress,
            openEditAddress = openEditAddress
        )
    }

    fun addAddress(
        onSaveComplete: () -> Unit
    ) = object : ViewModelFactory<EditAddressViewModel>() {
        override fun create() = EditAddressViewModel.new(
            repository = userRepository,
            onSaveComplete = onSaveComplete
        )
    }

    fun editAddress(
        id: Address.ID,
        onSaveComplete: () -> Unit
    ) = object : ViewModelFactory<EditAddressViewModel>() {
        override fun create() = EditAddressViewModel.edit(
            id = id,
            repository = userRepository,
            onSaveComplete = onSaveComplete
        )
    }

    fun paymentMethods(
        openAddPaymentMethod: () -> Unit,
        openEditPaymentMethod: (PaymentMethodID) -> Unit
    ) = object : ViewModelFactory<PaymentMethodsViewModel>() {
        override fun create() = PaymentMethodsViewModel(
            repository = userRepository,
            openAddPaymentMethod = openAddPaymentMethod,
            openEditPaymentMethod = openEditPaymentMethod
        )
    }

    fun addPaymentMethod(
        onSaveComplete: () -> Unit
    ) = object : ViewModelFactory<EditPaymentMethodViewModel>() {
        override fun create() = EditPaymentMethodViewModel.new(
            repository = userRepository,
            onSaveComplete = onSaveComplete
        )
    }

    fun editPaymentMethod(
        id: PaymentMethodID,
        onSaveComplete: () -> Unit
    ) = object : ViewModelFactory<EditPaymentMethodViewModel>() {
        override fun create() = EditPaymentMethodViewModel.edit(
            id = id,
            repository = userRepository,
            onSaveComplete = onSaveComplete
        )
    }
}