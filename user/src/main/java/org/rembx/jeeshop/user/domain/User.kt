package org.rembx.jeeshop.user.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.persistence.Transient
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType
import java.util.Date
import java.util.UUID

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "`user`")
class User() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:Column(unique = true, nullable = false, length = 100)
    @field:NotNull
    @field:Email
    var login: String? = null

    @field:Column(nullable = false, length = 100)
    @field:NotNull
    @field:JsonbTransient
    var password: String? = null

    @field:Column(nullable = false, length = 50)
    @field:NotNull
    @field:Size(max = 50)
    var firstname: String? = null

    @field:Column(nullable = false, length = 50)
    @field:NotNull
    @field:Size(max = 50)
    var lastname: String? = null

    @field:Size(max = 30)
    @field:Column(nullable = false)
    var gender: String? = null

    @field:Phone
    var phoneNumber: String? = null

    @field:OneToOne(cascade = [CascadeType.ALL])
    var address: Address? = null

    @field:OneToOne
    var deliveryAddress: Address? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var birthDate: Date? = null

    @field:Transient
    var age: Int? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var creationDate: Date? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var updateDate: Date? = null

    var disabled: Boolean? = null
    var activated: Boolean? = null

    @field:Column(columnDefinition = "BINARY(16)")
    @field:JsonbTransient
    var actionToken: UUID? = null

    var preferredLocale: String? = null
    var newslettersSubscribed: Boolean? = null

    @field:ManyToMany
    @field:JoinTable(
        name = "User_Role",
        joinColumns = [JoinColumn(name = "userId")],
        inverseJoinColumns = [JoinColumn(name = "roleId")],
    )
    var roles: Set<Role>? = null

    constructor(
        login: String?,
        password: String?,
        firstname: String?,
        lastname: String?,
        phoneNumber: String?,
        address: Address?,
        birthDate: Date?,
        preferredLocale: String?,
        deliveryAddress: Address?,
    ) : this() {
        this.login = login
        this.password = password
        this.firstname = firstname
        this.lastname = lastname
        this.phoneNumber = phoneNumber
        this.address = address
        this.birthDate = birthDate
        this.preferredLocale = preferredLocale
        this.deliveryAddress = deliveryAddress
    }

    constructor(
        login: String?,
        password: String?,
        gender: String?,
        firstname: String?,
        lastname: String?,
        phoneNumber: String?,
        address: Address?,
        birthDate: Date?,
        preferredLocale: String?,
        deliveryAddress: Address?,
    ) : this(login, password, firstname, lastname, phoneNumber, address, birthDate, preferredLocale, deliveryAddress) {
        this.gender = gender
    }

    @PrePersist
    fun prePersist() {
        creationDate = Date()
        activated = false
    }

    @PreUpdate
    fun preUpdate() {
        updateDate = Date()
    }

    override fun toString(): String =
        "User{actionToken=$actionToken, id=$id, login='$login', password='$password', firstname='$firstname', lastname='$lastname, phoneNumber='$phoneNumber', birthDate=$birthDate, age=$age, disabled=$disabled, activated=$activated}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as User
        return actionToken == other.actionToken &&
            activated == other.activated &&
            age == other.age &&
            birthDate == other.birthDate &&
            disabled == other.disabled &&
            firstname == other.firstname &&
            gender == other.gender &&
            id == other.id &&
            lastname == other.lastname &&
            login == other.login &&
            password == other.password &&
            phoneNumber == other.phoneNumber &&
            preferredLocale == other.preferredLocale
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (login?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + (firstname?.hashCode() ?: 0)
        result = 31 * result + (lastname?.hashCode() ?: 0)
        result = 31 * result + (gender?.hashCode() ?: 0)
        result = 31 * result + (phoneNumber?.hashCode() ?: 0)
        result = 31 * result + (birthDate?.hashCode() ?: 0)
        result = 31 * result + (age?.hashCode() ?: 0)
        result = 31 * result + (disabled?.hashCode() ?: 0)
        result = 31 * result + (activated?.hashCode() ?: 0)
        result = 31 * result + (actionToken?.hashCode() ?: 0)
        result = 31 * result + (preferredLocale?.hashCode() ?: 0)
        return result
    }
}
