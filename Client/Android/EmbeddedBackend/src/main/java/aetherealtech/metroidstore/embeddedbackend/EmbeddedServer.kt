package aetherealtech.metroidstore.embeddedbackend

import aetherealtech.metroidstore.backendmodel.EditAddress
import aetherealtech.metroidstore.backendmodel.EditPaymentMethod
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import aetherealtech.metroidstore.backendmodel.NewOrder
import aetherealtech.metroidstore.backendmodel.NewUser
import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.header
import io.ktor.server.request.receiveParameters
import io.ktor.server.request.receiveText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EmbeddedServer(
    port: UShort,
    context: Context
) {
    private val database: SQLiteDatabase

    init {
        database = EmbeddedDatabase.load(context)

        embeddedServer(Netty, port = port.toInt()) {
            routing {
                post("/users") {
                    val newUser = Json.decodeFromString<NewUser>(call.receiveText())

                    try {
                        val salt = String.random(8)

                        val passhash = BCrypt.withDefaults().hashToString(
                            10,
                            "${newUser.password}${salt}".toCharArray()
                        )

                        val success = database.createUser(
                            newUser.username,
                            passhash,
                            salt
                        )

                        if(!success) {
                            call.respond(HttpStatusCode.BadRequest)
                        } else {
                            call.respondText("")
                        }
                    } catch(error: Exception) {
                        println(error.localizedMessage)
                    }
                }

                post("/login") {
                    val parameters = call.receiveParameters()

                    val username = parameters["username"]!!
                    val password = parameters["password"]!!

                    try {
                        val authDetails = database.passhash(username)

                        val success = BCrypt.verifyer().verify(
                            "${password}${authDetails.salt}".toCharArray(),
                            authDetails.passhash
                        )

                        if(!success.verified) {
                            call.respond(HttpStatusCode.BadRequest)
                        } else {
                            call.respondText(username)
                        }
                    } catch(error: Exception) {
                        println(error.localizedMessage)
                    }
                }

                post("/logout") {
                    // Nothing to do
                    call.respondText("")
                }

                get("/products") {
                    val query = call.request.queryParameters["query"]

                    try {
                        val products = database.products(query)
                        call.respondText(Json.encodeToString(products))
                    } catch(error: Exception) {
                        println(error.localizedMessage)
                    }
                }

                get("/products/{productID}") {
                    val product = database.product(call.parameters["productID"]!!.toInt())
                    if(product != null) {
                        call.respondText(Json.encodeToString(product))
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                get("/images/{imageID}") {
                    val image = database.image(call.parameters["imageID"]!!.toInt())
                    call.respondBytes(image, ContentType.defaultForFileExtension("png"))
                }

                get("/cart") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val cart = database.cart(username)
                    call.respondText(Json.encodeToString(cart))
                }

                post("/cart/{productID}") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                    val productID = call.parameters["productID"]!!.toInt()

                    val cart = database.addToCart(username, productID)
                    call.respondText(Json.encodeToString(cart))
                }

                delete("/cart/{productID}") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@delete
                    }

                    val productID = call.parameters["productID"]!!.toInt()

                    val cart = database.removeFromCart(username, productID)
                    call.respondText(Json.encodeToString(cart))
                }

                post("/cart/{productID}/decrement") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                    val productID = call.parameters["productID"]!!.toInt()

                    val cart = database.decrementCartQuantity(username, productID)
                    call.respondText(Json.encodeToString(cart))
                }

                post("/cart/{productID}/increment") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                    val productID = call.parameters["productID"]!!.toInt()

                    val cart = database.incrementCartQuantity(username, productID)
                    call.respondText(Json.encodeToString(cart))
                }

                get("/addresses") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val addresses = database.addresses(username)
                    call.respondText(Json.encodeToString(addresses))
                }

                get("/addresses/details") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val addresses = database.addressDetails(username)
                    call.respondText(Json.encodeToString(addresses))
                }

                post("/addresses") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                    val newAddress = Json.decodeFromString<EditAddress>(call.receiveText())

                    val addresses = database.createAddress(username, newAddress)
                    call.respondText(Json.encodeToString(addresses))
                }

                patch("/addresses/{addressID}") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@patch
                    }

                    val addressID = call.parameters["addressID"]!!.toInt()

                    val newAddress = Json.decodeFromString<EditAddress>(call.receiveText())

                    val addresses = database.updateAddress(username, newAddress, addressID)
                    call.respondText(Json.encodeToString(addresses))
                }

                delete("/addresses/{addressID}") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@delete
                    }

                    val addressID = call.parameters["addressID"]!!.toInt()

                    val addresses = database.deleteAddress(username, addressID)
                    call.respondText(Json.encodeToString(addresses))
                }

                get("/shippingMethods") {
                    val shippingMethods = database.shippingMethods()
                    call.respondText(Json.encodeToString(shippingMethods))
                }


                get("/paymentMethods") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val paymentMethods = database.paymentMethods(username)
                    call.respondText(Json.encodeToString(paymentMethods))
                }

                get("/paymentMethods/details") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val paymentMethods = database.paymentMethodDetails(username)
                    call.respondText(Json.encodeToString(paymentMethods))
                }

                post("/paymentMethods") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                    val newPaymentMethod = Json.decodeFromString<EditPaymentMethod>(call.receiveText())

                    val paymentMethods = database.createPaymentMethod(username, newPaymentMethod)
                    call.respondText(Json.encodeToString(paymentMethods))
                }

                patch("/paymentMethods/{paymentMethodID}") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@patch
                    }

                    val paymentMethodID = call.parameters["paymentMethodID"]!!.toInt()

                    val newPaymentMethod = Json.decodeFromString<EditPaymentMethod>(call.receiveText())

                    val paymentMethods = database.updatePaymentMethod(username, newPaymentMethod, paymentMethodID)
                    call.respondText(Json.encodeToString(paymentMethods))
                }

                delete("/paymentMethods/{paymentMethodID}") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@delete
                    }

                    val paymentMethodID = call.parameters["paymentMethodID"]!!.toInt()

                    val paymentMethods = database.deletePaymentMethod(username, paymentMethodID)
                    call.respondText(Json.encodeToString(paymentMethods))
                }

                get("/orders") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val orders = database.orders(username)
                    call.respondText(Json.encodeToString(orders))
                }

                post("/orders") {
                    val username = call.request.header("Authorization")
                    if(username == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                    val newOrder = Json.decodeFromString<NewOrder>(call.receiveText())

                    val orderID = database.placeOrder(
                        username = username,
                        addressID = newOrder.addressID,
                        shippingMethodName = newOrder.shippingMethodName,
                        paymentMethodID = newOrder.paymentMethodID
                    )

                    call.respondText("$orderID")
                }

                get("/orders/{orderID}") {
                    val orderID = call.parameters["orderID"]!!.toInt()

                    val order = database.order(
                        orderID = orderID
                    )

                    call.respondText(Json.encodeToString(order))
                }

                get("/orders/{orderID}/activity") {
                    val orderID = call.parameters["orderID"]!!.toInt()

                    val activities = database.orderActivity(
                        orderID = orderID
                    )

                    call.respondText(Json.encodeToString(activities))
                }
            }
        }.start()
    }
}

fun String.Companion.random(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (0..< length)
        .map { allowedChars.random() }
        .joinToString("")
}