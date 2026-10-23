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
import org.rembx.jeeshop.catalog.domain.Category
import org.rembx.jeeshop.catalog.domain.Product
import org.rembx.jeeshop.catalog.domain.*
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser
import org.rembx.jeeshop.role.AuthorizationUtils.isOwner
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN

@Path("/categories")
@ApplicationScoped
class Categories(
    @PersistenceUnit(CatalogPersistenceUnit.NAME) private val entityManager: EntityManager,
    private val catalogItemFinder: CatalogItemRepositoryImpl,
    private val presentationResource: PresentationResource<Category>?
) : CatalogItemService<Category> {
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun create(@Context securityContext: SecurityContext?, category: Category): Category {
        attachOwner(securityContext, category)
        category.childCategoriesIds?.let { ids ->
            category.childCategories = ids.map { entityManager.find(Category::class.java, it) }.toMutableList()
        }
        category.childProductsIds?.let { ids ->
            category.childProducts = ids.map { entityManager.find(Product::class.java, it) }.toMutableList()
        }
        entityManager.persist(category)
        return category
    }

    @DELETE
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{categoryId}")
    override fun delete(@Context securityContext: SecurityContext?, @PathParam("categoryId") categoryId: Long?) {
        val category = entityManager.find(Category::class.java, categoryId)
        checkNotNull(category)
        if (!isOwner(securityContext, category.owner) && !isAdminUser(securityContext)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        catalogItemFinder.findForeignHolder(QCategory.category, QCategory.category.childCategories, category)
            .forEach { it.childCategories!!.remove(category) }
        catalogItemFinder.findForeignHolder(QCatalog.catalog, QCatalog.catalog.rootCategories, category)
            .forEach { it.rootCategories!!.remove(category) }
        entityManager.remove(category)
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{categoryId}/products")
    fun attachProducts(@PathParam("categoryId") categoryId: Long?, productsIds: List<Long>): Category {
        val category = entityManager.find(Category::class.java, categoryId)
        checkNotNull(category)
        val products = productsIds.map { entityManager.find(Product::class.java, it) }
        if (category.childProducts != null) category.childProducts!!.addAll(products)
        else category.childProducts = products.toMutableList()
        return entityManager.merge(category)
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{categoryId}/categories")
    fun attachCategories(@PathParam("categoryId") categoryId: Long?, categoriesIds: List<Long>): Category {
        val category = entityManager.find(Category::class.java, categoryId)
        checkNotNull(category)
        val categories = categoriesIds.map { entityManager.find(Category::class.java, it) }
        if (category.childCategories != null) category.childCategories!!.addAll(categories)
        else category.childCategories = categories.toMutableList()
        return entityManager.merge(category)
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun modify(@Context securityContext: SecurityContext?, category: Category): Category {
        val original = entityManager.find(Category::class.java, category.id)
        checkNotNull(original)
        if (!isOwner(securityContext, original.owner) && !isAdminUser(securityContext)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        category.childCategories = category.childCategoriesIds?.map {
            entityManager.find(Category::class.java, it)
        }?.toMutableList() ?: original.childCategories
        category.childProducts = category.childProductsIds?.map {
            entityManager.find(Product::class.java, it)
        }?.toMutableList() ?: original.childProducts
        category.presentationByLocale = original.presentationByLocale
        return entityManager.merge(category)
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
    ): List<Category> = if (search != null) {
        catalogItemFinder.findBySearchCriteria(QCategory.category, search, start, size, orderBy, isDesc, locale)
    } else {
        catalogItemFinder.findAll(QCategory.category, start, size, orderBy, isDesc, locale)
    }

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(STORE_ADMIN)
    fun findManagedCategories(@Context context: SecurityContext?): List<Category> =
        catalogItemFinder.findByOwner(QCategory.category, context?.userPrincipal?.name ?: "")

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun count(@QueryParam("search") search: String?): Long =
        if (search != null) catalogItemFinder.countBySearchCriteria(QCategory.category, search)
        else catalogItemFinder.countAll(QCategory.category)

    @GET
    @Path("/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    override fun find(
        @Context securityContext: SecurityContext?,
        @PathParam("categoryId") @NotNull categoryId: Long?,
        @QueryParam("locale") locale: String?
    ): Category {
        val category = entityManager.find(Category::class.java, categoryId)
        checkNotNull(category)
        return if (!isAdminUser(securityContext) && !isOwner(securityContext, category.owner)) {
            catalogItemFinder.filterVisible(category, locale)
        } else category
    }

    @GET
    @Path("/{categoryId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN, ADMIN_READONLY)
    override fun findPresentationsLocales(
        @Context securityContext: SecurityContext?,
        @PathParam("categoryId") @NotNull catalogId: Long?
    ): Set<String> {
        val category = entityManager.find(Category::class.java, catalogId)
        checkNotNull(category)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, category.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        return category.presentationByLocale!!.keys
    }

    @Path("/{categoryId}/presentations/{locale}")
    @PermitAll
    override fun findPresentationByLocale(
        @PathParam("categoryId") @NotNull productId: Long?,
        @NotNull @PathParam("locale") locale: String
    ): PresentationResource<Category> {
        val category = entityManager.find(Category::class.java, productId)
        checkNotNull(category)
        return presentationResource!!.init(Category::class.java, category, locale, category.presentationByLocale!![locale])
    }

    @GET
    @Path("/{categoryId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    fun findChildCategories(
        @Context securityContext: SecurityContext?,
        @PathParam("categoryId") @NotNull categoryId: Long?,
        @QueryParam("locale") locale: String?
    ): List<Category> {
        val category = entityManager.find(Category::class.java, categoryId)
        checkNotNull(category)
        val children = category.childCategories!!
        if (children.isEmpty()) return emptyList()
        return if (!isAdminUser(securityContext) && !isOwner(securityContext, category.owner)) {
            catalogItemFinder.findVisibleCatalogItems(QCategory.category, children, locale)
        } else children
    }

    @GET
    @Path("/{categoryId}/products")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    fun findChildProducts(
        @Context securityContext: SecurityContext?,
        @PathParam("categoryId") @NotNull categoryId: Long?,
        @QueryParam("locale") locale: String?
    ): List<Product> {
        val category = entityManager.find(Category::class.java, categoryId)
        checkNotNull(category)
        val children = category.childProducts!!
        if (children.isEmpty()) return emptyList()
        return if (!isOwner(securityContext, category.owner) && isAdminUser(securityContext)) {
            catalogItemFinder.findVisibleCatalogItems(QProduct.product, children, locale)
        } else children
    }

    private fun checkNotNull(category: Category?) {
        if (category == null) throw WebApplicationException(Response.Status.NOT_FOUND)
    }
}
