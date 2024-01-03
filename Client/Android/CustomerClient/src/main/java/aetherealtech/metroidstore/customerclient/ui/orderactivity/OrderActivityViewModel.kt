package aetherealtech.metroidstore.customerclient.ui.orderactivity

import aetherealtech.metroidstore.customerclient.model.OrderActivity
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.OrderStatus
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.utilities.displayString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderActivityViewModel(
    orderID: OrderID,
    repository: OrderRepository
): ViewModel() {
    private val _content = MutableStateFlow<OrderActivityContentViewModel?>(null)

    val content = _content
        .asStateFlow()

    init {
        viewModelScope.launch {
            val activities = repository.getOrderActivity(orderID)

            _content.value = OrderActivityContentViewModel(
                activities = activities
            )
        }
    }
}

class OrderActivityContentViewModel(
    activities: ImmutableList<OrderActivity>
): ViewModel() {
    val currentStatus: String
    val currentStateDate: String

    val statuses: ImmutableList<String>
    val activeStatusIndex: Int

    init {
        val currentActivity = activities.last()

        currentStatus = currentActivity.status.value
        currentStateDate = currentActivity.date.displayString

        val statuses = activities
            .map { activity -> activity.status }
            .toMutableList()

        if(!statuses.contains(OrderStatus.SHIPPED))
            statuses.add(OrderStatus.SHIPPED)

        if(!statuses.contains(OrderStatus.DELIVERED) &&
            !statuses.contains(OrderStatus.CANCELLED)
        )
            statuses.add(OrderStatus.DELIVERED)

        this.statuses = statuses
            .map { status -> status.value }
            .toImmutableList()

        activeStatusIndex = this.statuses
            .indexOf(currentStatus)
    }
}