package org.rembx.jeeshop.order.infrastructure.api

import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext
import org.rembx.jeeshop.catalog.infrastructure.repository.DiscountRepositoryImpl
import org.rembx.jeeshop.catalog.domain.Discount
import org.rembx.jeeshop.order.domain.OrderRepository
import org.rembx.jeeshop.role.JeeshopRoles
import org.rembx.jeeshop.user.domain.UserRepository

@Path("discounts/eligible")
@ApplicationScoped
class EligibleDiscounts(
    private val userRepository: UserRepository,
    private val discountFinder: DiscountRepositoryImpl,
    private val orderRepository: OrderRepository,
) {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.USER)
    fun findEligible(
        @Context securityContext: SecurityContext,
        @QueryParam("locale") locale: String?,
    ): List<Discount>? {
        val currentUser = userRepository.findByLogin(securityContext.userPrincipal.name)
        val completedOrders = orderRepository.countUserCompletedOrders(currentUser)
        return discountFinder.findEligibleOrderDiscounts(locale, completedOrders)
    }
}
