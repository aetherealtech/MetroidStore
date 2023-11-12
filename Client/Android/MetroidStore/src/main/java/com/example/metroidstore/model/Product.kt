package com.example.metroidstore.model

import kotlinx.collections.immutable.ImmutableList
import java.math.BigDecimal

data class Product(
    val name: String,
    val type: String,
    val game: String,
    val ratings: ImmutableList<Rating>,
    val price: BigDecimal
)

enum class Rating(val value: Int) {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5)
}

val List<Rating>.rating: Float
    get() {
        val sum = map { rating -> rating.value }
            .reduce { lhs, rhs -> lhs + rhs }

        return sum.toFloat() / size
    }