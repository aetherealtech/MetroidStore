//package com.example.metroidstore.localdatasources
//
//import android.database.sqlite.SQLiteDatabase
//import android.graphics.BitmapFactory
//import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.graphics.asImageBitmap
//import com.example.metroidstore.model.ImageSource
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//data class ImageSourceDatabase(
//    private val database: SQLiteDatabase,
//    private val imageID: Int
//): ImageSource {
//    override suspend fun load(): ImageBitmap {
//        return withContext(Dispatchers.IO) {
//            return@withContext database.rawQuery(
//                "SELECT data FROM Images WHERE id = ?",
//                arrayOf("$imageID")
//            ).use { cursor ->
//                cursor.moveToFirst()
//
//                val data = cursor.getBlob(0)
//
//                return@use BitmapFactory.decodeByteArray(
//                    data,
//                    0,
//                    data.size
//                )!!.asImageBitmap()
//            }
//        }
//    }
//}