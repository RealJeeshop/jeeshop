package org.rembx.jeeshop.order.domain

enum class OrderStatus {
    CREATED,
    VALIDATED,
    PAYMENT_VALIDATED,
    CANCELLED,
    READY_FOR_SHIPMENT,
    SHIPPED,
    DELIVERED,
    RETURNED,
}
