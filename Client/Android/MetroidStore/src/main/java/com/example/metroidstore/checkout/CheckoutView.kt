package com.example.metroidstore.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.example.metroidstore.fakedatasources.DataSourceFake
import com.example.metroidstore.model.Address
import com.example.metroidstore.model.CartItem
import com.example.metroidstore.model.PaymentMethod
import com.example.metroidstore.model.Percent
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.ShippingMethod
import com.example.metroidstore.model.UserAddressSummary
import com.example.metroidstore.model.subtotal
import com.example.metroidstore.repositories.CartRepository
import com.example.metroidstore.ui.theme.MetroidStoreTheme
import com.example.metroidstore.utilities.StateFlows
import com.example.metroidstore.utilities.mapState
import com.example.metroidstore.widgets.DropDownList
import com.example.metroidstore.widgets.DropDownViewModel
import com.example.metroidstore.widgets.PriceView
import com.example.metroidstore.widgets.PriceViewModel
import com.example.metroidstore.widgets.PrimaryCallToAction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CheckoutView(
    modifier: Modifier = Modifier,
    viewModel: CheckoutViewModel
) {
    val canPlaceOrder by viewModel.canPlaceOrder.collectAsState()

    @Composable
    fun PricePartView(
        size: TextUnit = 16.sp,
        title: String,
        price: PriceViewModel,
        combiner: String? = null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$title: ", fontSize = size)
            PriceView(price, size = size)
            if(combiner != null) {
                Text(" $combiner", fontSize = size)
            }
        }
    }

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

        Box(
            modifier = Modifier.fillMaxWidth(0.75f)
        ) {
            PrimaryCallToAction(
                enabled = canPlaceOrder,
                onClick = { viewModel.placeOrder() },
                text = "Place Order"
            )
        }
    }
}

class CheckoutViewModel(
    cartRepository: CartRepository
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
        val method: PaymentMethod
    ) {
        val displayName = method.name
    }

    val summary: CheckoutSummaryViewModel

    val addresses: DropDownViewModel<AddressViewModel>
    val shippingMethods: DropDownViewModel<ShippingMethodViewModel>
    val paymentMethods: DropDownViewModel<PaymentMethodViewModel>

    val canPlaceOrder: StateFlow<Boolean>

    init {
        val fakeAddresses = listOf(
            UserAddressSummary(Address.ID(0), "Home"),
            UserAddressSummary(Address.ID(0), "Office"),
            UserAddressSummary(Address.ID(0), "Secret Lair"),
        )

        val fakeShippingMethods = listOf(
            ShippingMethod("Slow", Price(1000)),
            ShippingMethod("Fast", Price(5000)),
        )

        val fakePaymentMethods = listOf(
            PaymentMethod(PaymentMethod.ID(0), "Credit"),
            PaymentMethod(PaymentMethod.ID(1), "Theft"),
        )

        val cart = cartRepository.cart

        addresses = DropDownViewModel(
            title = "Shipping Address",
            options = fakeAddresses
                .map { address -> AddressViewModel(
                    address = address
                ) }
                .toImmutableList(),
            additionalOption = { addNewAddress() }
        )

        shippingMethods = DropDownViewModel(
            title = "Shipping Method",
            options = fakeShippingMethods
                .map { shippingMethod -> ShippingMethodViewModel(
                    method = shippingMethod
                ) }
                .toImmutableList()
        )

        paymentMethods = DropDownViewModel(
            title = "Payment Method",
            options = fakePaymentMethods
                .map { paymentMethod -> PaymentMethodViewModel(
                    method = paymentMethod
                ) }
                .toImmutableList(),
            additionalOption = { addNewPaymentMethod() }
        )

        summary = CheckoutSummaryViewModel(
            cart = cart,
            shippingMethod = shippingMethods.selection.mapState { viewModel -> viewModel?.method }
        )

        canPlaceOrder = StateFlows
            .combine(
                addresses.selection,
                shippingMethods.selection,
                paymentMethods.selection
            )
            .mapState { selections ->
                !listOf(
                    selections.first != null,
                    selections.second != null,
                    selections.third != null
                ).contains(false)
            }
    }

    fun placeOrder() {

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
                )
            )
        )
    }
}