package com.example.metroidstore.embeddedbackend

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import io.ktor.http.ContentType
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.serializer

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

                get("/images/{imageId}") {
                    val image = database.image(call.parameters["imageId"]!!.toInt())
                    call.respondBytes(image, ContentType.defaultForFileExtension("png"))
                }
            }
        }.start()
    }
}