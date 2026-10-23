package org.rembx.jeeshop.catalog.infrastructure.api

import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogItemRepositoryImpl
import org.rembx.jeeshop.catalog.infrastructure.repository.DiscountRepositoryImpl
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.catalog.domain.Discount
import org.rembx.jeeshop.catalog.domain.*
import org.rembx.jeeshop.catalog.domain.Discount.ApplicableTo
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser
import org.rembx.jeeshop.role.AuthorizationUtils.isOwner
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN

@Path("/discounts")
@ApplicationScoped
class Discounts(
    @PersistenceUnit(CatalogPersistenceUnit.NAME) private val entityManager: EntityManager,
    private val catalogItemFinder: CatalogItemRepositoryImpl,
    private val presentationResource: PresentationResource<Discount>?,
    private val discountFinder: DiscountRepositoryImpl
) : CatalogItemService<Discount> {
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun create(@Context securityContext: SecurityContext?, discount: Discount): Discount {
        attachOwner(securityContext, discount)
        entityManager.persist(discount)
        return discount
    }

    @DELETE
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{discountId}")
    override fun delete(@Context securityContext: SecurityContext?, @PathParam("discountId") discountId: Long?) {
        val discount = entityManager.find(Discount::class.java, discountId)
        checkNotNull(discount)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, discount.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        catalogItemFinder.findForeignHolder(QProduct.product, QProduct.product.discounts, discount)
            .forEach { it.discounts!!.remove(discount) }
        catalogItemFinder.findForeignHolder(QSKU.sKU, QSKU.sKU.discounts, discount)
            .forEach { it.discounts!!.remove(discount) }
        entityManager.remove(discount)
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun modify(@Context securityContext: SecurityContext?, discount: Discount): Discount {
        val original = entityManager.find(Discount::class.java, discount.id)
        checkNotNull(original)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, discount.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        discount.presentationByLocale = original.presentationByLocale
        return entityManager.merge(discount)
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    override fun findAll(
        @QueryParam("search") search: String?,
        @QueryParam("start") start: Int?,
        @QueryParam("size") size: Int?,
        @QueryParam("orderBy") orderBy: String?,
        @QueryParam("isDesc") isDesc: Boolean?,
        @QueryParam("locale") locale: String?
    ): List<Discount> = if (search != null) {
        catalogItemFinder.findBySearchCriteria(QDiscount.discount, search, start, size, orderBy, isDesc, locale)
    } else catalogItemFinder.findAll(QDiscount.discount, start, size, orderBy, isDesc, locale)

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(STORE_ADMIN)
    fun findManagedDiscounts(@Context context: SecurityContext?): List<Discount> =
        catalogItemFinder.findByOwner(QDiscount.discount, context?.userPrincipal?.name ?: "")

    @GET
    @Path("/visible")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    fun findVisible(
        @NotNull @QueryParam("applicableTo") applicableTo: ApplicableTo,
        @QueryParam("locale") locale: String?
    ): List<Discount> = discountFinder.findVisibleDiscounts(applicableTo, locale)

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun count(@QueryParam("search") search: String?): Long =
        if (search != null) catalogItemFinder.countBySearchCriteria(QDiscount.discount, search)
        else catalogItemFinder.countAll(QDiscount.discount)

    @GET
    @Path("/{discountId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN, ADMIN_READONLY)
    override fun find(
        @Context securityContext: SecurityContext?,
        @PathParam("discountId") @NotNull productId: Long?,
        @QueryParam("locale") locale: String?
    ): Discount {
        val discount = entityManager.find(Discount::class.java, productId)
        checkNotNull(discount)
        return if (isAdminUser(securityContext) || isOwner(securityContext, discount.owner)) discount
        else catalogItemFinder.filterVisible(discount, locale)
    }

    @GET
    @Path("/{discountId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN, ADMIN_READONLY)
    override fun findPresentationsLocales(
        @Context securityContext: SecurityContext?,
        @PathParam("discountId") @NotNull catalogId: Long?
    ): Set<String> {
        val discount = entityManager.find(Discount::class.java, catalogId)
        checkNotNull(discount)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, discount.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        return discount.presentationByLocale!!.keys
    }

    @Path("/{discountId}/presentations/{locale}")
    @PermitAll
    override fun findPresentationByLocale(
        @PathParam("discountId") @NotNull productId: Long?,
        @NotNull @PathParam("locale") locale: String
    ): PresentationResource<Discount> {
        val discount = entityManager.find(Discount::class.java, productId)
        checkNotNull(discount)
        return presentationResource!!.init(
            Discount::class.java,
            discount,
            locale,
            discount.presentationByLocale!![locale]
        )
    }

    private fun checkNotNull(discount: Discount?) {
        if (discount == null) throw WebApplicationException(Response.Status.NOT_FOUND)
    }
}
