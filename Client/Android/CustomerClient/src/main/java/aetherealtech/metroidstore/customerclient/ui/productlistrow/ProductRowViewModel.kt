package aetherealtech.metroidstore.customerclient.ui.productlistrow

import aetherealtech.metroidstore.customerclient.model.ImageSource
import aetherealtech.metroidstore.customerclient.model.ProductID
import aetherealtech.metroidstore.customerclient.model.ProductSummary
import aetherealtech.metroidstore.customerclient.widgets.PriceViewModel
import aetherealtech.metroidstore.customerclient.widgets.StarRatingViewModel
import androidx.lifecycle.ViewModel

class ProductRowViewModel(
    product: ProductSummary,
    val select: () -> Unit
): ViewModel() {
    val id: ProductID
    val image: ImageSource
    val name: String
    val type: String
    val game: String
    val ratings: StarRatingViewModel?
    val price: PriceViewModel

    init {
        id = product.id
        image = product.image
        name = product.name
        type = product.type
        game = product.game
        ratings = StarRatingViewModel.create(product.ratingCount, product.rating)
        price = PriceViewModel(product.price)
    }
}