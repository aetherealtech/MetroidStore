package com.example.metroidstore.backenddatasources

import com.example.metroidstore.backendclient.BackendClient
import com.example.metroidstore.datasources.DataSource
import okhttp3.HttpUrl

class DataSourceBackend(
    host: HttpUrl
): DataSource {
    private val client = BackendClient(host)

    override val products = ProductDataSourceBackend(client)
    override val cart = CartDataSourceBackend(client)
}