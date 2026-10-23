package org.rembx.jeeshop.order

import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Default
import jakarta.persistence.EntityManager
import org.apache.commons.collections4.CollectionUtils
import org.rembx.jeeshop.catalog.infrastructure.repository.DiscountRepositoryImpl
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.catalog.domain.Discount
import org.rembx.jeeshop.catalog.domain.SKU
import org.rembx.jeeshop.order.domain.Order
import org.rembx.jeeshop.order.domain.OrderDiscount
import org.rembx.jeeshop.order.domain.OrderRepository

@ApplicationScoped
@Default
class PriceEngineImpl(
    @PersistenceUnit(CatalogPersistenceUnit.NAME) private val entityManager: EntityManager,
    private val orderConfiguration: OrderConfiguration,
    private val orderRepository: OrderRepository,
    private val discountFinder: DiscountRepositoryImpl,
) : PriceEngine {
    override fun computePrice(order: Order) {
        if (CollectionUtils.isEmpty(order.items)) {
            throw IllegalStateException("Order items list is empty $order")
        }

        var price = 0.0
        for (orderItem in order.items!!) {
            val sku = entityManager.find(SKU::class.java, orderItem.skuId)
            val skuPrice = checkNotNull(sku.price)
            price += skuPrice * orderItem.quantity!!
            orderItem.price = skuPrice
        }

        val fixedDeliveryFee = orderConfiguration.getFixedDeliveryFee()
        price = applyEligibleDiscounts(order, price)
        if (fixedDeliveryFee != null) price += fixedDeliveryFee
        order.deliveryFee = fixedDeliveryFee
        order.price = price
    }

    private fun applyEligibleDiscounts(order: Order, originalPrice: Double): Double {
        var price = originalPrice
        val userCompletedOrders = orderRepository.countUserCompletedOrders(order.customer)
        val eligibleDiscounts = discountFinder.findEligibleOrderDiscounts(null, userCompletedOrders) ?: return price
        val orderDiscounts = order.orderDiscounts ?: HashSet<OrderDiscount>().also { order.orderDiscounts = it }

        for (discount: Discount in eligibleDiscounts) {
            if (discount.isEligible(originalPrice)) {
                price = checkNotNull(discount.processDiscount(price, originalPrice))
                orderDiscounts.add(OrderDiscount(discount.id, discount.discountValue))
            }
        }
        return price
    }
}
