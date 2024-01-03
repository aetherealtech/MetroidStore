package aetherealtech.metroidstore.customerclient.ui.orderdetails

import aetherealtech.metroidstore.customerclient.model.OrderDetails
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.ui.orderactivity.OrderActivityViewModel
import aetherealtech.metroidstore.customerclient.ui.orderitemrow.OrderItemRowViewModel
import aetherealtech.metroidstore.customerclient.utilities.displayString
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    val orderID: OrderID,
    private val repository: OrderRepository,
    selectItem: (ProductID) -> Unit
): ViewModel() {
    private val _content = MutableStateFlow<OrderDetailsContentViewModel?>(null)

    val content = _content
        .asStateFlow()

    init {
        viewModelScope.launch {
            val orderDetails = repository.getOrder(orderID)

            _content.value = OrderDetailsContentViewModel(
                orderDetails = orderDetails,
                selectItem = selectItem,
                activityViewModelFactory = {
                    OrderActivityViewModel(
                        orderID = orderID,
                        repository = repository
                    )
                }
            )
        }
    }
}

class OrderDetailsContentViewModel(
    orderDetails: OrderDetails,
    selectItem: (ProductID) -> Unit,
    activityViewModelFactory: () -> OrderActivityViewModel
): ViewModel() {
    val summary = OrderDetailsSummaryViewModel(
        orderDetails = orderDetails,
        activityViewModelFactory = activityViewModelFactory
    )

    val items = orderDetails.items
        .map { item ->
            OrderItemRowViewModel(
                item = item,
                select = { selectItem(item.productID) }
            )
        }
        .toImmutableList()
}

class OrderDetailsSummaryViewModel(
    orderDetails: OrderDetails,
    private val activityViewModelFactory: () -> OrderActivityViewModel
): ViewModel() {
    private val _activity = MutableSharedFlow<OrderActivityViewModel>()

    val date: String
    val status: String
    val total: PriceViewModel
    val address: String
    val shippingMethod: String
    val paymentMethod: String

    val activity = _activity
        .asSharedFlow()

    init {
        date = orderDetails.date.displayString
        status = orderDetails.latestStatus.value

        total = PriceViewModel(orderDetails.total)

        address = "Deliver to: ${orderDetails.address}"
        shippingMethod = "Shipping Method: ${orderDetails.shippingMethod}"
        paymentMethod = "Payment Method: ${orderDetails.paymentMethod}"
    }

    fun viewActivity() {
        viewModelScope.launch {
            _activity.emit(activityViewModelFactory())
        }
    }
}