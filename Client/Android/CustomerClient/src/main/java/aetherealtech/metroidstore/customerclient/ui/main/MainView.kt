package aetherealtech.metroidstore.customerclient.ui.main

import aetherealtech.metroidstore.customerclient.ui.editaddress.EditAddressView
import aetherealtech.metroidstore.customerclient.ui.addresses.AddressesView
import aetherealtech.metroidstore.customerclient.ui.checkout.CheckoutView
import aetherealtech.metroidstore.customerclient.ui.editpaymentmethod.EditPaymentMethodView
import aetherealtech.metroidstore.customerclient.ui.orderdetails.OrderDetailsView
import aetherealtech.metroidstore.customerclient.ui.paymentmethods.PaymentMethodsView
import aetherealtech.metroidstore.customerclient.ui.productdetail.ProductDetailView
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.routing.Screen
import aetherealtech.metroidstore.customerclient.routing.rememberRouter
import aetherealtech.metroidstore.customerclient.theme.Colors
import aetherealtech.metroidstore.customerclient.utilities.AddressIDType
import aetherealtech.metroidstore.customerclient.utilities.OrderIDType
import aetherealtech.metroidstore.customerclient.utilities.PaymentMethodIDType
import aetherealtech.metroidstore.customerclient.utilities.ProductIDType
import aetherealtech.metroidstore.customerclient.utilities.getAddressID
import aetherealtech.metroidstore.customerclient.utilities.getOrderID
import aetherealtech.metroidstore.customerclient.utilities.getPaymentMethodID
import aetherealtech.metroidstore.customerclient.utilities.getProductID
import aetherealtech.metroidstore.customerclient.utilities.viewModel
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    viewModel: MainViewModel
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
            ) {
                val detailsViewModel = viewModel(
                    factory = viewModel.checkout(
                        viewOrder = { orderID ->
                            router.selectTab(Screen.Orders)
                            router.viewOrder(orderID)
                        },
                        addNewAddress = { router.openAddAddress() },
                        addNewPaymentMethod = { router.openAddPaymentMethod() }
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
            ) {
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
            ) {
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
            ) {
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
            ) {
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

