package aetherealtech.metroidstore.customerclient.ui.checkout

import aetherealtech.metroidstore.customerclient.model.NewOrder
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.PaymentMethodSummary
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
import aetherealtech.metroidstore.customerclient.model.UserAddressSummary
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.ui.placeorderbutton.PlaceOrderViewModel
import aetherealtech.kotlinflowsextensions.StateFlows
import aetherealtech.kotlinflowsextensions.mapState
import aetherealtech.metroidstore.customerclient.widgets.DropDownViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

class CheckoutViewModel(
    cartRepository: CartRepository,
    userRepository: UserRepository,
    viewOrder: (OrderID) -> Unit,
    addNewAddress: () -> Unit,
    addNewPaymentMethod: () -> Unit
): ViewModel() {
    data class AddressViewModel(
        val address: UserAddressSummary
    ) {
        val displayName = address.name
    }

    data class ShippingMethodViewModel(
        val method: ShippingMethod
    ) {
        val displayName = method.name
    }

    data class PaymentMethodViewModel(
        val method: PaymentMethodSummary
    ) {
        val displayName = method.name
    }

    val summary: CheckoutSummaryViewModel

    val addresses: DropDownViewModel<AddressViewModel>
    val shippingMethods: DropDownViewModel<ShippingMethodViewModel>
    val paymentMethods: DropDownViewModel<PaymentMethodViewModel>

    val placeOrder: PlaceOrderViewModel

    val busy = userRepository.busy

    init {
        val cart = cartRepository.cart

        addresses = DropDownViewModel(
            title = "Shipping Address",
            options = userRepository.addresses
                .mapState { addresses ->
                    addresses.map { address ->
                        AddressViewModel(
                            address = address
                        )
                    }
                        .toImmutableList()
                },
            additionalOption = { addNewAddress() }
        )

        shippingMethods = DropDownViewModel(
            title = "Shipping Method",
            options = userRepository.shippingMethods
                .mapState { shippingMethods ->
                    shippingMethods.map { shippingMethod ->
                        ShippingMethodViewModel(
                            method = shippingMethod
                        )
                    }
                        .toImmutableList()
                }
        )

        paymentMethods = DropDownViewModel(
            title = "Payment Method",
            options = userRepository.paymentMethods
                .mapState { paymentMethods ->
                    paymentMethods.map { paymentMethod ->
                        PaymentMethodViewModel(
                            method = paymentMethod
                        )
                    }
                        .toImmutableList()
                },
            additionalOption = { addNewPaymentMethod() }
        )

        summary = CheckoutSummaryViewModel(
            cart = cart,
            shippingMethod = shippingMethods.selection.mapState { viewModel -> viewModel?.method }
        )

        val order = StateFlows.combine(
            addresses.selection,
            shippingMethods.selection,
            paymentMethods.selection
        )
            .mapState { selections ->
                val address = selections.first
                val shippingMethod = selections.second
                val paymentMethod = selections.third

                if(address == null || shippingMethod == null || paymentMethod == null)
                    return@mapState null

                return@mapState NewOrder(
                    addressID = address.address.addressID,
                    shippingMethod = shippingMethod.method,
                    paymentMethodID = paymentMethod.method.id
                )
            }

        placeOrder = PlaceOrderViewModel(
            order = order,
            userRepository = userRepository,
            viewOrder = viewOrder
        )

        viewModelScope.launch {
            userRepository.updateAddresses()
            userRepository.updateShippingMethods()
            userRepository.updatePaymentMethods()

            addresses.selection.value = addresses.options.value
                .find { viewModel -> viewModel.address.isPrimary }

            paymentMethods.selection.value = paymentMethods.options.value
                .find { paymentMethod -> paymentMethod.method.isPrimary }
        }
    }
}