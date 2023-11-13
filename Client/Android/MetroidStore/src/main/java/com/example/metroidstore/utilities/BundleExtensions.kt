package com.example.metroidstore.utilities

import android.os.Bundle
import androidx.navigation.NavType
import com.example.metroidstore.model.Product

fun Bundle.getProductID(key: String): Product.ID {
    return Product.ID(getInt(key))
}

fun Bundle.putProductID(key: String, value: Product.ID) {
    putInt(key, value.value)
}

val NavType.Companion.ProductIDType: NavType<Product.ID>
    get() {
        return object: NavType<Product.ID>(false) {
            override val name: String
                get() = "productId"

            override fun get(bundle: Bundle, key: String): Product.ID {
                return bundle.getProductID(key)
            }

            override fun parseValue(value: String): Product.ID {
                return Product.ID(value.toInt())
            }

            override fun put(bundle: Bundle, key: String, value: Product.ID) {
                bundle.putProductID(key, value)
            }
        }
    }