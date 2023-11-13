package com.example.metroidstore.backenddatasources

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.metroidstore.model.ImageSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await

data class ImageSourceBackend(
    private val client: OkHttpClient,
    private val request: Request
): ImageSource {
    override suspend fun load(): ImageBitmap {
        val response = client.newCall(request).await()

        return withContext(Dispatchers.IO) {
            val body = response.body
            if(body == null)
                throw IllegalStateException("Invalid image request")

            val result = body.bytes()

            val bitmap = BitmapFactory.decodeByteArray(
                result,
                0,
                result.size
            )

            if(bitmap == null)
                throw IllegalStateException("Data is not a valid image")

            return@withContext bitmap.asImageBitmap()
        }
    }
}