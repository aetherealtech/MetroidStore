package com.example.metroidstore.embeddedbackend

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.metroidstore.backendmodel.NewOrder
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.header
import io.ktor.server.request.receiveText
import kotlinx.serialization.decodeFromString
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
                get("/products") {
                    val products = database.products()
                    call.respondText(Json.encodeToString(products))
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
            }
        }.start()
    }
}