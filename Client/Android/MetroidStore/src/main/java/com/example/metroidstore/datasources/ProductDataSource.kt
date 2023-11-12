package com.example.metroidstore.datasources

import com.example.metroidstore.model.Product
import com.example.metroidstore.model.Rating
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.math.BigDecimal

interface ProductDataSource {
    suspend fun getProducts(): ImmutableList<Product>
}

class ProductDataSourceFake: ProductDataSource {
    override suspend fun getProducts(): ImmutableList<Product> {
        return (0..<100)
            .map { i -> Product(
                name = "Item $i",
                type = "Type",
                game = "Game",
                ratings = persistentListOf(
                    Rating.TWO,
                    Rating.FOUR,
                    Rating.FIVE
                ),
                price = BigDecimal("10.99")
            ) }
            .toImmutableList()
    }
}