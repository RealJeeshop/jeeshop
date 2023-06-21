package org.rembx.jeeshop.catalog;

import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Presentation;
import org.rembx.jeeshop.catalog.model.Store;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.rembx.jeeshop.catalog.model.QCatalog.catalog;
import static org.rembx.jeeshop.catalog.model.QStore.store;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;
import static org.rembx.jeeshop.role.AuthorizationUtils.isOwner;
import static org.rembx.jeeshop.role.JeeshopRoles.*;

@Path("/stores")
@ApplicationScoped
public class Stores implements CatalogItemService<Store> {

    private final EntityManager entityManager;
    private final CatalogItemFinder catalogItemFinder;
    private final PresentationResource<Store> presentationResource;

    Stores(@PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager, CatalogItemFinder catalogItemFinder, PresentationResource<Store> presentationResource) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.presentationResource = presentationResource;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<Store> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start,
                               @QueryParam("size") Integer size, @QueryParam("orderBy") String orderBy,
                               @QueryParam("isDesc") Boolean isDesc, @QueryParam("locale") String locale) {

        if (search != null)
            return catalogItemFinder.findBySearchCriteria(store, search, start, size, orderBy, isDesc, locale);
        else {
            return catalogItemFinder.findAll(store, start, size, orderBy, isDesc, locale);
        }
    }

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({STORE_ADMIN})
    public List<Store> findManagedStores(@Context SecurityContext context) {
        return catalogItemFinder.findByOwner(store, context.getUserPrincipal().getName());
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count(@QueryParam("search") String search) {
        if (search != null)
            return catalogItemFinder.countBySearchCriteria(store, search);
        else
            return catalogItemFinder.countAll(store);
    }

    @GET
    @Path("/{storeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Store find(@Context SecurityContext securityContext, @PathParam("storeId") @NotNull Long itemId, @QueryParam("locale") String locale) {
        Store store = entityManager.find(Store.class, itemId);
        store.getPremisses();

        if (isAdminUser(securityContext) || isOwner(securityContext, store.getOwner()))
            return store;
        else
            return catalogItemFinder.filterVisible(store, locale);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public Store create(@Context SecurityContext securityContext, Store store) {
        attachOwner(securityContext, store);
        entityManager.persist(store);
        return store;
    }

    @DELETE
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    @Path("/{storeId}")
    public void delete(@Context SecurityContext securityContext, @PathParam("storeId") Long storeId) {
        Store store = entityManager.find(Store.class, storeId);
        checkNotNull(store);

        if (isOwner(securityContext, store.getOwner()) || isAdminUser(securityContext)) {
            entityManager.remove(store);
        } else {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public Store modify(@Context SecurityContext securityContext, Store store) {

        Store originalCatalog = entityManager.find(Store.class, store.getId());
        checkNotNull(originalCatalog);

        if (!isOwner(securityContext, originalCatalog.getOwner()) && !isAdminUser(securityContext))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        if (store.getCatalogsIds() != null) {
            List<Catalog> catalogs = new ArrayList<>();
            store.getCatalogsIds().forEach(categoryId -> catalogs.add(entityManager.find(Catalog.class, categoryId)));
            store.setCatalogs(catalogs);
        } else {
            store.setCatalogs(originalCatalog.getCatalogs());
        }

        store.setPresentationByLocale(originalCatalog.getPresentationByLocale());
        return entityManager.merge(store);
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    @Path("/{storeId}/categories")
    public Store attachCatalogs(@PathParam("storeId") Long storeId, List<Long> catalogsIds) {

        Store store = entityManager.find(Store.class, storeId);
        checkNotNull(store);

        List<Catalog> newCatalogs = new ArrayList<>();
        catalogsIds.forEach(catalogId -> newCatalogs.add(entityManager.find(Catalog.class, catalogId)));

        if (store.getCatalogs() != null) {
            store.getCatalogs().addAll(newCatalogs);
        } else {
            store.setCatalogs(newCatalogs);
        }

        return entityManager.merge(store);
    }

    @GET
    @Path("/{storeId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN, ADMIN_READONLY})
    public Set<String> findPresentationsLocales(@Context SecurityContext securityContext, @PathParam("storeId") @NotNull Long storeId) {
        Store loadedStore = entityManager.find(Store.class, storeId);
        checkNotNull(loadedStore);

        if (!isAdminUser(securityContext) && !isOwner(securityContext, loadedStore.getOwner()))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        return loadedStore.getPresentationByLocale().keySet();
    }

    @Path("/{storeId}/presentations/{locale}")
    @PermitAll
    public PresentationResource<Store> findPresentationByLocale(@NotNull @PathParam("storeId") Long storeId, @NotNull @PathParam("locale") String locale) {
        Store store = entityManager.find(Store.class, storeId);
        checkNotNull(store);
        Presentation presentation = store.getPresentationByLocale().get(locale);
        return presentationResource.init(Store.class, store, locale, presentation);
    }

    @GET
    @Path("/{storeId}/catalogs")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Catalog> findCatalogs(@Context SecurityContext securityContext,
                                      @PathParam("storeId") @NotNull Long storeId,
                                      @QueryParam("locale") String locale) {

        Store loadedStore = entityManager.find(Store.class, storeId);
        checkNotNull(loadedStore);

        List<Catalog> catalogs = loadedStore.getCatalogs();
        if (catalogs.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(securityContext) || isOwner(securityContext, loadedStore.getOwner())) {
            return catalogs;
        } else {
            return catalogItemFinder.findVisibleCatalogItems(catalog, catalogs, locale);
        }

    }

    private void checkNotNull(Store originalCatalog) {
        if (originalCatalog == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
