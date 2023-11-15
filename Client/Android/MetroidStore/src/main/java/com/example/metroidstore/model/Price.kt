package com.example.metroidstore.model

import java.math.BigDecimal
data class Price(
    val cents: Int
) {
    companion object {
        val zero: Price
            get() = Price(0)
    }

    operator fun plus(other: Price): Price {
        return Price(cents + other.cents)
    }

    operator fun minus(other: Price): Price {
        return Price(cents - other.cents)
    }

    operator fun times(other: Int): Price {
        return Price(cents * other)
    }

    operator fun times(other: BigDecimal): Price {
        return Price((cents.toBigDecimal() * other).toInt())
    }

    operator fun div(other: Int): Price {
        return Price(cents / other)
    }

    operator fun div(other: BigDecimal): Price {
        return Price((cents.toBigDecimal() / other).toInt())
    }
}