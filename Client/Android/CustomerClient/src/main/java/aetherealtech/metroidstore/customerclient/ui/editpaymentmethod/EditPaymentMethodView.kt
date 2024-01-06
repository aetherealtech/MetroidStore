package aetherealtech.metroidstore.customerclient.ui.editpaymentmethod

import aetherealtech.metroidstore.customerclient.datasources.fake.DataSourceFake
import aetherealtech.metroidstore.customerclient.repositories.UserRepository
import aetherealtech.metroidstore.customerclient.routing.AppBarState
import aetherealtech.metroidstore.customerclient.theme.MetroidStoreTheme
import aetherealtech.metroidstore.customerclient.widgets.PrimaryCallToAction
import aetherealtech.androiduitoolkit.BusyView
import aetherealtech.androiduitoolkit.LabeledSwitch
import aetherealtech.androiduitoolkit.LabeledValidatedTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EditPaymentMethodView(
    setAppBarState: (AppBarState) -> Unit,
    viewModel: EditPaymentMethodViewModel
) {
    LaunchedEffect(Unit) {
        setAppBarState(AppBarState(
            title = "Add PaymentMethod"
        ))
    }

    val create by viewModel.create.collectAsState()

    BusyView(
        busy = viewModel.busy
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabeledValidatedTextField(
                    name = "Name",
                    value = viewModel.name
                )
                LabeledValidatedTextField(
                    name = "Number",
                    value = viewModel.number
                )

                LabeledSwitch(
                    name = "Primary",
                    value = viewModel.isPrimary
                )
            }

            PrimaryCallToAction(
                modifier = Modifier
                    .width(128.dp),
                onClick = create,
                text = "Save"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPaymentMethodPreview() {
    MetroidStoreTheme {
        EditPaymentMethodView(
            setAppBarState = { },
            viewModel = EditPaymentMethodViewModel.new(
                repository = UserRepository(
                    dataSource = DataSourceFake().user,
                ),
                onSaveComplete = { }
            )
        )
    }
}