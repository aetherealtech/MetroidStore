package com.example.metroidstore.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroidstore.fakedatasources.DataSourceFake
import com.example.metroidstore.model.CartItem
import com.example.metroidstore.model.NewOrder
import com.example.metroidstore.model.OrderID
import com.example.metroidstore.model.PaymentMethodSummary
import com.example.metroidstore.model.Percent
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.ShippingMethod
import com.example.metroidstore.model.UserAddressSummary
import com.example.metroidstore.model.subtotal
import com.example.metroidstore.orderconfirmation.PlaceOrderButton
import com.example.metroidstore.orderconfirmation.PlaceOrderViewModel
import com.example.metroidstore.repositories.CartRepository
import com.example.metroidstore.repositories.UserRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import com.example.metroidstore.utilities.StateFlows
import com.example.metroidstore.utilities.mapState
import com.example.metroidstore.widgets.BusyView
import com.example.metroidstore.widgets.DropDownList
import com.example.metroidstore.widgets.DropDownViewModel
import com.example.metroidstore.widgets.PriceView
import com.example.metroidstore.widgets.PriceViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun CheckoutView(
    modifier: Modifier = Modifier,
    viewModel: CheckoutViewModel
) {
    BusyView(
        busy = viewModel.busy
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CheckoutSummaryView(
                viewModel = viewModel.summary
            )

            DropDownList(
                viewModel = viewModel.addresses,
                optionDisplay = { address -> address.displayName },
                additionalOption = { Text("Add New Address") }
            )

            DropDownList(
                viewModel = viewModel.shippingMethods,
                optionDisplay = { shippingMethod -> shippingMethod.displayName }
            )

            DropDownList(
                viewModel = viewModel.paymentMethods,
                optionDisplay = { paymentMethod -> paymentMethod.displayName },
                additionalOption = { Text("Add New Payment Method") }
            )

            PlaceOrderButton(
                viewModel = viewModel.placeOrder
            )
        }
    }
}

class CheckoutViewModel(
    cartRepository: CartRepository,
    userRepository: UserRepository,
    viewOrder: (OrderID) -> Unit
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

        val order = StateFlows
            .combine(
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

    fun addNewAddress() {

    }

    fun addNewPaymentMethod() {

    }
}

@Composable
fun CheckoutSummaryView(
    viewModel: CheckoutSummaryViewModel
) {
    val subtotal by viewModel.subtotal.collectAsState()
    val shipping by viewModel.shipping.collectAsState()
    val taxes by viewModel.taxes.collectAsState()
    val total by viewModel.total.collectAsState()

    @Composable
    fun PricePartView(
        size: TextUnit = 16.sp,
        title: String,
        price: PriceViewModel?,
        combiner: String? = null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$title: ", fontSize = size)
            price?.let {
                PriceView(price, size = size)
            } ?: Text(text = "???", fontSize = size, fontWeight = FontWeight.Bold)
            if(combiner != null) {
                Text(" $combiner", fontSize = size)
            }
        }
    }

    Column {
        PricePartView(
            title = "Subtotal",
            price = subtotal,
            combiner = "+"
        )
        PricePartView(
            title = "Shipping",
            price = shipping,
            combiner = "+"
        )
        PricePartView(
            title = "Taxes",
            price = taxes,
            combiner = "="
        )
        Spacer(modifier = Modifier.height(8.dp))
        PricePartView(
            title = "Total",
            price = total,
            size = 24.sp
        )
    }
}

class CheckoutSummaryViewModel(
    cart: StateFlow<ImmutableList<CartItem>>,
    shippingMethod: StateFlow<ShippingMethod?>
): ViewModel() {
    val subtotal: StateFlow<PriceViewModel>
    val shipping: StateFlow<PriceViewModel?>
    val taxes: StateFlow<PriceViewModel>
    val total: StateFlow<PriceViewModel>

    init {
        val subtotal = cart.mapState { cart -> cart.subtotal }
        val shipping = shippingMethod.mapState { method -> method?.cost }

        val taxRate = Percent(10)

        val base = StateFlows.combine(subtotal, shipping)
            .mapState { subtotalAndShipping ->
                (subtotalAndShipping.first + (subtotalAndShipping.second ?: Price.zero))
            }

        val taxes = base
            .mapState { value -> value * taxRate }

        val total = StateFlows.combine(base, taxes)
            .mapState { baseAndTaxes -> baseAndTaxes.first + baseAndTaxes.second }

        this.subtotal = subtotal.mapState { value -> PriceViewModel(value) }
        this.shipping = shipping.mapState { value -> value?.let { value -> PriceViewModel(value) } }
        this.taxes = taxes.mapState { value -> PriceViewModel(value) }
        this.total = total.mapState { value -> PriceViewModel(value) }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutPreview() {
    MetroidStoreTheme {
        CheckoutView(
            viewModel = CheckoutViewModel(
                cartRepository = CartRepository(
                    dataSource = DataSourceFake().cart
                ),
                userRepository = UserRepository(
                    dataSource = DataSourceFake().user
                ),
                viewOrder = { }
            )
        )
    }
}