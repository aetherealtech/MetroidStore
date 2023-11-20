package aetherealtech.metroidstore.customerclient.backendclient

import android.graphics.BitmapFactory
import aetherealtech.metroidstore.customerclient.model.ImageSource
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await

data class ImageSourceBackend(
    private val client: OkHttpClient,
    private val request: Request
): ImageSource {
    override suspend fun load(): Bitmap {
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

            return@withContext bitmap
        }
    }
}