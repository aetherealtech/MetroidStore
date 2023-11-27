package aetherealtech.metroidstore.customerclient.routing

import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.ProductID
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class Router(
    private val navController: NavHostController,
    private val _topBarState: MutableState<AppBarState>
) {
    init {
        CoroutineScope(Dispatchers.Default).launch {
            navController.currentBackStack
                .drop(1)
                .collect {
                    _topBarState.value = AppBarState.default
                }
        }
    }

    val previousBackStackEntry: NavBackStackEntry?
        get() {
            return navController.previousBackStackEntry
        }

    val topBarState: AppBarState
        get() {
            return _topBarState.value
        }

    fun selectTab(screen: Screen) {
        navController.navigate(screen.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            navController.graph.setStartDestination(screen.route)
            launchSingleTop = true
            restoreState = true
        }
    }

    fun viewProductDetails(productID: ProductID) {
        navController.navigate("productDetails/${productID.value}")
    }

    fun viewOrder(orderID: OrderID) {
        navController.navigate("orders/${orderID.value}")
    }

    fun openCheckout() {
        navController.navigate("checkout")
    }

    fun openAddresses() {
        navController.navigate("addresses")
    }

    fun openPaymentMethods() {

    }

    fun back() {
        navController.popBackStack()
    }

    @Composable
    fun currentBackStackEntryAsState(): State<NavBackStackEntry?> {
        return navController.currentBackStackEntryAsState()
    }

    fun setAppBarState(
        newState: AppBarState
    ) {
        _topBarState.value = newState
    }

    @Composable
    fun NavHost(
        startDestination: String,
        modifier: Modifier = Modifier,
        builder: NavGraphBuilder.() -> Unit
    ) {
        androidx.navigation.compose.NavHost(
            navController,
            startDestination = startDestination,
            modifier,
            builder = builder
        )
    }
}

@Composable
fun rememberRouter(): Router {
    return Router(
        navController = rememberNavController(),
        _topBarState = remember { mutableStateOf(AppBarState.default) }
    )
}