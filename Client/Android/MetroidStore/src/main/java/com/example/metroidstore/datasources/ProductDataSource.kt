package com.example.metroidstore.datasources

import com.example.metroidstore.model.Product
import kotlinx.collections.immutable.ImmutableList

interface ProductDataSource {
    suspend fun getProducts(): ImmutableList<Product>
}