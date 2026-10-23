package org.rembx.jeeshop.order

import org.rembx.jeeshop.order.domain.Order

interface PaymentTransactionEngine {
    fun processPayment(order: Order)
}
