package org.rembx.jeeshop.catalog.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType
import java.util.Date

/** Discount entity. */
@Entity
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
class Discount : CatalogItem {
    enum class Type {
        DISCOUNT_RATE,
        ORDER_DISCOUNT_AMOUNT,
        SHIPPING_FEE_DISCOUNT_AMOUNT
    }

    enum class Trigger {
        QUANTITY,
        AMOUNT,
        ORDER_NUMBER
    }

    enum class ApplicableTo {
        ORDER,
        ITEM
    }

    @field:Size(max = 100)
    @field:Column(length = 100)
    var voucherCode: String? = null

    var usesPerCustomer: Int? = null

    @field:Enumerated(EnumType.STRING)
    @field:Column(length = 50)
    var type: Type? = null

    @field:Enumerated(EnumType.STRING)
    @field:Column(length = 50)
    var triggerRule: Trigger? = null

    @field:Enumerated(EnumType.STRING)
    @field:NotNull
    @field:Column(length = 10, nullable = false)
    var applicableTo: ApplicableTo? = null

    var triggerValue: Double? = null
    var discountValue: Double? = null

    @field:ManyToMany(mappedBy = "discounts")
    @field:JsonbTransient
    var skus: MutableList<SKU>? = null

    @field:ManyToMany(mappedBy = "discounts")
    @field:JsonbTransient
    var products: MutableList<Product>? = null

    @field:Transient
    var rateType: Boolean? = null

    /** Cannot be used with other discounts when true. */
    var uniqueUse: Boolean? = null

    constructor()

    constructor(id: Long?) : this() {
        this.id = id
    }

    constructor(
        name: String,
        description: String?,
        applicableTo: ApplicableTo?,
        type: Type?,
        triggerRule: Trigger?,
        voucherCode: String?,
        discountValue: Double?,
        triggerValue: Double?,
        usesPerCustomer: Int?,
        uniqueUse: Boolean?,
        startDate: Date?,
        endDate: Date?,
        disabled: Boolean?,
        owner: String?
    ) : this() {
        this.name = name
        this.description = description
        this.applicableTo = applicableTo
        this.type = type
        this.triggerRule = triggerRule
        this.voucherCode = voucherCode
        this.discountValue = discountValue
        this.triggerValue = triggerValue
        this.usesPerCustomer = usesPerCustomer
        this.uniqueUse = uniqueUse
        this.startDate = startDate
        this.endDate = endDate
        this.disabled = disabled
        this.owner = owner
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    protected fun comptuteRateType() {
        rateType = type == Type.DISCOUNT_RATE
    }

    fun isEligible(itemsPrice: Double?): Boolean {
        if (triggerRule == Trigger.AMOUNT && triggerValue != null && itemsPrice!! < triggerValue!!) return false
        return true
    }

    fun processDiscount(currentPrice: Double?, originItemsPrice: Double?): Double? {
        if (!isEligible(originItemsPrice)) return currentPrice

        return when (type!!) {
            Type.DISCOUNT_RATE -> currentPrice!! - originItemsPrice!! * discountValue!! / 100
            Type.ORDER_DISCOUNT_AMOUNT,
            Type.SHIPPING_FEE_DISCOUNT_AMOUNT -> currentPrice!! - discountValue!!
        }
    }

    fun getTrigger(): Trigger? = triggerRule

    fun setTrigger(trigger: Trigger?) {
        triggerRule = trigger
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass || !super.equals(other)) return false
        other as Discount
        return applicableTo == other.applicableTo &&
            discountValue == other.discountValue &&
            triggerRule == other.triggerRule &&
            triggerValue == other.triggerValue &&
            type == other.type &&
            uniqueUse == other.uniqueUse &&
            usesPerCustomer == other.usesPerCustomer &&
            voucherCode == other.voucherCode
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (voucherCode?.hashCode() ?: 0)
        result = 31 * result + (usesPerCustomer?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (triggerRule?.hashCode() ?: 0)
        result = 31 * result + (applicableTo?.hashCode() ?: 0)
        result = 31 * result + (triggerValue?.hashCode() ?: 0)
        result = 31 * result + (discountValue?.hashCode() ?: 0)
        result = 31 * result + (uniqueUse?.hashCode() ?: 0)
        return result
    }
}
