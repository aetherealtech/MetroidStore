package com.example.metroidstore.model

data class Address(
    val id: ID,
    val street1: Street1,
    val street2: Street2?,
    val locality: Locality,
    val province: Province,
    val country: Country,
    val planet: Planet,
    val postalCode: PostalCode?
) {
    data class ID(val value: Int)
    data class Street1(val value: String)
    data class Street2(val value: String)
    data class Locality(val value: String)
    data class Province(val value: String)
    data class Country(val value: String)
    data class Planet(val value: String)
    data class PostalCode(val value: String)

    companion object {
        fun parse(
            formatted: String,
            id: ID
        ): Address {
            val lines = formatted
                .split("\n")

            if(!(2..3).contains(lines.size))
                throw IllegalArgumentException("Address must contain between 2 and 3 lines, not ${lines.size}")

            val streetLine = lines[0]
            val areaLine = lines[1]
            val postalCode = lines.getOrNull(2)?.let { code -> PostalCode(code) }

            val areaComponents = areaLine
                .split(",")

            if(areaComponents.size != 4)
                throw IllegalArgumentException("Address second line must contain 4 comma separated elements, not ${areaComponents.size}")

            return Address(
                id = id,
                street1 = Street1(streetLine),
                street2 = null,
                locality = Locality(areaComponents[0]),
                province = Province(areaComponents[1]),
                country = Country(areaComponents[2]),
                planet = Planet(areaComponents[3]),
                postalCode = postalCode
            )
        }
    }

    val formatted: String
        get() = "${street1.value} ${street2?.let { value -> " - ${value.value}" } ?: ""}\n${locality.value}, ${province.value}, ${country.value}, $planet${postalCode?.let { code -> "\n${code.value}" } ?: "" }"
}