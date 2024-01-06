package aetherealtech.metroidstore.customerclient.ui.checkout

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
import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.model.CartItem
import aetherealtech.metroidstore.customerclient.model.Percent
import aetherealtech.metroidstore.customerclient.model.Price
import aetherealtech.metroidstore.customerclient.model.ShippingMethod
import aetherealtech.metroidstore.customerclient.model.subtotal
import aetherealtech.metroidstore.customerclient.ui.placeorderbutton.PlaceOrderButton
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.kotlinflowsextensions.StateFlows
import aetherealtech.kotlinflowsextensions.mapState
import aetherealtech.metroidstore.customerclient.widgets.BusyView
import aetherealtech.metroidstore.customerclient.widgets.DropDownList
import aetherealtech.metroidstore.customerclient.widgets.PriceView
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import androidx.compose.runtime.LaunchedEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CheckoutView(
    modifier: Modifier = Modifier,
    setAppBarState: (AppBarState) -> Unit,
    viewModel: CheckoutViewModel
) {
    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = "Checkout"
        ))
    }

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
            setAppBarState = { },
            viewModel = CheckoutViewModel(
                cartRepository = CartRepository(
                    dataSource = DataSourceFake().cart
                ),
                userRepository = UserRepository(
                    dataSource = DataSourceFake().user
                ),
                viewOrder = { },
                addNewAddress = { },
                addNewPaymentMethod = { }
            )
        )
    }
}