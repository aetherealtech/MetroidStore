package aetherealtech.metroidstore.customerclient.ui.addresses

import aetherealtech.metroidstore.customerclient.model.Address
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.ui.addressrow.AddressRowViewModel
import aetherealtech.metroidstore.customerclient.utilities.mapState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AddressesViewModel(
    private val repository: UserRepository,
    val openAddAddress: () -> Unit,
    val openEditAddress: (Address.ID) -> Unit
): ViewModel() {
    val items = repository.addressDetails
        .mapState { addressDetailsList ->
            addressDetailsList.map { addressDetails ->
                AddressRowViewModel(
                    details = addressDetails,
                    select = { openEditAddress(addressDetails.address.id) }
                )
            }
        }

    init {
        viewModelScope.launch {
            repository.updateAddressDetails()
        }
    }

    fun delete(item: AddressRowViewModel) {
        viewModelScope.launch {
            repository.deleteAddress(item.id)
        }
    }
}