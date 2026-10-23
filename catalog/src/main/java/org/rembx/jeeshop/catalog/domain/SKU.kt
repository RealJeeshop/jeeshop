package org.rembx.jeeshop.catalog.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType
import java.util.Date

/** Stock keeping unit. */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
class SKU : CatalogItem {
    var price: Double? = null

    @field:Size(min = 3, max = 3)
    @field:Column(length = 3)
    var currency: String? = null

    @field:Size(max = 50)
    @field:Column(length = 50)
    var reference: String? = null

    var threshold: Int? = null
    var quantity: Int? = null

    @field:ManyToMany(cascade = [CascadeType.PERSIST])
    @field:JoinTable(
        joinColumns = [JoinColumn(name = "skuId")],
        inverseJoinColumns = [JoinColumn(name = "discountId")]
    )
    @field:OrderColumn(name = "orderIdx")
    var discounts: MutableList<Discount>? = null

    @field:Transient
    var discountsIds: MutableList<Long>? = null

    /** Calculated field true if quantity > threshold. */
    @field:Transient
    var available: Boolean = false

    constructor()

    constructor(id: Long?, name: String?) : this() {
        this.id = id
        this.name = name
    }

    constructor(name: String?, description: String?) : this() {
        this.name = name
        this.description = description
    }

    constructor(
        name: String?,
        description: String?,
        price: Double?,
        quantity: Int?,
        reference: String?,
        startDate: Date?,
        endDate: Date?,
        disabled: Boolean?,
        threshold: Int?,
        owner: String?
    ) : this() {
        this.name = name
        this.description = description
        this.price = price
        this.quantity = quantity
        this.reference = reference
        this.disabled = disabled
        this.startDate = startDate
        this.endDate = endDate
        this.threshold = threshold
        this.owner = owner
    }

    constructor(
        id: Long?,
        name: String?,
        description: String?,
        price: Double?,
        quantity: Int?,
        reference: String?,
        startDate: Date?,
        endDate: Date?,
        disabled: Boolean?,
        threshold: Int?
    ) : this() {
        this.id = id
        this.name = name
        this.description = description
        this.price = price
        this.quantity = quantity
        this.reference = reference
        this.disabled = disabled
        this.startDate = startDate
        this.endDate = endDate
        this.threshold = threshold
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private fun fillTransient() {
        val currentQuantity = quantity ?: run {
            available = false
            return
        }
        if (threshold == null) threshold = 0
        available = currentQuantity > threshold!!
    }

    fun isAvailable(): Boolean = available

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass || !super.equals(other)) return false
        other as SKU
        return available == other.available &&
            currency == other.currency &&
            price == other.price &&
            quantity == other.quantity &&
            reference == other.reference &&
            threshold == other.threshold
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (price?.hashCode() ?: 0)
        result = 31 * result + (currency?.hashCode() ?: 0)
        result = 31 * result + (reference?.hashCode() ?: 0)
        result = 31 * result + (threshold?.hashCode() ?: 0)
        result = 31 * result + (quantity?.hashCode() ?: 0)
        result = 31 * result + available.hashCode()
        return result
    }
}
