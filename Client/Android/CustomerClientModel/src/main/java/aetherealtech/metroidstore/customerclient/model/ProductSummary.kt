package aetherealtech.metroidstore.customerclient.model

data class ProductSummary(
    val id: ProductID,
    val image: ImageSource,
    val name: String,
    val type: String,
    val game: String,
    val price: Price,
    val ratingCount: Int,
    val rating: Float?
)