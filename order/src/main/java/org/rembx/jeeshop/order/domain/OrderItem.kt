package org.rembx.jeeshop.order.domain

import jakarta.json.bind.annotation.JsonbTransient
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Transient
import jakarta.validation.constraints.NotNull
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType

@Entity
@XmlAccessorType(XmlAccessType.FIELD)
class OrderItem(
    @field:NotNull
    @field:Column(name = "sku_id")
    var skuId: Long? = null,
    @field:NotNull
    @field:Column(name = "product_id")
    var productId: Long? = null,
    @field:NotNull
    @field:Column(nullable = false)
    var quantity: Int? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @field:ManyToOne
    @field:JsonbTransient
    var order: Order? = null

    var price: Double? = null

    @field:Transient
    var displayName: String? = null

    @field:Transient
    var skuReference: String? = null

    @field:Transient
    var presentationImageURI: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as OrderItem
        return displayName == other.displayName && id == other.id &&
            presentationImageURI == other.presentationImageURI && price == other.price &&
            productId == other.productId && quantity == other.quantity && skuId == other.skuId
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (productId?.hashCode() ?: 0)
        result = 31 * result + (skuId?.hashCode() ?: 0)
        result = 31 * result + (quantity?.hashCode() ?: 0)
        result = 31 * result + (displayName?.hashCode() ?: 0)
        result = 31 * result + (presentationImageURI?.hashCode() ?: 0)
        result = 31 * result + (price?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "OrderItem{id=$id, productId=$productId, skuId=$skuId, quantity=$quantity, price=$price, displayName='$displayName', skuReference='$skuReference', presentationImageURI='$presentationImageURI'}"
}
