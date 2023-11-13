package com.example.metroidstore.localdatasources

import android.content.res.Resources.NotFoundException
import android.database.sqlite.SQLiteDatabase
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.ProductDetails
import com.example.metroidstore.model.ProductID
import com.example.metroidstore.model.ProductSummary
import com.example.metroidstore.model.Rating
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class ProductDataSourceDatabase(
    private val database: SQLiteDatabase
): ProductDataSource {
    override suspend fun getProducts(): ImmutableList<ProductSummary> {
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
            val results = mutableListOf<ProductSummary>()

            while(cursor.moveToNext()) {
                val productId = cursor.getInt(0)

                val ratings = allRatings
                    .filter { rating -> rating.productId == productId }
                    .map { rating -> rating.rating }
                    .toImmutableList()

                results.add(
                    ProductSummary(
                        id = ProductID(productId),
                        image = ImageSourceDatabase(database, cursor.getInt(1)),
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

    override suspend fun getProductDetails(id: ProductID): ProductDetails {
        val images = database.rawQuery(
            "SELECT imageId FROM ProductImages WHERE productId = ?",
            arrayOf("${id.value}")
        ).use { cursor ->
            val results = mutableListOf<ImageSourceDatabase>()

            while(cursor.moveToNext()) {
                results.add(ImageSourceDatabase(database, cursor.getInt(0)))
            }

            return@use results.toImmutableList()
        }

        val ratings = database.rawQuery(
            "SELECT rating FROM ProductReviews WHERE productId = ?",
            arrayOf("${id.value}")
        ).use { cursor ->
            val results = mutableListOf<Rating>()

            while(cursor.moveToNext()) {
                val rawRating = cursor.getInt(0)
                results.add(Rating.values().first { rating -> rating.value == rawRating })
            }

            return@use results.toImmutableList()
        }

        return database.rawQuery(
            "SELECT name, type, game, price FROM Products WHERE id = ?",
            arrayOf("${id.value}")
        ).use { cursor ->
            if(!cursor.moveToNext())
                throw NotFoundException("No product with ID ${id.value}")

            return@use ProductDetails(
                images = images,
                name = cursor.getString(0),
                type = cursor.getString(1),
                game = cursor.getString(2),
                ratings = ratings,
                price = Price(cursor.getInt(3))
            )
        }
    }
}