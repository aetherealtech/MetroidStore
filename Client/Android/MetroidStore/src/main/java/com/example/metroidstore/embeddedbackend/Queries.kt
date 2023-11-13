package com.example.metroidstore.embeddedbackend

import android.database.sqlite.SQLiteDatabase
import kotlinx.collections.immutable.toImmutableList

fun SQLiteDatabase.products(): List<ProductSummary> {
    data class RatingQuery(val productId: Int, val rating: Int)

    val allRatings = rawQuery(
        "SELECT productId, rating FROM ProductReviews",
        emptyArray()
    ).use { cursor ->
        val results = mutableListOf<RatingQuery>()

        while(cursor.moveToNext()) {
            results.add(RatingQuery(
                productId = cursor.getInt(0),
                rating = cursor.getInt(1)
            ))
        }

        return@use results
    }

    return rawQuery(
        "SELECT Products.id, Images.id, Products.name, Products.type, Products.game, Products.price FROM Products LEFT JOIN ProductImages ON ProductImages.productId = Products.id JOIN Images ON Images.id = ProductImages.imageId WHERE ProductImages.isPrimary = 1",
        emptyArray()
    ).use { cursor ->
        val results = mutableListOf<ProductSummary>()

        while(cursor.moveToNext()) {
            val productId = cursor.getInt(0)

            val ratings = allRatings
                .filter { rating -> rating.productId == productId }
                .map { rating -> rating.rating }

            results.add(
                ProductSummary(
                    id = productId,
                    image = "images/${cursor.getInt(1)}",
                    name = cursor.getString(2),
                    type = cursor.getString(3),
                    game = cursor.getString(4),
                    ratings = ratings,
                    priceCents = cursor.getInt(5)
                )
            )
        }

        return@use results.toImmutableList()
    }
}

fun SQLiteDatabase.product(id: Int): ProductDetails? {
    val images = rawQuery(
        "SELECT imageId FROM ProductImages WHERE productId = ?",
        arrayOf("${id}")
    ).use { cursor ->
        val results = mutableListOf<String>()

        while(cursor.moveToNext()) {
            results.add("images/${cursor.getInt(0)}")
        }

        return@use results.toImmutableList()
    }

    val ratings = rawQuery(
        "SELECT rating FROM ProductReviews WHERE productId = ?",
        arrayOf("${id}")
    ).use { cursor ->
        val results = mutableListOf<Int>()

        while(cursor.moveToNext()) {
            results.add(cursor.getInt(0))
        }

        return@use results.toImmutableList()
    }

    return rawQuery(
        "SELECT name, type, game, price FROM Products WHERE id = ?",
        arrayOf("${id}")
    ).use { cursor ->
        if(!cursor.moveToNext())
            return@use null

        return@use ProductDetails(
            images = images,
            name = cursor.getString(0),
            type = cursor.getString(1),
            game = cursor.getString(2),
            ratings = ratings,
            priceCents = cursor.getInt(3)
        )
    }
}

fun SQLiteDatabase.image(id: Int): ByteArray {
    return rawQuery(
        "SELECT data FROM Images WHERE id = ?",
        arrayOf("$id")
    ).use { cursor ->
        cursor.moveToFirst()

        return@use cursor.getBlob(0)
    }
}