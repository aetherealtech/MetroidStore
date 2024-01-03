package aetherealtech.metroidstore.customerclient.ui.addtocartbutton

import aetherealtech.metroidstore.customerclient.ui.addtocartconfirmation.AddToCartConfirmation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import aetherealtech.metroidstore.customerclient.theme.Colors

@Composable
fun AddToCartButton(
    viewModel: AddToCartViewModel
) {
    Button(
        onClick = { viewModel.addToCart() },
        modifier = Modifier.fillMaxWidth(0.75f),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = Colors.PrimaryCallToAction
        )
    ) {
        Text(text = "Add to Cart")
    }

    AddToCartConfirmation(
        viewModel = viewModel.confirmationViewModel
    )
}

