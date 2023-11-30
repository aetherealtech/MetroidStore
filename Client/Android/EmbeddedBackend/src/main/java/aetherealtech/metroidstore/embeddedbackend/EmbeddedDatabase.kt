package aetherealtech.metroidstore.embeddedbackend

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import aetherealtech.metroidstore.backendmodel.CartItem
import aetherealtech.metroidstore.backendmodel.EditAddress
import aetherealtech.metroidstore.backendmodel.EditPaymentMethod
import aetherealtech.metroidstore.backendmodel.OrderActivity
import aetherealtech.metroidstore.backendmodel.OrderDetails
import aetherealtech.metroidstore.backendmodel.OrderSummary
import aetherealtech.metroidstore.backendmodel.PaymentMethodDetails
import aetherealtech.metroidstore.backendmodel.PaymentMethodSummary
import aetherealtech.metroidstore.backendmodel.ProductDetails
import aetherealtech.metroidstore.backendmodel.ProductSummary
import aetherealtech.metroidstore.backendmodel.ShippingMethod
import aetherealtech.metroidstore.backendmodel.UserAddressDetails
import aetherealtech.metroidstore.backendmodel.UserAddressSummary
import kotlinx.collections.immutable.toImmutableList
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.stream.Collectors

val taxesPercent = 10

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

