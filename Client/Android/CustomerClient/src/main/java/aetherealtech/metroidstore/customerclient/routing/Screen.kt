package aetherealtech.metroidstore.customerclient.routing

import aetherealtech.metroidstore.customerclient.cart.CartView
import aetherealtech.metroidstore.customerclient.cart.CartViewModel
import aetherealtech.metroidstore.customerclient.orders.OrdersView
import aetherealtech.metroidstore.customerclient.orders.OrdersViewModel
import aetherealtech.metroidstore.customerclient.productlist.ProductListView
import aetherealtech.metroidstore.customerclient.productlist.ProductListViewModel
import aetherealtech.metroidstore.customerclient.root.RootViewModel
import aetherealtech.metroidstore.customerclient.settings.SettingsView
import aetherealtech.metroidstore.customerclient.settings.SettingsViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aetherealtech.metroidstore.customerclient.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class Screen(
    val title: String,
    val icon: @Composable () -> ImageVector,
    val route: String,
    val content: @Composable (Router, (AppBarState) -> Unit, RootViewModel) -> Unit
) {
    constructor(
        title: String,
        icon: ImageVector,
        route: String,
        content: @Composable (Router, (AppBarState) -> Unit, RootViewModel) -> Unit
    ) : this(
        title = title,
        icon = { icon },
        route = route,
        content = content
    )

    data object ProductList : Screen(
        title = "Browse",
        icon = Icons.Filled.List,
        route = "productList",
        content = { router, _, rootViewModel ->
            val productListViewModel = viewModel<ProductListViewModel>(
                factory = rootViewModel.productList(
                    openProductDetails = { productID -> router.viewProductDetails(productID) }
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
        content = { router, _, rootViewModel ->
            val cartViewModel = viewModel<CartViewModel>(
                factory = rootViewModel.cart(
                    openProductDetails = { productID -> router.viewProductDetails(productID) },
                    openCheckout = { router.openCheckout() }
                )
            )

            CartView(
                viewModel = cartViewModel
            )
        }
    )

    data object Orders : Screen(
        title = "Orders",
        icon = { ImageVector.vectorResource(R.drawable.baseline_receipt_long_24) },
        route = "orders",
        content = { router, _, rootViewModel ->
            val ordersViewModel = viewModel<OrdersViewModel>(
                factory = rootViewModel.orders(
                    viewOrder = { orderID -> router.viewOrder(orderID) }
                )
            )

            OrdersView(
                viewModel = ordersViewModel
            )
        }
    )

    data object Settings : Screen(
        title = "Settings",
        icon = Icons.Filled.Settings,
        route = "settings",
        content = { router, _, rootViewModel ->
            val settingsViewModel = viewModel<SettingsViewModel>(
                factory = rootViewModel.settings(
                    openAddresses = { router.openAddresses() },
                    openPaymentMethods = { router.openPaymentMethods() }
                )
            )

            SettingsView(
                viewModel = settingsViewModel
            )
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