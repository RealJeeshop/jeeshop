package org.rembx.jeeshop.catalog.domain

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType
import java.util.Objects

@Entity
@XmlType
@Table(name = "premises_address")
@XmlAccessorType(XmlAccessType.FIELD)
class PremisesAddress {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:Size(max = 255)
    @field:NotNull
    @field:Column(nullable = false, length = 255)
    var street: String? = null

    @field:Column(nullable = false, length = 255)
    @field:Size(max = 255)
    @field:NotNull
    var city: String? = null

    @field:Column(nullable = false, length = 10)
    @field:Size(min = 1, max = 10)
    @field:NotNull
    var zipCode: String? = null

    @field:NotNull
    @field:Size(min = 3, max = 3)
    @field:Column(nullable = false, length = 3)
    var countryIso3Code: String? = null

    constructor()

    constructor(street: String?, city: String?, zipCode: String?, countryIso3Code: String?) : this() {
        this.street = street
        this.city = city
        this.zipCode = zipCode
        this.countryIso3Code = countryIso3Code
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as PremisesAddress
        return id == other.id &&
            street == other.street &&
            city == other.city &&
            zipCode == other.zipCode &&
            countryIso3Code == other.countryIso3Code
    }

    override fun hashCode(): Int = Objects.hash(id, street, city, zipCode, countryIso3Code)

    override fun toString(): String =
        "PremisesAddress{id=$id, street='$street', city='$city', zipCode='$zipCode', countryIso3Code='$countryIso3Code'}"
}
