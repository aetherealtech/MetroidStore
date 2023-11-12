package com.example.metroidstore.embeddedbackend

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File

class EmbeddedDatabase {
    companion object {
        fun load(context: Context): SQLiteDatabase {
            val dbFile = File(context.filesDir, "MetroidStore.db")

            if(!dbFile.exists()) {
                dbFile.createNewFile()

                context.assets.open("MetroidStore.db").use { dbAssetStream ->
                    dbFile.outputStream().use { dbFileStream ->
                        dbAssetStream.copyTo(dbFileStream)
                    }
                }
            }

            return SQLiteDatabase.openDatabase(
                dbFile.path,
                null,
                SQLiteDatabase.CREATE_IF_NECESSARY
            )
        }
    }
}