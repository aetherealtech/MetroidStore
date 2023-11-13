package com.example.metroidstore.backenddatasources

import com.example.metroidstore.datasources.DataSource
import okhttp3.HttpUrl

class DataSourceBackend(
    host: HttpUrl
): DataSource {
    override val products = ProductDataSourceBackend(host)
}