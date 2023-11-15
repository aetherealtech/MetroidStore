package com.example.metroidstore.model

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

data class ImageID(val value: Int)
interface ImageSource {
    suspend fun load(): ImageBitmap
}

data class ImageSourceData(
    val data: ByteArray
): ImageSource {
    constructor(base64: String) : this(Base64.decode(base64, Base64.DEFAULT))

    override suspend fun load(): ImageBitmap {
        return BitmapFactory.decodeByteArray(
            data,
            0,
            data.size
        )!!.asImageBitmap()
    }
}