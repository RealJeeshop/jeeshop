package org.rembx.jeeshop.order.infrastructure.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpressionBase
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import org.apache.commons.lang3.math.NumberUtils
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.catalog.domain.Discount
import org.rembx.jeeshop.catalog.domain.Product
import org.rembx.jeeshop.catalog.domain.SKU
import org.rembx.jeeshop.catalog.domain.Store
import org.rembx.jeeshop.order.OrderConfiguration
import org.rembx.jeeshop.order.domain.Order
import org.rembx.jeeshop.order.domain.OrderRepository
import org.rembx.jeeshop.order.domain.OrderStatus
import org.rembx.jeeshop.order.domain.QOrder.order
import org.rembx.jeeshop.user.domain.User
import org.rembx.jeeshop.order.infrastructure.repository.OrderPersistenceUnit

@ApplicationScoped
class OrderRepositoryImpl(
    @PersistenceUnit(OrderPersistenceUnit.NAME) private val entityManager: EntityManager,
    @PersistenceUnit(CatalogPersistenceUnit.NAME) private val catalogEntityManager: EntityManager,
    private val orderConfiguration: OrderConfiguration,
) : OrderRepository {
    override fun countUserCompletedOrders(user: User?): Long =
        JPAQueryFactory(entityManager).selectFrom(order)
            .where(
                order.customer.eq(user),
                order.status.notIn(OrderStatus.CREATED, OrderStatus.CANCELLED, OrderStatus.RETURNED),
            )
            .fetchCount()

    override fun countAll(searchCriteria: String?, status: OrderStatus?, skuId: Long?): Long =
        JPAQueryFactory(entityManager).selectFrom(order)
            .where(matchesSearchAndStatusAndItemsSkuId(searchCriteria, status, skuId))
            .fetchCount()

    override fun findAll(
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
        searchCriteria: String?,
        status: OrderStatus?,
        skuId: Long?,
        enhanceResult: Boolean,
    ): List<Order> {
        val query = JPAQueryFactory(entityManager).selectFrom(order)
            .where(matchesSearchAndStatusAndItemsSkuId(searchCriteria, status, skuId))
        offset?.let { query.offset(it.toLong()) }
        limit?.let { query.limit(it.toLong()) }
        sortBy(orderBy, isDesc, query)
        return query.fetch().also { orders ->
            if (enhanceResult) orders.forEach(::enhanceOrder)
        }
    }

    override fun findByCustomer(
        user: User?,
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
        status: OrderStatus?,
    ): List<Order> {
        val query = JPAQueryFactory(entityManager).selectFrom(order).where(
            order.customer.eq(user),
            if (status == OrderStatus.CREATED) null else order.status.ne(OrderStatus.CREATED),
            status?.let { order.status.eq(it) },
        )
        offset?.let { query.offset(it.toLong()) }
        limit?.let { query.limit(it.toLong()) }
        sortBy(orderBy, isDesc, query)
        return query.fetch()
    }

    override fun findByOwner(
        store: Store,
        offset: Int?,
        limit: Int?,
        orderBy: String?,
        isDesc: Boolean?,
        status: OrderStatus?,
    ): List<Order> {
        val query = JPAQueryFactory(entityManager).selectFrom(order).where(
            order.storeId.eq(store.id),
            if (status == OrderStatus.CREATED) null else order.status.ne(OrderStatus.CREATED),
            status?.let { order.status.eq(it) },
        )
        offset?.let { query.offset(it.toLong()) }
        limit?.let { query.limit(it.toLong()) }
        sortBy(orderBy, isDesc, query)
        return query.fetch()
    }

    private fun matchesSearchAndStatusAndItemsSkuId(
        searchCriteria: String?,
        status: OrderStatus?,
        skuId: Long?,
    ): BooleanExpression? {
        var predicate = searchCriteria?.let(::matchesSearchCriteria)
        status?.let { predicate = predicate?.and(order.status.eq(it)) ?: order.status.eq(it) }
        skuId?.let {
            val skuPredicate = order.items.any().skuId.eq(it)
            predicate = predicate?.and(skuPredicate) ?: skuPredicate
        }
        return predicate
    }

    private fun sortBy(orderBy: String?, isDesc: Boolean?, query: JPAQuery<Order>) {
        val property = sortProperties[orderBy] ?: return
        query.orderBy(if (isDesc == true) property.desc() else property.asc())
    }

    private fun matchesSearchCriteria(search: String): BooleanExpression {
        var predicate = order.customer.login.containsIgnoreCase(search)
            .or(order.customer.firstname.containsIgnoreCase(search))
            .or(order.customer.lastname.containsIgnoreCase(search))
            .or(order.transactionId.containsIgnoreCase(search))
        if (NumberUtils.isNumber(search)) {
            predicate = order.id.eq(search.toLong()).or(order.transactionId.eq(search))
        }
        return predicate
    }

    override fun enhanceOrder(orderEntity: Order) {
        val user = orderEntity.customer!!
        orderEntity.items.orEmpty().forEach { orderItem ->
            val product = catalogEntityManager.find(Product::class.java, orderItem.productId)
            val sku = catalogEntityManager.find(SKU::class.java, orderItem.skuId)
            product.setLocalizedPresentation(user.preferredLocale)
            val presentation = product.localizedPresentation
            orderItem.displayName = presentation?.displayName ?: product.name
            orderItem.skuReference = sku.reference
            val image = presentation?.smallImage
            if (image != null) {
                orderItem.presentationImageURI =
                    "products/${orderItem.productId}/${presentation.locale}/${image.uri}"
            }
        }

        orderEntity.orderDiscounts.orEmpty().forEach { orderDiscount ->
            val discount = catalogEntityManager.find(Discount::class.java, orderDiscount.discountId)
            discount.setLocalizedPresentation(user.preferredLocale)
            val presentation = discount.localizedPresentation
            orderDiscount.displayName = presentation?.displayName ?: discount.name
            orderDiscount.rateType = discount.rateType
            val image = presentation?.smallImage
            if (image != null) {
                orderDiscount.presentationImageURI =
                    "discounts/${orderDiscount.discountId}/${presentation.locale}/${image.uri}"
            }
        }
        orderEntity.deliveryFee = orderConfiguration.getFixedDeliveryFee()
        orderEntity.vat = orderConfiguration.getVAT()
    }

    companion object {
        private val sortProperties: Map<String, ComparableExpressionBase<*>> = mapOf(
            "id" to order.id,
            "owner" to order.customer.lastname,
            "login" to order.customer.login,
            "status" to order.status,
            "creationDate" to order.creationDate,
            "updateDate" to order.updateDate,
        )
    }
}
