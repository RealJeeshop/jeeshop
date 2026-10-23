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
import org.rembx.jeeshop.catalog.domain.Catalog
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.catalog.domain.Category
import org.rembx.jeeshop.catalog.domain.*
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser
import org.rembx.jeeshop.role.AuthorizationUtils.isOwner
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN

@Path("/catalogs")
@ApplicationScoped
class Catalogs(
    @PersistenceUnit(CatalogPersistenceUnit.NAME) private val entityManager: EntityManager,
    private val catalogItemFinder: CatalogItemRepositoryImpl,
    private val presentationResource: PresentationResource<Catalog>?
) : CatalogItemService<Catalog> {
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
    ): List<Catalog> = if (search != null) {
        catalogItemFinder.findBySearchCriteria(QCatalog.catalog, search, start, size, orderBy, isDesc, locale)
    } else {
        catalogItemFinder.findAll(QCatalog.catalog, start, size, orderBy, isDesc, locale)
    }

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(STORE_ADMIN)
    fun findManagedCatalogs(@Context context: SecurityContext?): List<Catalog> =
        catalogItemFinder.findByOwner(QCatalog.catalog, context?.userPrincipal?.name ?: "")

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun count(@QueryParam("search") search: String?): Long =
        if (search != null) catalogItemFinder.countBySearchCriteria(QCatalog.catalog, search)
        else catalogItemFinder.countAll(QCatalog.catalog)

    @GET
    @Path("/{catalogId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    override fun find(
        @Context securityContext: SecurityContext?,
        @PathParam("catalogId") @NotNull catalogId: Long?,
        @QueryParam("locale") locale: String?
    ): Catalog {
        val catalog = entityManager.find(Catalog::class.java, catalogId)
        return if (isAdminUser(securityContext) || isOwner(securityContext, catalog.owner)) catalog
        else catalogItemFinder.filterVisible(catalog, locale)
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun create(@Context securityContext: SecurityContext?, catalog: Catalog): Catalog {
        attachOwner(securityContext, catalog)
        catalog.rootCategoriesIds?.let { ids ->
            catalog.rootCategories = ids.map { entityManager.find(Category::class.java, it) }.toMutableList()
        }
        entityManager.persist(catalog)
        return catalog
    }

    @DELETE
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{catalogId}")
    override fun delete(@Context securityContext: SecurityContext?, @PathParam("catalogId") catalogId: Long?) {
        val catalog = entityManager.find(Catalog::class.java, catalogId)
        checkNotNull(catalog)
        if (isAdminUser(securityContext) || isOwner(securityContext, catalog.owner)) entityManager.remove(catalog)
        else throw WebApplicationException(Response.Status.FORBIDDEN)
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun modify(@Context securityContext: SecurityContext?, catalogToModify: Catalog): Catalog {
        val original = entityManager.find(Catalog::class.java, catalogToModify.id)
        checkNotNull(original)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, original.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        catalogToModify.rootCategories = catalogToModify.rootCategoriesIds?.map {
            entityManager.find(Category::class.java, it)
        }?.toMutableList() ?: original.rootCategories
        catalogToModify.presentationByLocale = original.presentationByLocale
        return entityManager.merge(catalogToModify)
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{catalogId}/categories")
    fun attachCategories(@PathParam("catalogId") catalogId: Long?, categoriesIds: List<Long>): Catalog {
        val catalog = entityManager.find(Catalog::class.java, catalogId)
        checkNotNull(catalog)
        val categories = categoriesIds.map { entityManager.find(Category::class.java, it) }
        if (catalog.rootCategories != null) catalog.rootCategories!!.addAll(categories)
        else catalog.rootCategories = categories.toMutableList()
        return entityManager.merge(catalog)
    }

    @GET
    @Path("/{catalogId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN, ADMIN_READONLY)
    override fun findPresentationsLocales(
        @Context securityContext: SecurityContext?,
        @PathParam("catalogId") @NotNull catalogId: Long?
    ): Set<String> {
        val catalog = entityManager.find(Catalog::class.java, catalogId)
        checkNotNull(catalog)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, catalog.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        return catalog.presentationByLocale!!.keys
    }

    @Path("/{catalogId}/presentations/{locale}")
    @PermitAll
    override fun findPresentationByLocale(
        @PathParam("catalogId") @NotNull catalogId: Long?,
        @NotNull @PathParam("locale") locale: String
    ): PresentationResource<Catalog> {
        val catalog = entityManager.find(Catalog::class.java, catalogId)
        checkNotNull(catalog)
        return presentationResource!!.init(Catalog::class.java, catalog, locale, catalog.presentationByLocale!![locale])
    }

    @GET
    @Path("/{catalogId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    fun findCategories(
        @Context securityContext: SecurityContext?,
        @PathParam("catalogId") @NotNull catalogId: Long?,
        @QueryParam("locale") locale: String?
    ): List<Category> {
        val catalog = entityManager.find(Catalog::class.java, catalogId)
        checkNotNull(catalog)
        val rootCategories = catalog.rootCategories!!
        if (rootCategories.isEmpty()) return emptyList()
        return if (isAdminUser(securityContext) || isOwner(securityContext, catalog.owner)) rootCategories
        else catalogItemFinder.findVisibleCatalogItems(QCategory.category, rootCategories, locale)
    }

    private fun checkNotNull(catalog: Catalog?) {
        if (catalog == null) throw WebApplicationException(Response.Status.NOT_FOUND)
    }
}
