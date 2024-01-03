package aetherealtech.metroidstore.customerclient.ui.main

import aetherealtech.metroidstore.customerclient.datasources.api.DataSource
import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.PaymentMethodID
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.repositories.CartRepository
import aetherealtech.metroidstore.customerclient.repositories.OrderRepository
import aetherealtech.metroidstore.customerclient.repositories.ProductRepository
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.ui.addresses.AddressesViewModel
import aetherealtech.metroidstore.customerclient.ui.cart.CartViewModel
import aetherealtech.metroidstore.customerclient.ui.checkout.CheckoutViewModel
import aetherealtech.metroidstore.customerclient.ui.editaddress.EditAddressViewModel
import aetherealtech.metroidstore.customerclient.ui.editpaymentmethod.EditPaymentMethodViewModel
import aetherealtech.metroidstore.customerclient.ui.orderdetails.OrderDetailsViewModel
import aetherealtech.metroidstore.customerclient.ui.orders.OrdersViewModel
import aetherealtech.metroidstore.customerclient.ui.paymentmethods.PaymentMethodsViewModel
import aetherealtech.metroidstore.customerclient.ui.productdetail.ProductDetailViewModel
import aetherealtech.metroidstore.customerclient.ui.productlist.ProductListViewModel
import aetherealtech.metroidstore.customerclient.ui.settings.SettingsViewModel
import aetherealtech.metroidstore.customerclient.utilities.ViewModelFactory
import androidx.lifecycle.ViewModel

class MainViewModel(
    val dataSource: DataSource,
    val logout: () -> Unit
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
        viewOrder: (OrderID) -> Unit,
        addNewAddress: () -> Unit,
        addNewPaymentMethod: () -> Unit
    ) = object : ViewModelFactory<CheckoutViewModel>() {
        override fun create() = CheckoutViewModel(
            cartRepository = cartRepository,
            userRepository = userRepository,
            viewOrder = viewOrder,
            addNewAddress = addNewAddress,
            addNewPaymentMethod = addNewPaymentMethod
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
        logout: () -> Unit
    ) = object : ViewModelFactory<SettingsViewModel>() {
        override fun create() = SettingsViewModel(
            openAddresses = openAddresses,
            openPaymentMethods = openPaymentMethods,
            logout = logout
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