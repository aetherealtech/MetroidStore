package com.example.metroidstore.utilities

import android.os.Bundle
import androidx.navigation.NavType
import com.example.metroidstore.model.ProductID

fun Bundle.getProductID(key: String): ProductID {
    return ProductID(getInt(key))
}

fun Bundle.putProductID(key: String, value: ProductID) {
    putInt(key, value.value)
}

val NavType.Companion.ProductIDType: NavType<ProductID>
    get() {
        return object: NavType<ProductID>(false) {
            override val name: String
                get() = "productId"

            override fun get(bundle: Bundle, key: String): ProductID {
                return bundle.getProductID(key)
            }

            override fun parseValue(value: String): ProductID {
                return ProductID(value.toInt())
            }

            override fun put(bundle: Bundle, key: String, value: ProductID) {
                bundle.putProductID(key, value)
            }
        }
    }