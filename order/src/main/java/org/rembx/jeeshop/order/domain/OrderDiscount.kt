package org.rembx.jeeshop.order.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Transient
import jakarta.validation.constraints.NotNull

@Embeddable
class OrderDiscount(
    @field:NotNull
    @field:Column(name = "discount_id")
    var discountId: Long? = null,
    var discountValue: Double? = null,
) {
    @field:Transient
    var displayName: String? = null

    @field:Transient
    var presentationImageURI: String? = null

    @field:Transient
    var rateType: Boolean? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as OrderDiscount
        return discountId == other.discountId && discountValue == other.discountValue &&
            displayName == other.displayName && presentationImageURI == other.presentationImageURI
    }

    override fun hashCode(): Int {
        var result = discountId?.hashCode() ?: 0
        result = 31 * result + (discountValue?.hashCode() ?: 0)
        result = 31 * result + (displayName?.hashCode() ?: 0)
        result = 31 * result + (presentationImageURI?.hashCode() ?: 0)
        return result
    }
}
