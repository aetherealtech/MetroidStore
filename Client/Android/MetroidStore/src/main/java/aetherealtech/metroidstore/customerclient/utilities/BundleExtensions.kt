package aetherealtech.metroidstore.customerclient.utilities

import android.os.Bundle
import androidx.navigation.NavType
import aetherealtech.metroidstore.customerclient.model.OrderID
import aetherealtech.metroidstore.customerclient.model.ProductID

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
                get() = "productID"

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

fun Bundle.getOrderID(key: String): OrderID {
    return OrderID(getInt(key))
}

fun Bundle.putOrderID(key: String, value: OrderID) {
    putInt(key, value.value)
}

val NavType.Companion.OrderIDType: NavType<OrderID>
    get() {
        return object: NavType<OrderID>(false) {
            override val name: String
                get() = "productID"

            override fun get(bundle: Bundle, key: String): OrderID {
                return bundle.getOrderID(key)
            }

            override fun parseValue(value: String): OrderID {
                return OrderID(value.toInt())
            }

            override fun put(bundle: Bundle, key: String, value: OrderID) {
                bundle.putOrderID(key, value)
            }
        }
    }