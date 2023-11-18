package com.example.metroidstore.model

import java.math.BigDecimal

data class Percent(
    val value: Int
) {
    operator fun plus(other: Percent): Percent {
        return Percent(value + other.value)
    }

    operator fun minus(other: Percent): Percent {
        return Percent(value - other.value)
    }

    operator fun times(other: Percent): Percent {
        return Percent(value * other.value / 100)
    }

    operator fun div(other: Percent): Percent {
        return Percent(value / other.value * 100)
    }

    operator fun times(other: Int): Int {
        return value * other / 100
    }

    operator fun div(other: Int): Int {
        return value / other * 100
    }
}

operator fun Int.times(other: Percent): Int {
    return other * this
}

operator fun Int.div(other: Percent): Int {
    return other / this
}

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

    operator fun times(other: Percent): Price {
        return Price(cents * other)
    }

    operator fun div(other: Int): Price {
        return Price(cents / other)
    }

    operator fun div(other: Percent): Price {
        return Price(cents / other)
    }
}

operator fun Int.times(other: Price): Price {
    return other * this
}

operator fun Percent.times(other: Price): Price {
    return other * this
}

operator fun Int.div(other: Price): Price {
    return other / this
}

operator fun Percent.div(other: Price): Price {
    return other / this
}