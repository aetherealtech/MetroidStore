package com.example.metroidstore.datasources

interface DataSource {
    val products: ProductDataSource
    val cart: CartDataSource
}