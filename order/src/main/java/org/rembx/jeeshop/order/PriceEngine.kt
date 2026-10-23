package org.rembx.jeeshop.order

import jakarta.enterprise.context.ApplicationScoped
import org.rembx.jeeshop.order.domain.Order

@ApplicationScoped
interface PriceEngine {
    fun computePrice(order: Order)
}
