package com.example.metroidstore.embeddedbackend

import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.metroidstore.model.ImageSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DatabaseImageSource(
    private val database: SQLiteDatabase,
    private val imageId: Int
): ImageSource {
    override suspend fun load(): ImageBitmap {
        return withContext(Dispatchers.IO) {
            return@withContext database.rawQuery(
                "SELECT data FROM Images WHERE id = ?",
                arrayOf("$imageId")
            ).use { cursor ->
                cursor.moveToFirst()

                val data = cursor.getBlob(0)

                return@use BitmapFactory.decodeByteArray(
                    data,
                    0,
                    data.size
                )!!.asImageBitmap()
            }
        }
    }
}