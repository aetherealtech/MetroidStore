package com.example.metroidstore.embeddedbackend

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import com.example.metroidstore.backendmodel.CartItem
import com.example.metroidstore.backendmodel.ProductDetails
import com.example.metroidstore.backendmodel.ProductSummary
import kotlinx.collections.immutable.toImmutableList
import java.io.File

class EmbeddedDatabase {
    companion object {
        fun load(context: Context): SQLiteDatabase {
            val dbFile = File(context.filesDir, "MetroidStore.db")

            if(!dbFile.exists()) {
                dbFile.createNewFile()

                context.assets.open("MetroidStore.db").use { dbAssetStream ->
                    dbFile.outputStream().use { dbFileStream ->
                        dbAssetStream.copyTo(dbFileStream)
                    }
                }
            }

            return SQLiteDatabase.openDatabase(
                dbFile.path,
                null,
                SQLiteDatabase.CREATE_IF_NECESSARY
            )
        }
    }
}

fun SQLiteDatabase.products(): List<ProductSummary> {
    data class RatingQuery(val productID: Int, val rating: Int)

    val allRatings = rawQuery(
        "SELECT productID, rating FROM ProductReviews",
        emptyArray()
    ).use { cursor ->
        val results = mutableListOf<RatingQuery>()

        while(cursor.moveToNext()) {
            results.add(RatingQuery(
                productID = cursor.getInt(0),
                rating = cursor.getInt(1)
            ))
        }

        return@use results
    }

    return rawQuery(
        "SELECT Products.id, Images.id, Products.name, Products.type, Products.game, Products.price FROM Products LEFT JOIN ProductImages ON ProductImages.productID = Products.id JOIN Images ON Images.id = ProductImages.imageID WHERE ProductImages.isPrimary = 1",
        emptyArray()
    ).use { cursor ->
        val results = mutableListOf<ProductSummary>()

        while(cursor.moveToNext()) {
            val productID = cursor.getInt(0)

            val ratings = allRatings
                .filter { rating -> rating.productID == productID }
                .map { rating -> rating.rating }

            results.add(
                ProductSummary(
                    id = productID,
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

fun SQLiteDatabase.cart(username: String): List<CartItem> {
    return rawQuery(
        "SELECT Products.id, Images.id, Products.name, Products.price, CartItems.quantity FROM CartItems LEFT JOIN Products ON Products.id = CartItems.productID LEFT JOIN ProductImages ON ProductImages.productID = Products.id JOIN Images ON Images.id = ProductImages.imageID WHERE CartItems.username = ? AND ProductImages.isPrimary = 1",
        arrayOf(username)
    ).use { cursor ->
        val results = mutableListOf<CartItem>()

        while(cursor.moveToNext()) {
            results.add(
                CartItem(
                    productID = cursor.getInt(0),
                    image = "images/${cursor.getInt(1)}",
                    name = cursor.getString(2),
                    priceCents = cursor.getInt(3),
                    quantity = cursor.getInt(4)
                )
            )
        }

        return@use results.toImmutableList()
    }
}

fun SQLiteDatabase.addToCart(
    username: String,
    productID: Int
): List<CartItem> {
    beginTransaction()

    try {
        execSQL(
            "INSERT INTO CartItems (username, productID) VALUES (?, ?) ON CONFLICT DO UPDATE SET quantity = quantity + 1",
            arrayOf(username, productID)
        )

        val cart = cart(username)

        setTransactionSuccessful()

        return cart
    } catch (error: Exception) {
        println(error.localizedMessage)

        return emptyList()
    } finally {
        endTransaction()
    }
}

fun SQLiteDatabase.removeFromCart(
    username: String,
    productID: Int
): List<CartItem> {
    beginTransaction()

    try {
        execSQL(
            "DELETE FROM CartItems WHERE username = ? AND productID = ?",
            arrayOf(username, productID)
        )

        val cart = cart(username)

        setTransactionSuccessful()

        return cart
    } catch (error: Exception) {
        println(error.localizedMessage)

        return emptyList()
    } finally {
        endTransaction()
    }
}

fun SQLiteDatabase.decrementCartQuantity(
    username: String,
    productID: Int
): List<CartItem> {
    beginTransaction()

    try {
        execSQL(
            "DELETE FROM CartItems WHERE username = ? AND productID = ? AND quantity = 1",
            arrayOf(username, productID)
        )

        execSQL(
            "UPDATE CartItems SET quantity = quantity - 1 WHERE username = ? AND productID = ?",
            arrayOf(username, productID)
        )

        val cart = cart(username)

        setTransactionSuccessful()

        return cart
    } catch (error: Exception) {
        println(error.localizedMessage)

        return emptyList()
    } finally {
        endTransaction()
    }
}

fun SQLiteDatabase.incrementCartQuantity(
    username: String,
    productID: Int
): List<CartItem> {
    beginTransaction()

    try {
        execSQL(
            "UPDATE CartItems SET quantity = quantity + 1 WHERE username = ? AND productID = ?",
            arrayOf(username, productID)
        )

        val cart = cart(username)

        setTransactionSuccessful()

        return cart
    } catch (error: Exception) {
        println(error.localizedMessage)

        return emptyList()
    } finally {
        endTransaction()
    }
}

fun SQLiteDatabase.product(id: Int): ProductDetails? {
    val images = rawQuery(
        "SELECT imageID FROM ProductImages WHERE productID = ?",
        arrayOf("${id}")
    ).use { cursor ->
        val results = mutableListOf<String>()

        while(cursor.moveToNext()) {
            results.add("images/${cursor.getInt(0)}")
        }

        return@use results.toImmutableList()
    }

    val ratings = rawQuery(
        "SELECT rating FROM ProductReviews WHERE productID = ?",
        arrayOf("${id}")
    ).use { cursor ->
        val results = mutableListOf<Int>()

        while(cursor.moveToNext()) {
            results.add(cursor.getInt(0))
        }

        return@use results.toImmutableList()
    }

    return rawQuery(
        "SELECT id, name, type, game, price FROM Products WHERE id = ?",
        arrayOf("${id}")
    ).use { cursor ->
        if(!cursor.moveToNext())
            return@use null

        return@use ProductDetails(
            id = cursor.getInt(0),
            images = images,
            name = cursor.getString(1),
            type = cursor.getString(2),
            game = cursor.getString(3),
            ratings = ratings,
            priceCents = cursor.getInt(4)
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