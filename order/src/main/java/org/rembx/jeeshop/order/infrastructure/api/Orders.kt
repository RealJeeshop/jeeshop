package org.rembx.jeeshop.order.infrastructure.api

import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.apache.commons.collections4.CollectionUtils
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogItemRepositoryImpl
import org.rembx.jeeshop.catalog.domain.QStore.store
import org.rembx.jeeshop.mail.Mailer
import org.rembx.jeeshop.order.domain.Order
import org.rembx.jeeshop.order.domain.OrderStatus
import org.rembx.jeeshop.order.OrderConfiguration
import org.rembx.jeeshop.order.PaymentTransactionEngine
import org.rembx.jeeshop.order.PriceEngine
import org.rembx.jeeshop.order.domain.OrderRepository
import org.rembx.jeeshop.order.infrastructure.repository.OrderPersistenceUnit
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.USER
import org.rembx.jeeshop.user.domain.MailTemplateRepository
import org.rembx.jeeshop.user.domain.UserRepository

@Path("/orders")
@ApplicationScoped
class Orders(
    @PersistenceUnit(OrderPersistenceUnit.NAME) private val entityManager: EntityManager,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val mailTemplateRepository: MailTemplateRepository,
    private val mailer: Mailer,
    private val priceEngine: PriceEngine,
    private val paymentTransactionEngine: PaymentTransactionEngine,
    private val catalogItemFinder: CatalogItemRepositoryImpl,
) {
    private var orderConfiguration: OrderConfiguration? = null

    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY, USER)
    fun find(
        @Context securityContext: SecurityContext,
        @PathParam("orderId") @NotNull orderId: Long,
        @QueryParam("enhanced") enhanced: Boolean?,
    ): Order {
        val order = entityManager.find(Order::class.java, orderId)
        if (securityContext.isUserInRole(USER) && !securityContext.isUserInRole(ADMIN)) {
            val authenticatedUser = userRepository.findByLogin(securityContext.userPrincipal.name)
            if (order.customer != authenticatedUser) throw WebApplicationException(Response.Status.UNAUTHORIZED)
        }
        if (enhanced == true) orderRepository.enhanceOrder(order)
        checkNotNull(order)
        return order
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun findAll(
        @Context securityContext: SecurityContext,
        @QueryParam("search") search: String?,
        @QueryParam("start") start: Int?,
        @QueryParam("size") size: Int?,
        @QueryParam("orderBy") orderBy: String?,
        @QueryParam("isDesc") isDesc: Boolean?,
        @QueryParam("status") status: OrderStatus?,
        @QueryParam("skuId") skuId: Long?,
        @QueryParam("enhanced") enhanced: Boolean?,
    ): List<Order> = orderRepository.findAll(start, size, orderBy, isDesc, search, status, skuId, enhanced ?: false)

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(STORE_ADMIN, USER)
    fun findManaged(
        @Context securityContext: SecurityContext,
        @QueryParam("search") search: String?,
        @QueryParam("start") start: Int?,
        @QueryParam("size") size: Int?,
        @QueryParam("orderBy") orderBy: String?,
        @QueryParam("isDesc") isDesc: Boolean?,
        @QueryParam("status") status: OrderStatus?,
        @QueryParam("skuId") skuId: Long?,
        @QueryParam("enhanced") enhanced: Boolean?,
    ): List<Order> {
        if (securityContext.isUserInRole(STORE_ADMIN)) {
            return catalogItemFinder.findByOwner(store, securityContext.userPrincipal.name)
                .flatMap { orderRepository.findByOwner(it, start, size, orderBy, isDesc, status) }
        }
        if (securityContext.isUserInRole(USER)) {
            val user = userRepository.findByLogin(securityContext.userPrincipal.name)
            return orderRepository.findByCustomer(user, start, size, orderBy, isDesc, status)
        }
        return emptyList()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER, ADMIN)
    fun create(
        @Context securityContext: SecurityContext,
        order: Order,
        @QueryParam("userLogin") userLogin: String?,
    ): Order {
        checkOrder(order)
        assignOrderToUser(securityContext, order, userLogin)
        order.status = OrderStatus.CREATED
        priceEngine.computePrice(order)
        assignOrderToOrderItems(order)
        entityManager.persist(order)
        paymentTransactionEngine.processPayment(order)
        return order
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    fun modify(@NotNull order: Order): Order {
        val existingOrder = entityManager.find(Order::class.java, order.id)
        checkNotNull(existingOrder)
        order.customer = existingOrder.customer
        order.items.orEmpty().forEach { it.order = order }
        return entityManager.merge(order)
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Path("/{orderId}")
    fun delete(@PathParam("orderId") orderId: Long) {
        val order = entityManager.find(Order::class.java, orderId)
        checkNotNull(order)
        entityManager.remove(order)
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun count(
        @QueryParam("search") search: String?,
        @QueryParam("status") status: OrderStatus?,
        @QueryParam("skuId") skuId: Long?,
    ): Long = orderRepository.countAll(search, status, skuId)

    @GET
    @Path("/fixeddeliveryfee")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun getFixedDeliveryFee(): Double? = orderConfiguration?.getFixedDeliveryFee()

    private fun assignOrderToUser(securityContext: SecurityContext, order: Order, userLogin: String?) {
        if (securityContext.isUserInRole(USER)) {
            order.customer = userRepository.findByLogin(securityContext.userPrincipal.name)
        }
        if (securityContext.isUserInRole(ADMIN)) {
            order.customer = userLogin?.let(userRepository::findByLogin)
        }
    }

    private fun checkNotNull(order: Order?) {
        if (order == null) throw WebApplicationException(Response.Status.NOT_FOUND)
    }

    private fun assignOrderToOrderItems(order: Order) {
        if (CollectionUtils.isEmpty(order.items)) return
        order.items.orEmpty().forEach { item ->
            if (item.id != null) throw WebApplicationException(Response.Status.BAD_REQUEST)
            item.order = order
        }
    }

    private fun checkOrder(order: Order) {
        if (order.id != null || order.deliveryAddress?.id != null || order.billingAddress?.id != null) {
            throw WebApplicationException(Response.Status.BAD_REQUEST)
        }
    }
}
