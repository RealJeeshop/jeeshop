package org.rembx.jeeshop.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
class Address() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @field:Column(length = 50, nullable = false)
    @field:Size(max = 50)
    @field:NotNull
    var firstname: String? = null

    @field:Column(length = 50, nullable = false)
    @field:Size(max = 50)
    @field:NotNull
    var lastname: String? = null

    @field:Size(max = 30)
    @field:Column(length = 30, nullable = false)
    @field:NotNull
    var gender: String? = null

    @field:Size(max = 100)
    @field:Column(length = 100)
    var company: String? = null

    @field:NotNull
    @field:Size(min = 3, max = 3)
    @field:Column(nullable = false, length = 3)
    var countryIso3Code: String? = null

    constructor(
        street: String?,
        city: String?,
        zipCode: String?,
        firstname: String?,
        lastname: String?,
        gender: String?,
        company: String?,
        countryIso3Code: String?,
    ) : this() {
        this.street = street
        this.city = city
        this.zipCode = zipCode
        this.firstname = firstname
        this.lastname = lastname
        this.gender = gender
        this.company = company
        this.countryIso3Code = countryIso3Code
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as Address
        return id == other.id &&
            street == other.street &&
            city == other.city &&
            zipCode == other.zipCode &&
            firstname == other.firstname &&
            lastname == other.lastname &&
            gender == other.gender &&
            company == other.company &&
            countryIso3Code == other.countryIso3Code
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (street?.hashCode() ?: 0)
        result = 31 * result + (city?.hashCode() ?: 0)
        result = 31 * result + (zipCode?.hashCode() ?: 0)
        result = 31 * result + (firstname?.hashCode() ?: 0)
        result = 31 * result + (lastname?.hashCode() ?: 0)
        result = 31 * result + (gender?.hashCode() ?: 0)
        result = 31 * result + (company?.hashCode() ?: 0)
        result = 31 * result + (countryIso3Code?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "Address{id=$id, street='$street', city='$city', zipCode='$zipCode', firstname='$firstname', lastname='$lastname', gender='$gender', company='$company', countryIso3Code='$countryIso3Code'}"
}
