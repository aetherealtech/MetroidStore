package aetherealtech.metroidstore.embeddedbackend

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import aetherealtech.metroidstore.backendmodel.CartItem
import aetherealtech.metroidstore.backendmodel.OrderSummary
import aetherealtech.metroidstore.backendmodel.PaymentMethodSummary
import aetherealtech.metroidstore.backendmodel.ProductDetails
import aetherealtech.metroidstore.backendmodel.ProductSummary
import aetherealtech.metroidstore.backendmodel.ShippingMethod
import aetherealtech.metroidstore.backendmodel.UserAddressSummary
import kotlinx.collections.immutable.toImmutableList
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.stream.Collectors

class EmbeddedDatabase {
    companion object {
        fun load(context: Context): SQLiteDatabase {
            val dbFile = File(context.filesDir, "MetroidStore.db")

            var needsSeed = false

            if(!dbFile.exists()) {
                needsSeed = true
                dbFile.createNewFile()
            }

            val db = SQLiteDatabase.openDatabase(
                dbFile.path,
                null,
                SQLiteDatabase.CREATE_IF_NECESSARY
            )

            db.execSQL("PRAGMA foreign_keys = ON")

            if(needsSeed) {
                context.assets.open("MetroidStore.sql").use { dbAssetStream ->
                    BufferedReader(InputStreamReader(dbAssetStream))
                        .lines()
                        .collect(Collectors.joining("\n"))
                        .split(";")
                        .filter { statement -> !statement.isEmpty() }
                        .forEach { statement -> db.execSQL(statement) }
                }
            }

            return db
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

fun SQLiteDatabase.addresses(username: String): List<UserAddressSummary> {
    return rawQuery(
        "SELECT addressID, name, isPrimary FROM UserAddresses WHERE username = ?",
        arrayOf(username)
    ).use { cursor ->
        val results = mutableListOf<UserAddressSummary>()

        while(cursor.moveToNext()) {
            results.add(
                UserAddressSummary(
                    addressID = cursor.getInt(0),
                    name = cursor.getString(1),
                    isPrimary = cursor.getInt(2) != 0
                )
            )
        }

        return@use results.toImmutableList()
    }
}

fun SQLiteDatabase.shippingMethods(): List<ShippingMethod> {
    return rawQuery(
        "SELECT name, cost FROM ShippingMethods",
        emptyArray()
    ).use { cursor ->
        val results = mutableListOf<ShippingMethod>()

        while(cursor.moveToNext()) {
            results.add(
                ShippingMethod(
                    name = cursor.getString(0),
                    costCents = cursor.getInt(1)
                )
            )
        }

        return@use results.toImmutableList()
    }
}

fun SQLiteDatabase.paymentMethods(username: String): List<PaymentMethodSummary> {
    return rawQuery(
        "SELECT id, name, isPrimary FROM PaymentMethods WHERE username = ?",
        arrayOf(username)
    ).use { cursor ->
        val results = mutableListOf<PaymentMethodSummary>()

        while(cursor.moveToNext()) {
            results.add(
                PaymentMethodSummary(
                    id = cursor.getInt(0),
                    name = cursor.getString(1),
                    isPrimary = cursor.getInt(2) != 0
                )
            )
        }

        return@use results.toImmutableList()
    }
}

fun SQLiteDatabase.placeOrder(
    username: String,
    addressID: Int,
    shippingMethodName: String,
    paymentMethodID: Int
): Int {
    beginTransaction()

    try {
        execSQL(
            "INSERT INTO Orders (username, addressID, shippingMethod, paymentMethodID) VALUES (?, ?, ?, ?)",
            arrayOf(username, addressID, shippingMethodName, paymentMethodID)
        )

        val orderID = rawQuery(
            "SELECT last_insert_rowid()",
            emptyArray()
        ).use { cursor ->
            cursor.moveToNext()
            return@use cursor.getInt(0)
        }

        execSQL(
            "INSERT INTO OrderItems SELECT ?, productID, quantity FROM CartItems WHERE username = ?",
            arrayOf(orderID, username)
        )

        execSQL(
            "DELETE FROM CartItems WHERE username = ?",
            arrayOf(username)
        )

        setTransactionSuccessful()

        return orderID
    } finally {
        endTransaction()
    }
}

fun SQLiteDatabase.orders(username: String): List<OrderSummary> {
    return rawQuery(
        """
            SELECT Orders.id, Orders.createdAt, ShippingMethods.cost AS shippingCost, SUM(OrderItems.quantity) AS items, SUM(QuantityPrices.quantityPrice) AS subtotal, LatestOrderStatuses.latestStatus
            FROM OrderItems
            LEFT JOIN Orders ON OrderItems.orderID = Orders.id
            LEFT JOIN ShippingMethods ON Orders.shippingMethod = ShippingMethods.name
            JOIN (SELECT OI.orderID, OI.productID, Products.price * OI.quantity AS quantityPrice FROM Products LEFT JOIN OrderItems OI on Products.id = OI.productID) AS QuantityPrices ON (OrderItems.orderID, OrderItems.productID) = (QuantityPrices.orderID, QuantityPrices.productID)
            JOIN (SELECT Orders.id AS orderID, coalesce(OrderActivities.status, (SELECT OrderStatuses.name FROM OrderStatuses LIMIT 1)) AS latestStatus FROM Orders LEFT JOIN OrderActivities ON OrderActivities.id = (SELECT OrderActivities.id FROM OrderActivities WHERE OrderActivities.orderID = Orders.id ORDER BY OrderActivities.date DESC LIMIT 1)) AS LatestOrderStatuses ON LatestOrderStatuses.orderID = Orders.id
            WHERE Orders.username = ?
            GROUP BY Orders.id
            ORDER BY Orders.createdAt
        """,
        arrayOf(username)
    ).use { cursor ->
        val results = mutableListOf<OrderSummary>()

        while(cursor.moveToNext()) {
            val shippingCost = cursor.getInt(2)
            val subtotal = cursor.getInt(4)

            val taxesPercent = 10

            val total = (subtotal + shippingCost) * (100 + taxesPercent) / 100

            results.add(
                OrderSummary(
                    id = cursor.getInt(0),
                    date = cursor.getString(1),
                    items = cursor.getInt(3),
                    totalCents = total,
                    latestStatus = cursor.getString(5)
                )
            )
        }

        return@use results.toImmutableList()
    }
}