package aetherealtech.metroidstore.customerclient.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ImageID(val value: Int)

interface ImageSource {
    suspend fun load(): Bitmap
}

data class ImageSourceData(
    val data: ByteArray
): ImageSource {
    constructor(base64: String) : this(Base64.decode(base64, Base64.DEFAULT))

    override suspend fun load(): Bitmap {
        return withContext(Dispatchers.IO) {
            BitmapFactory.decodeByteArray(
                data,
                0,
                data.size
            )!!
        }
    }
}