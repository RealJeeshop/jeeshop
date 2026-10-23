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
import org.rembx.jeeshop.catalog.domain.Product
import org.rembx.jeeshop.catalog.domain.SKU
import org.rembx.jeeshop.catalog.domain.*
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser
import org.rembx.jeeshop.role.AuthorizationUtils.isOwner
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN

@Path("/products")
@ApplicationScoped
class Products(
    @PersistenceUnit(CatalogPersistenceUnit.NAME) private val entityManager: EntityManager,
    private val catalogItemFinder: CatalogItemRepositoryImpl,
    private val presentationResource: PresentationResource<Product>?
) : CatalogItemService<Product> {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun create(@Context securityContext: SecurityContext?, product: Product): Product {
        attachOwner(securityContext, product)
        product.childSKUsIds?.let { ids ->
            product.childSKUs = ids.map { entityManager.find(SKU::class.java, it) }.toMutableList()
        }
        product.discountsIds?.let { ids ->
            product.discounts = ids.map { entityManager.find(Discount::class.java, it) }.toMutableList()
        }
        entityManager.persist(product)
        return product
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{productId}")
    override fun delete(@Context securityContext: SecurityContext?, @PathParam("productId") productId: Long?) {
        val product = entityManager.find(Product::class.java, productId)
        checkNotNull(product)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, product.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        catalogItemFinder.findForeignHolder(QCategory.category, QCategory.category.childProducts, product)
            .forEach { it.childProducts!!.remove(product) }
        catalogItemFinder.findForeignHolder(QDiscount.discount, QDiscount.discount.products, product)
            .forEach { discount ->
                product.discounts!!.remove(discount)
                discount.products!!.remove(product)
            }
        entityManager.remove(product)
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun modify(@Context securityContext: SecurityContext?, product: Product): Product {
        val original = entityManager.find(Product::class.java, product.id)
        checkNotNull(original)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, original.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        product.childSKUs = product.childSKUsIds?.map { entityManager.find(SKU::class.java, it) }?.toMutableList()
            ?: original.childSKUs
        product.discounts = product.discountsIds?.map { entityManager.find(Discount::class.java, it) }?.toMutableList()
            ?: original.discounts
        product.presentationByLocale = original.presentationByLocale
        return entityManager.merge(product)
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{productId}/discounts")
    fun attachDiscounts(@PathParam("productId") productId: Long?, discountsIds: List<Long>): Product {
        val product = entityManager.find(Product::class.java, productId)
        checkNotNull(product)
        val discounts = discountsIds.map { entityManager.find(Discount::class.java, it) }
        if (product.discounts != null) product.discounts!!.addAll(discounts)
        else product.discounts = discounts.toMutableList()
        return entityManager.merge(product)
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
    ): List<Product> = if (search != null) {
        catalogItemFinder.findBySearchCriteria(QProduct.product, search, start, size, orderBy, isDesc, locale)
    } else catalogItemFinder.findAll(QProduct.product, start, size, orderBy, isDesc, locale)

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(STORE_ADMIN)
    fun findManagedProducts(@Context context: SecurityContext?): List<Product> =
        catalogItemFinder.findByOwner(QProduct.product, context?.userPrincipal?.name ?: "")

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun count(@QueryParam("search") search: String?): Long =
        if (search != null) catalogItemFinder.countBySearchCriteria(QProduct.product, search)
        else catalogItemFinder.countAll(QProduct.product)

    @GET
    @Path("/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    override fun find(
        @Context sessionContext: SecurityContext?,
        @PathParam("productId") @NotNull productId: Long?,
        @QueryParam("locale") locale: String?
    ): Product {
        val product = entityManager.find(Product::class.java, productId)
        checkNotNull(product)
        return if (isAdminUser(sessionContext) || isOwner(sessionContext, product.owner)) product
        else catalogItemFinder.filterVisible(product, locale)
    }

    @GET
    @Path("/{productId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN, ADMIN_READONLY)
    override fun findPresentationsLocales(
        @Context securityContext: SecurityContext?,
        @PathParam("productId") @NotNull catalogId: Long?
    ): Set<String> {
        val product = entityManager.find(Product::class.java, catalogId)
        checkNotNull(product)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, product.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        return product.presentationByLocale!!.keys
    }

    @Path("/{productId}/presentations/{locale}")
    @PermitAll
    override fun findPresentationByLocale(
        @PathParam("productId") @NotNull productId: Long?,
        @NotNull @PathParam("locale") locale: String
    ): PresentationResource<Product> {
        val product = entityManager.find(Product::class.java, productId)
        checkNotNull(product)
        return presentationResource!!.init(Product::class.java, product, locale, product.presentationByLocale!![locale])
    }

    @GET
    @Path("/{productId}/skus")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    fun findChildSKUs(
        @Context sessionContext: SecurityContext?,
        @PathParam("productId") @NotNull productId: Long?,
        @QueryParam("locale") locale: String?
    ): List<SKU> {
        val product = entityManager.find(Product::class.java, productId)
        checkNotNull(product)
        val skus = product.childSKUs!!
        if (skus.isEmpty()) return emptyList()
        return if (isAdminUser(sessionContext) || isOwner(sessionContext, product.owner)) skus
        else catalogItemFinder.findVisibleCatalogItems(QSKU.sKU, skus, locale)
    }

    @GET
    @Path("/{productId}/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN, ADMIN_READONLY)
    fun findDiscounts(@Context sessionContext: SecurityContext?, @PathParam("productId") @NotNull productId: Long?): List<Discount> {
        val product = entityManager.find(Product::class.java, productId)
        checkNotNull(product)
        val discounts = product.discounts!!
        if (discounts.isEmpty()) return emptyList()
        return if (isAdminUser(sessionContext) || isOwner(sessionContext, product.owner)) discounts
        else catalogItemFinder.findVisibleCatalogItems(QDiscount.discount, discounts, null)
    }

    private fun checkNotNull(product: Product?) {
        if (product == null) throw WebApplicationException(Response.Status.NOT_FOUND)
    }
}
