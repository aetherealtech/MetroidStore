package com.example.metroidstore.localdatasources

import android.content.res.Resources.NotFoundException
import android.database.sqlite.SQLiteDatabase
import com.example.metroidstore.datasources.ProductDataSource
import com.example.metroidstore.model.Price
import com.example.metroidstore.model.Product
import com.example.metroidstore.model.Rating
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class ProductDataSourceDatabase(
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
                        id = Product.ID(productId),
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

    override suspend fun getProductDetails(id: Product.ID): Product {
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
            "SELECT Products.id, Images.id, Products.name, Products.type, Products.game, Products.price FROM Products LEFT JOIN ProductImages ON ProductImages.productId = Products.id JOIN Images ON Images.id = ProductImages.imageId WHERE Product.id = ? AND ProductImages.isPrimary = 1",
            arrayOf("${id.value}")
        ).use { cursor ->
            if(!cursor.moveToNext())
                throw NotFoundException("No product with ID ${id.value}")

            val productId = cursor.getInt(0)

            return@use Product(
                id = Product.ID(productId),
                image = ImageSourceDatabase(database, cursor.getInt(1)),
                name = cursor.getString(2),
                type = cursor.getString(3),
                game = cursor.getString(4),
                ratings = ratings,
                price = Price(cursor.getInt(5))
            )
        }
    }
}