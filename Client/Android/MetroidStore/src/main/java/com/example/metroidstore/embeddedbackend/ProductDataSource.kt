package com.example.metroidstore.embeddedbackend

import android.database.sqlite.SQLiteDatabase
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.Product
import com.example.metroidstore.model.Rating
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class ProductDataSourceEmbedded(
    private val database: SQLiteDatabase
): ProductDataSource {
    override suspend fun getProducts(): ImmutableList<Product> {
        data class RatingQuery(val productId: Int, val rating: Rating)

        val allRatings = database.rawQuery(
            "SELECT productId, rating FROM ProductReviews",
            emptyArray()
        ).use { cursor ->
            val results = mutableListOf<RatingQuery>()

            while(cursor.moveToNext()) {
                val rawRating = cursor.getInt(1)

                results.add(RatingQuery(
                    productId = cursor.getInt(0),
                    rating = Rating.values().first { rating -> rating.value == rawRating }
                ))
            }

            return@use results
        }

        return database.rawQuery(
            "SELECT Products.id, Images.id, Products.name, Products.type, Products.game, Products.price FROM Products LEFT JOIN ProductImages ON ProductImages.productId = Products.id JOIN Images ON Images.id = ProductImages.imageId WHERE ProductImages.isPrimary = 1",
            emptyArray()
        ).use { cursor ->
            val results = mutableListOf<Product>()

            while(cursor.moveToNext()) {
                val productId = cursor.getInt(0)

                val ratings = allRatings
                    .filter { rating -> rating.productId == productId }
                    .map { rating -> rating.rating }
                    .toImmutableList()

                results.add(
                    Product(
                        image = DatabaseImageSource(database, cursor.getInt(1)),
                        name = cursor.getString(2),
                        type = cursor.getString(3),
                        game = cursor.getString(4),
                        ratings = ratings,
                        price = Price(cursor.getInt(5))
                    )
                )
            }

            return@use results.toImmutableList()
        }
    }
}