package com.example.metroidstore.localdatasources

import android.database.sqlite.SQLiteDatabase
import com.example.metroidstore.datasources.DataSource

class DataSourceDatabase(
    database: SQLiteDatabase
): DataSource {
    override val products = ProductDataSourceDatabase(database)
}