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
import org.rembx.jeeshop.catalog.domain.Store
import org.rembx.jeeshop.catalog.domain.*
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser
import org.rembx.jeeshop.role.AuthorizationUtils.isOwner
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN

@Path("/stores")
@ApplicationScoped
class Stores(
    @PersistenceUnit(CatalogPersistenceUnit.NAME) private val entityManager: EntityManager,
    private val catalogItemFinder: CatalogItemRepositoryImpl,
    private val presentationResource: PresentationResource<Store>?
) : CatalogItemService<Store> {
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
    ): List<Store> = if (search != null) {
        catalogItemFinder.findBySearchCriteria(QStore.store, search, start, size, orderBy, isDesc, locale)
    } else catalogItemFinder.findAll(QStore.store, start, size, orderBy, isDesc, locale)

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(STORE_ADMIN)
    fun findManagedStores(@Context context: SecurityContext?): List<Store> =
        catalogItemFinder.findByOwner(QStore.store, context?.userPrincipal?.name ?: "")

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun count(@QueryParam("search") search: String?): Long =
        if (search != null) catalogItemFinder.countBySearchCriteria(QStore.store, search)
        else catalogItemFinder.countAll(QStore.store)

    @GET
    @Path("/{storeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    override fun find(
        @Context securityContext: SecurityContext?,
        @PathParam("storeId") @NotNull productId: Long?,
        @QueryParam("locale") locale: String?
    ): Store {
        val store = entityManager.find(Store::class.java, productId)
        store.premisses
        return if (isAdminUser(securityContext) || isOwner(securityContext, store.owner)) store
        else catalogItemFinder.filterVisible(store, locale)
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun create(@Context securityContext: SecurityContext?, store: Store): Store {
        attachOwner(securityContext, store)
        entityManager.persist(store)
        return store
    }

    @DELETE
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{storeId}")
    override fun delete(@Context securityContext: SecurityContext?, @PathParam("storeId") storeId: Long?) {
        val store = entityManager.find(Store::class.java, storeId)
        checkNotNull(store)
        if (isOwner(securityContext, store.owner) || isAdminUser(securityContext)) entityManager.remove(store)
        else throw WebApplicationException(Response.Status.FORBIDDEN)
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed(ADMIN, STORE_ADMIN)
    override fun modify(@Context securityContext: SecurityContext?, store: Store): Store {
        val original = entityManager.find(Store::class.java, store.id)
        checkNotNull(original)
        if (!isOwner(securityContext, original.owner) && !isAdminUser(securityContext)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        store.catalogs = store.catalogsIds?.map { entityManager.find(Catalog::class.java, it) }?.toMutableList()
            ?: original.catalogs
        store.presentationByLocale = original.presentationByLocale
        return entityManager.merge(store)
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{storeId}/categories")
    fun attachCatalogs(@PathParam("storeId") storeId: Long?, catalogsIds: List<Long>): Store {
        val store = entityManager.find(Store::class.java, storeId)
        checkNotNull(store)
        val catalogs = catalogsIds.map { entityManager.find(Catalog::class.java, it) }
        if (store.catalogs != null) store.catalogs!!.addAll(catalogs)
        else store.catalogs = catalogs.toMutableList()
        return entityManager.merge(store)
    }

    @GET
    @Path("/{storeId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN, ADMIN_READONLY)
    override fun findPresentationsLocales(
        @Context securityContext: SecurityContext?,
        @PathParam("storeId") @NotNull catalogId: Long?
    ): Set<String> {
        val store = entityManager.find(Store::class.java, catalogId)
        checkNotNull(store)
        if (!isAdminUser(securityContext) && !isOwner(securityContext, store.owner)) {
            throw WebApplicationException(Response.Status.FORBIDDEN)
        }
        return store.presentationByLocale!!.keys
    }

    @Path("/{storeId}/presentations/{locale}")
    @PermitAll
    override fun findPresentationByLocale(
        @NotNull @PathParam("storeId") productId: Long?,
        @NotNull @PathParam("locale") locale: String
    ): PresentationResource<Store> {
        val store = entityManager.find(Store::class.java, productId)
        checkNotNull(store)
        return presentationResource!!.init(Store::class.java, store, locale, store.presentationByLocale!![locale])
    }

    @GET
    @Path("/{storeId}/catalogs")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    fun findCatalogs(
        @Context securityContext: SecurityContext?,
        @PathParam("storeId") @NotNull storeId: Long?,
        @QueryParam("locale") locale: String?
    ): List<Catalog> {
        val store = entityManager.find(Store::class.java, storeId)
        checkNotNull(store)
        val catalogs = store.catalogs!!
        if (catalogs.isEmpty()) return emptyList()
        return if (isAdminUser(securityContext) || isOwner(securityContext, store.owner)) catalogs
        else catalogItemFinder.findVisibleCatalogItems(QCatalog.catalog, catalogs, locale)
    }

    private fun checkNotNull(store: Store?) {
        if (store == null) throw WebApplicationException(Response.Status.NOT_FOUND)
    }
}