fun SQLiteDatabase.orders(
    username: String
): List<OrderSummary> {
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
            ORDER BY Orders.createdAt DESC
        """,
        arrayOf(username)
    ).use { cursor ->
        val results = mutableListOf<OrderSummary>()

        while(cursor.moveToNext()) {
            val shippingCost = cursor.getInt(2)
            val subtotal = cursor.getInt(4)

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

fun SQLiteDatabase.order(
    orderID: Int
): OrderDetails {
    return rawQuery(
        """
            SELECT Orders.createdAt AS date, UserAddresses.name AS address, Orders.shippingMethod, PaymentMethods.name AS paymentMethod, ShippingMethods.cost AS shippingCost, LatestOrderStatuses.latestStatus, Products.id, Products.name, ProductImages.imageID, OrderItems.quantity, Products.price
            FROM OrderItems
            LEFT JOIN Orders ON OrderItems.orderID = Orders.id
            LEFT JOIN ShippingMethods ON Orders.shippingMethod = ShippingMethods.name
            LEFT JOIN Products ON Products.id = OrderItems.productID
            LEFT JOIN ProductImages ON ProductImages.productID = Products.id
            LEFT JOIN UserAddresses ON (UserAddresses.username, UserAddresses.addressID) = (Orders.username, Orders.addressID)
            LEFT JOIN PaymentMethods ON (PaymentMethods.username, PaymentMethods.id) = (Orders.username, Orders.paymentMethodID)
            JOIN (SELECT Orders.id AS orderID, coalesce(OrderActivities.status, (SELECT OrderStatuses.name FROM OrderStatuses LIMIT 1)) AS latestStatus FROM Orders LEFT JOIN OrderActivities ON OrderActivities.id = (SELECT OrderActivities.id FROM OrderActivities WHERE OrderActivities.orderID = Orders.id ORDER BY OrderActivities.date DESC LIMIT 1)) AS LatestOrderStatuses ON LatestOrderStatuses.orderID = Orders.id
            WHERE OrderItems.orderID = ? AND ProductImages.isPrimary
        """,
        arrayOf("$orderID")
    ).use { cursor ->
        val items = mutableListOf<OrderDetails.Item>()

        var subtotal = 0

        while(cursor.moveToNext()) {
            val quantity = cursor.getInt(9)
            val pricePerUnit = cursor.getInt(10)

            val price = quantity * pricePerUnit
            subtotal += price

            items.add(
                OrderDetails.Item(
                    productID = cursor.getInt(6),
                    name = cursor.getString(7),
                    image = "images/${cursor.getInt(8)}",
                    quantity = quantity,
                    priceCents = price
                )
            )
        }

        cursor.moveToPrevious()

        val shippingCost = cursor.getInt(4)

        val total = (subtotal + shippingCost) * (100 + taxesPercent) / 100

        return@use OrderDetails(
            date = cursor.getString(0),
            address = cursor.getString(1),
            shippingMethod = cursor.getString(2),
            paymentMethod = cursor.getString(3),
            totalCents = total,
            latestStatus = cursor.getString(5),
            items = items
        )
    }
}

fun SQLiteDatabase.orderActivity(
    orderID: Int
): List<OrderActivity> {
    return rawQuery(
        """
            SELECT status, date FROM (
                SELECT orderID, status, date
                FROM OrderActivities
                UNION
                SELECT id, (SELECT name FROM OrderStatuses LIMIT 1), createdAt FROM Orders
            )
            WHERE orderID = ?
            ORDER BY date
        """,
        arrayOf("$orderID")
    ).use { cursor ->
        val result = mutableListOf<OrderActivity>()

        while(cursor.moveToNext()) {
            result.add(
                OrderActivity(
                    status = cursor.getString(0),
                    date = cursor.getString(1)
                )
            )
        }

        return@use result
    }
}

fun SQLiteDatabase.addressDetails(username: String): List<UserAddressDetails> {
    return rawQuery(
        """
            SELECT 
                UserAddresses.name, 
                Addresses.id, 
                Addresses.street1,
                Addresses.street2, 
                Addresses.locality, 
                Addresses.province, 
                Addresses.country, 
                Addresses.planet, 
                Addresses.postalCode, 
                UserAddresses.isPrimary 
            FROM UserAddresses
            JOIN Addresses ON Addresses.id = UserAddresses.addressID
            WHERE username = ?
            """,
        arrayOf(username)
    ).use { cursor ->
        val results = mutableListOf<UserAddressDetails>()

        while(cursor.moveToNext()) {
            results.add(
                UserAddressDetails(
                    name = cursor.getString(0),
                    addressID = cursor.getInt(1),
                    street1 = cursor.getString(2),
                    street2 = cursor.getString(3),
                    locality = cursor.getString(4),
                    province = cursor.getString(5),
                    country = cursor.getString(6),
                    planet = cursor.getString(7),
                    postalCode = cursor.getString(8),
                    isPrimary = cursor.getInt(9) != 0
                )
            )
        }

        return@use results.toImmutableList()
    }
}

fun SQLiteDatabase.createAddress(
    username: String,
    address: EditAddress
): List<UserAddressDetails> {
    beginTransaction()

    try {
        execSQL(
            "INSERT INTO Addresses (street1, street2, locality, province, country, planet, postalCode) VALUES (?, ?, ?, ?, ?, ?, ?)",
            arrayOf(address.street1, address.street2, address.locality, address.province, address.country, address.planet, address.postalCode)
        )

        val addressID = rawQuery(
            "SELECT last_insert_rowid()",
            emptyArray()
        ).use { cursor ->
            cursor.moveToNext()
            return@use cursor.getInt(0)
        }

        if(address.isPrimary) {
            execSQL(
                "UPDATE UserAddresses SET isPrimary = 0 WHERE username = ?",
                arrayOf(username)
            )
        }

        execSQL(
            "INSERT INTO UserAddresses (username, addressID, name, isPrimary) VALUES (?, ?, ?, ?)",
            arrayOf(username, addressID, address.name, address.isPrimary)
        )

        val addresses = addressDetails(username)

        setTransactionSuccessful()

        return addresses
    } finally {
        endTransaction()
    }
}

fun SQLiteDatabase.updateAddress(
    username: String,
    address: EditAddress,
    addressID: Int
): List<UserAddressDetails> {
    beginTransaction()

    try {
        execSQL(
            "UPDATE Addresses SET street1 = ?, street2 = ?, locality = ?, province = ?, country = ?, planet = ?, postalCode = ? WHERE id = ?",
            arrayOf(address.street1, address.street2, address.locality, address.province, address.country, address.planet, address.postalCode, addressID)
        )

        if(address.isPrimary) {
            execSQL(
                "UPDATE UserAddresses SET isPrimary = 0 WHERE username = ?",
                arrayOf(username)
            )
        }

        execSQL(
            "UPDATE UserAddresses SET name = ?, isPrimary = ? WHERE username = ? AND addressID = ?",
            arrayOf(address.name, address.isPrimary, username, addressID)
        )

        val addresses = addressDetails(username)

        setTransactionSuccessful()

        return addresses
    } finally {
        endTransaction()
    }
}

fun SQLiteDatabase.deleteAddress(
    username: String,
    addressID: Int
): List<UserAddressDetails> {
    beginTransaction()

    try {
        execSQL(
            "DELETE FROM Addresses WHERE id = ?",
            arrayOf(addressID)
        )

        execSQL(
            "DELETE FROM UserAddresses WHERE addressID = ?",
            arrayOf(addressID)
        )

        val addresses = addressDetails(username)

        setTransactionSuccessful()

        return addresses
    } finally {
        endTransaction()
    }
}

fun SQLiteDatabase.paymentMethodDetails(username: String): List<PaymentMethodDetails> {
    return rawQuery(
        """
            SELECT
                id,
                name, 
                number,
                isPrimary 
            FROM PaymentMethods
            WHERE username = ?
            """,
        arrayOf(username)
    ).use { cursor ->
        val results = mutableListOf<PaymentMethodDetails>()

        while(cursor.moveToNext()) {
            results.add(
                PaymentMethodDetails(
                    id = cursor.getInt(0),
                    name = cursor.getString(1),
                    number = cursor.getString(2),
                    isPrimary = cursor.getInt(3) != 0
                )
            )
        }

        return@use results.toImmutableList()
    }
}

fun SQLiteDatabase.createPaymentMethod(
    username: String,
    paymentMethod: EditPaymentMethod
): List<PaymentMethodDetails> {
    beginTransaction()

    try {
        if(paymentMethod.isPrimary) {
            execSQL(
                "UPDATE PaymentMethods SET isPrimary = 0 WHERE username = ?",
                arrayOf(username)
            )
        }

        execSQL(
            "INSERT INTO PaymentMethods (username, name, number, isPrimary) VALUES (?, ?, ?, ?)",
            arrayOf(username, paymentMethod.name, paymentMethod.number, paymentMethod.isPrimary)
        )

        val paymentMethods = paymentMethodDetails(username)

        setTransactionSuccessful()

        return paymentMethods
    } finally {
        endTransaction()
    }
}

fun SQLiteDatabase.updatePaymentMethod(
    username: String,
    paymentMethod: EditPaymentMethod,
    paymentMethodID: Int
): List<PaymentMethodDetails> {
    beginTransaction()

    try {
        if(paymentMethod.isPrimary) {
            execSQL(
                "UPDATE PaymentMethods SET isPrimary = 0 WHERE username = ?",
                arrayOf(username)
            )
        }

        execSQL(
            "UPDATE PaymentMethods SET name = ?, number = ?, isPrimary = ? WHERE username = ? AND id = ?",
            arrayOf(paymentMethod.name, paymentMethod.number, paymentMethod.isPrimary, username, paymentMethodID)
        )

        val paymentMethods = paymentMethodDetails(username)

        setTransactionSuccessful()

        return paymentMethods
    } finally {
        endTransaction()
    }
}

fun SQLiteDatabase.deletePaymentMethod(
    username: String,
    paymentMethodID: Int
): List<PaymentMethodDetails> {
    beginTransaction()

    try {
        execSQL(
            "DELETE FROM PaymentMethods WHERE id = ?",
            arrayOf(paymentMethodID)
        )

        val paymentMethods = paymentMethodDetails(username)

        setTransactionSuccessful()

        return paymentMethods
    } finally {
        endTransaction()
    }
}