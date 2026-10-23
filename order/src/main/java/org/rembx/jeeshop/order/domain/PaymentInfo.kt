package org.rembx.jeeshop.order.domain

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
class PaymentInfo(var paymentFormResponse: String? = null) {
    override fun equals(other: Any?): Boolean =
        this === other || (other is PaymentInfo && paymentFormResponse == other.paymentFormResponse)

    override fun hashCode(): Int = paymentFormResponse?.hashCode() ?: 0
}
