package com.example.metroidstore.model

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
            .fold(0) { lhs, rhs -> lhs + rhs }

        return sum.toFloat() / size
    }