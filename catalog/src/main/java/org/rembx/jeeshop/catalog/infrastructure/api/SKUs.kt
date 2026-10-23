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
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.catalog.domain.Discount
import org.rembx.jeeshop.catalog.domain.SKU
import org.rembx.jeeshop.catalog.domain.*
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser
import org.rembx.jeeshop.role.AuthorizationUtils.isOwner
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN

@Path("/skus")
@ApplicationScoped
class SKUs(
    @PersistenceUnit(CatalogPersistenceUnit.NAME) private val entityManager: EntityManager,
    private val catalogItemFinder: CatalogItemRepositoryImpl,
    private val presentationResource: PresentationResource<SKU>?
) : CatalogItemService<SKU> {
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun create(@Context securityContext: SecurityContext?, sku: SKU): SKU {
        attachOwner(securityContext, sku)
        sku.discountsIds?.let { ids ->
            sku.discounts = ids.map { entityManager.find(Discount::class.java, it) }.toMutableList()
        }
        entityManager.persist(sku)
        return sku
    }

    @DELETE
    @Transactional
    @Path("/{skuId}")
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    override fun delete(@Context securityContext: SecurityContext?, @PathParam("skuId") skuId: Long?) {
        val sku = entityManager.find(SKU::class.java, skuId)
        checkNotNull(sku)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, sku.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        catalogItemFinder.findForeignHolder(QProduct.product, QProduct.product.childSKUs, sku)
            .forEach { it.childSKUs!!.remove(sku) }
        catalogItemFinder.findForeignHolder(QDiscount.discount, QDiscount.discount.skus, sku)
            .forEach { discount ->
                sku.discounts!!.remove(discount)
                discount.skus!!.remove(sku)
            }
        entityManager.remove(sku)
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun modify(@Context securityContext: SecurityContext?, sku: SKU): SKU {
        val original = entityManager.find(SKU::class.java, sku.id)
        checkNotNull(original)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, original.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        sku.discounts = sku.discountsIds?.map { entityManager.find(Discount::class.java, it) }?.toMutableList()
            ?: original.discounts
        sku.presentationByLocale = original.presentationByLocale
        return entityManager.merge(sku)
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{skuId}/discounts")
    fun attachDiscounts(@PathParam("skuId") skuId: Long?, discountsIds: List<Long>): SKU {
        val sku = entityManager.find(SKU::class.java, skuId)
        checkNotNull(sku)
        val discounts = discountsIds.map { entityManager.find(Discount::class.java, it) }
        if (sku.discounts != null) sku.discounts!!.addAll(discounts)
        else sku.discounts = discounts.toMutableList()
        return entityManager.merge(sku)
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
    ): List<SKU> = if (search != null) {
        catalogItemFinder.findBySearchCriteria(QSKU.sKU, search, start, size, orderBy, isDesc, locale)
    } else catalogItemFinder.findAll(QSKU.sKU, start, size, orderBy, isDesc, locale)

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(STORE_ADMIN)
    fun findManagedSKUs(@Context context: SecurityContext?): List<SKU> =
        catalogItemFinder.findByOwner(QSKU.sKU, context?.userPrincipal?.name ?: "")

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun count(@QueryParam("search") search: String?): Long =
        if (search != null) catalogItemFinder.countBySearchCriteria(QSKU.sKU, search)
        else catalogItemFinder.countAll(QSKU.sKU)

    @GET
    @Path("/{skuId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    override fun find(
        @Context securityContext: SecurityContext?,
        @PathParam("skuId") @NotNull productId: Long?,
        @QueryParam("locale") locale: String?
    ): SKU {
        val sku = entityManager.find(SKU::class.java, productId)
        checkNotNull(sku)
        return if (isAdminUser(securityContext) || isOwner(securityContext, sku.owner)) sku
        else catalogItemFinder.filterVisible(sku, locale)
    }

    @GET
    @Path("/{skuId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN, ADMIN_READONLY)
    override fun findPresentationsLocales(
        @Context securityContext: SecurityContext?,
        @PathParam("skuId") @NotNull catalogId: Long?
    ): Set<String> {
        val sku = entityManager.find(SKU::class.java, catalogId)
        checkNotNull(sku)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, sku.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        return sku.presentationByLocale!!.keys
    }

    @Path("/{skuId}/presentations/{locale}")
    @PermitAll
    override fun findPresentationByLocale(
        @PathParam("skuId") @NotNull productId: Long?,
        @NotNull @PathParam("locale") locale: String
    ): PresentationResource<SKU> {
        val sku = entityManager.find(SKU::class.java, productId)
        checkNotNull(sku)
        return presentationResource!!.init(SKU::class.java, sku, locale, sku.presentationByLocale!![locale])
    }

    @GET
    @Path("/{skuId}/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN, ADMIN_READONLY)
    fun findDiscounts(@Context securityContext: SecurityContext?, @PathParam("skuId") @NotNull skuId: Long?): List<Discount> {
        val sku = entityManager.find(SKU::class.java, skuId)
        checkNotNull(sku)
        val discounts = sku.discounts!!
        if (discounts.isEmpty()) return emptyList()
        return if (isAdminUser(securityContext) || isOwner(securityContext, sku.owner)) discounts
        else catalogItemFinder.findVisibleCatalogItems(QDiscount.discount, discounts, null)
    }

    private fun checkNotNull(sku: SKU?) {
        if (sku == null) throw WebApplicationException(Response.Status.NOT_FOUND)
    }
}
