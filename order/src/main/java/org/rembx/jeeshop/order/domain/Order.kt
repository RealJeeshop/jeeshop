package org.rembx.jeeshop.order.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.CollectionTable
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.PostUpdate
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.persistence.Transient
import jakarta.validation.constraints.NotNull
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlRootElement
import org.rembx.jeeshop.user.domain.Address
import org.rembx.jeeshop.user.domain.User
import java.text.SimpleDateFormat
import java.util.Date

@Entity
@Table(name = "orders")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class Order() {
    companion object {
        const val ORDER_REF_SEPARATOR = "-"
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:NotNull
    @field:ManyToOne(targetEntity = User::class)
    @field:JoinColumn(name = "user_id")
    var customer: User? = null

    @field:NotNull
    @field:Column(name = "store_id")
    var storeId: Long? = null

    @field:OneToMany(cascade = [CascadeType.ALL], mappedBy = "order", fetch = FetchType.EAGER)
    var items: MutableSet<OrderItem>? = null

    @field:ElementCollection(fetch = FetchType.EAGER)
    @field:CollectionTable(name = "OrderDiscount", joinColumns = [JoinColumn(name = "order_id")])
    var orderDiscounts: MutableSet<OrderDiscount>? = null

    @field:OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var deliveryAddress: Address? = null

    @field:OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var billingAddress: Address? = null

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false, length = 30)
    var status: OrderStatus? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    @field:Column(nullable = false)
    var creationDate: Date? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var updateDate: Date? = null

    var price: Double? = null

    @field:Column(length = 50)
    var transactionId: String? = null

    @field:Column(length = 50)
    var parcelTrackingKey: String? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var deliveryDate: Date? = null

    @field:Temporal(TemporalType.TIMESTAMP)
    var paymentDate: Date? = null

    @field:Transient
    var paymentInfo: PaymentInfo? = null

    @field:Transient
    var deliveryFee: Double? = null

    @field:Transient
    var vat: Double? = null

    @field:Transient
    var reference: String? = null

    constructor(items: MutableSet<OrderItem>?, deliveryAddress: Address?, billingAddress: Address?) : this() {
        this.items = items
        this.deliveryAddress = deliveryAddress
        this.billingAddress = billingAddress
    }

    constructor(
        user: User?,
        items: MutableSet<OrderItem>?,
        deliveryAddress: Address?,
        billingAddress: Address?,
        status: OrderStatus?,
    ) : this(items, deliveryAddress, billingAddress) {
        customer = user
        this.status = status
    }

    @PrePersist
    fun prePersist() {
        val date = Date()
        creationDate = date
        updateDate = date
    }

    @PreUpdate
    fun preUpdate() {
        updateDate = Date()
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    fun computeOrderReference() {
        val date = creationDate
        reference = (date?.let { SimpleDateFormat("MMddyyyy").format(it) } ?: "") + ORDER_REF_SEPARATOR + id
        if (transactionId != null) reference += ORDER_REF_SEPARATOR + transactionId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as Order
        return id == other.id &&
            parcelTrackingKey == other.parcelTrackingKey &&
            price == other.price &&
            reference == other.reference &&
            status == other.status &&
            transactionId == other.transactionId
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (price?.hashCode() ?: 0)
        result = 31 * result + (transactionId?.hashCode() ?: 0)
        result = 31 * result + (parcelTrackingKey?.hashCode() ?: 0)
        result = 31 * result + (reference?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "Order{id=$id, status=$status, price=$price, transactionId='$transactionId', " +
            "parcelTrackingKey='$parcelTrackingKey', deliveryFee=$deliveryFee, reference='$reference'}"
}
