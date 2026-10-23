package org.rembx.jeeshop.order.domain

import org.rembx.jeeshop.stereotype.Repository
import org.rembx.jeeshop.catalog.domain.Store
import org.rembx.jeeshop.user.domain.User

@Repository
interface OrderRepository {
    fun countUserCompletedOrders(user: User?): Long
    fun countAll(searchCriteria: String?, status: OrderStatus?, skuId: Long?): Long
    fun findAll(
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
        searchCriteria: String?,
        status: OrderStatus?,
        skuId: Long?,
        enhanceResult: Boolean,
    ): List<Order>
    fun findByCustomer(
        user: User?,
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
        status: OrderStatus?,
    ): List<Order>
    fun findByOwner(
        store: Store,
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
        status: OrderStatus?,
    ): List<Order>
    fun enhanceOrder(order: Order)
}
