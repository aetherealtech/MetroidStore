package com.example.metroidstore.fakedatasources

import android.content.res.Resources.NotFoundException
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.model.DataImageSource
import com.example.metroidstore.model.ImageSource
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.model.ProductSummary
import com.example.metroidstore.model.Rating
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class ProductDataSourceFake: ProductDataSource {
    private data class Product(
        val id: ProductID,
        val name: String,
        val type: String,
        val game: String,
        val images: ImmutableList<ImageSource>,
        val ratings: ImmutableList<Rating>,
        val price: Price
    )

    private val products = (0..<100)
        .map { i ->
            Product(
                id = ProductID(i),
                name = "Item $i",
                type = "Type",
                game = "Game",
                images = persistentListOf(
                    DataImageSource(base64 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAABg2lDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV/TSkUrDnYQcchQnSyIijhqFYpQIdQKrTqYXPoFTRqSFBdHwbXg4Mdi1cHFWVcHV0EQ/ABxdXFSdJES/5cUWsR4cNyPd/ced+8AoVFhmhUaBzTdNtPJhJjNrYrhV/QiAiAEQWaWMSdJKfiOr3sE+HoX51n+5/4cfWreYkBAJJ5lhmkTbxBPb9oG533iKCvJKvE58ZhJFyR+5Lri8RvnossCz4yamfQ8cZRYLHaw0sGsZGrEU8QxVdMpX8h6rHLe4qxVaqx1T/7CSF5fWeY6zWEksYglSBChoIYyKrARp1UnxUKa9hM+/iHXL5FLIVcZjBwLqEKD7PrB/+B3t1ZhcsJLiiSArhfH+RgBwrtAs+4438eO0zwBgs/Ald72VxvAzCfp9bYWOwL6t4GL67am7AGXO8DgkyGbsisFaQqFAvB+Rt+UAwZugZ41r7fWPk4fgAx1lboBDg6B0SJlr/u8u7uzt3/PtPr7Abp2cl13280HAAAAHlBMVEUAAABTKgiQRxHLZRv1rGwcSRo7oCay8lX/+2v////2rSEqAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH5wsMESsYgmujSwAAAHpJREFUCNclTTkOwjAQnKUILn1JtNlsHmCveUBEIt6RDtFRBXgB346Dp5nRaA4AcGhwc/feKtOiT/xWIHL+GvMCwiV9mkATNKfpYc4rSCXfj1ZgXyxJD45ZrovWnZC8lL7uJZAfbDUsjRwngOGLjrfjS9Se/iEpgxPdAQyvEObhzmsrAAAAAElFTkSuQmCC"),
                    DataImageSource(base64 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAABg2lDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV/TSkUrDnYQcchQnSyIijhqFYpQIdQKrTqYXPoFTRqSFBdHwbXg4Mdi1cHFWVcHV0EQ/ABxdXFSdJES/5cUWsR4cNyPd/ced+8AoVFhmhUaBzTdNtPJhJjNrYrhV/QiAiAEQWaWMSdJKfiOr3sE+HoX51n+5/4cfWreYkBAJJ5lhmkTbxBPb9oG533iKCvJKvE58ZhJFyR+5Lri8RvnossCz4yamfQ8cZRYLHaw0sGsZGrEU8QxVdMpX8h6rHLe4qxVaqx1T/7CSF5fWeY6zWEksYglSBChoIYyKrARp1UnxUKa9hM+/iHXL5FLIVcZjBwLqEKD7PrB/+B3t1ZhcsJLiiSArhfH+RgBwrtAs+4438eO0zwBgs/Ald72VxvAzCfp9bYWOwL6t4GL67am7AGXO8DgkyGbsisFaQqFAvB+Rt+UAwZugZ41r7fWPk4fgAx1lboBDg6B0SJlr/u8u7uzt3/PtPr7Abp2cl13280HAAAAHlBMVEUAAABTKgiQRxHLZRv1rGwcSRo7oCay8lX/+2v////2rSEqAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH5wsMESsYgmujSwAAAHpJREFUCNclTTkOwjAQnKUILn1JtNlsHmCveUBEIt6RDtFRBXgB346Dp5nRaA4AcGhwc/feKtOiT/xWIHL+GvMCwiV9mkATNKfpYc4rSCXfj1ZgXyxJD45ZrovWnZC8lL7uJZAfbDUsjRwngOGLjrfjS9Se/iEpgxPdAQyvEObhzmsrAAAAAElFTkSuQmCC"),
                    DataImageSource(base64 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAABg2lDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV/TSkUrDnYQcchQnSyIijhqFYpQIdQKrTqYXPoFTRqSFBdHwbXg4Mdi1cHFWVcHV0EQ/ABxdXFSdJES/5cUWsR4cNyPd/ced+8AoVFhmhUaBzTdNtPJhJjNrYrhV/QiAiAEQWaWMSdJKfiOr3sE+HoX51n+5/4cfWreYkBAJJ5lhmkTbxBPb9oG533iKCvJKvE58ZhJFyR+5Lri8RvnossCz4yamfQ8cZRYLHaw0sGsZGrEU8QxVdMpX8h6rHLe4qxVaqx1T/7CSF5fWeY6zWEksYglSBChoIYyKrARp1UnxUKa9hM+/iHXL5FLIVcZjBwLqEKD7PrB/+B3t1ZhcsJLiiSArhfH+RgBwrtAs+4438eO0zwBgs/Ald72VxvAzCfp9bYWOwL6t4GL67am7AGXO8DgkyGbsisFaQqFAvB+Rt+UAwZugZ41r7fWPk4fgAx1lboBDg6B0SJlr/u8u7uzt3/PtPr7Abp2cl13280HAAAAHlBMVEUAAABTKgiQRxHLZRv1rGwcSRo7oCay8lX/+2v////2rSEqAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH5wsMESsYgmujSwAAAHpJREFUCNclTTkOwjAQnKUILn1JtNlsHmCveUBEIt6RDtFRBXgB346Dp5nRaA4AcGhwc/feKtOiT/xWIHL+GvMCwiV9mkATNKfpYc4rSCXfj1ZgXyxJD45ZrovWnZC8lL7uJZAfbDUsjRwngOGLjrfjS9Se/iEpgxPdAQyvEObhzmsrAAAAAElFTkSuQmCC"),
                    DataImageSource(base64 = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAABg2lDQ1BJQ0MgcHJvZmlsZQAAKJF9kT1Iw0AcxV/TSkUrDnYQcchQnSyIijhqFYpQIdQKrTqYXPoFTRqSFBdHwbXg4Mdi1cHFWVcHV0EQ/ABxdXFSdJES/5cUWsR4cNyPd/ced+8AoVFhmhUaBzTdNtPJhJjNrYrhV/QiAiAEQWaWMSdJKfiOr3sE+HoX51n+5/4cfWreYkBAJJ5lhmkTbxBPb9oG533iKCvJKvE58ZhJFyR+5Lri8RvnossCz4yamfQ8cZRYLHaw0sGsZGrEU8QxVdMpX8h6rHLe4qxVaqx1T/7CSF5fWeY6zWEksYglSBChoIYyKrARp1UnxUKa9hM+/iHXL5FLIVcZjBwLqEKD7PrB/+B3t1ZhcsJLiiSArhfH+RgBwrtAs+4438eO0zwBgs/Ald72VxvAzCfp9bYWOwL6t4GL67am7AGXO8DgkyGbsisFaQqFAvB+Rt+UAwZugZ41r7fWPk4fgAx1lboBDg6B0SJlr/u8u7uzt3/PtPr7Abp2cl13280HAAAAHlBMVEUAAABTKgiQRxHLZRv1rGwcSRo7oCay8lX/+2v////2rSEqAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH5wsMESsYgmujSwAAAHpJREFUCNclTTkOwjAQnKUILn1JtNlsHmCveUBEIt6RDtFRBXgB346Dp5nRaA4AcGhwc/feKtOiT/xWIHL+GvMCwiV9mkATNKfpYc4rSCXfj1ZgXyxJD45ZrovWnZC8lL7uJZAfbDUsjRwngOGLjrfjS9Se/iEpgxPdAQyvEObhzmsrAAAAAElFTkSuQmCC"),
                ),
                ratings = persistentListOf(
                    Rating.TWO,
                    Rating.FOUR,
                    Rating.FIVE
                ),
                price = Price(cents = 1099)
            )
        }

    override suspend fun getProducts(): ImmutableList<ProductSummary> {
        return products
            .map { product ->
                ProductSummary(
                    id = product.id,
                    image = product.images[0],
                    name = product.name,
                    type = product.type,
                    game = product.game,
                    ratings = product.ratings,
                    price = product.price
                )
            }
            .toImmutableList()
    }

    override suspend fun getProductDetails(id: ProductID): ProductDetails {
        val product = products
            .find { product -> product.id == id }

        if(product == null)
            throw NotFoundException("No product with ID ${id.value}")

        return ProductDetails(
            images = product.images,
            name = product.name,
            type = product.type,
            game = product.game,
            ratings = product.ratings,
            price = product.price
        )
    }
}