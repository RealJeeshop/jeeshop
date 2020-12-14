package org.rembx.jeeshop.catalog;

import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.catalog.model.*;
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

import static org.rembx.jeeshop.catalog.OwnerUtils.attachOwner;
import static org.rembx.jeeshop.catalog.model.QCatalog.catalog;
import static org.rembx.jeeshop.catalog.model.QStore.store;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;
import static org.rembx.jeeshop.role.AuthorizationUtils.isOwner;
import static org.rembx.jeeshop.role.JeeshopRoles.*;

@Path("/rs/stores")
@ApplicationScoped
public class Stores implements CatalogItems<Store> {

    private EntityManager entityManager;
    private CatalogItemFinder catalogItemFinder;
    private PresentationResource presentationResource;

    Stores(@PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager, CatalogItemFinder catalogItemFinder, PresentationResource presentationResource) {
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

//        if (store.getRootCategoriesIds() != null) {
//            List<Category> newCategories = new ArrayList<>();
//            store.getRootCategoriesIds().forEach(categoryId -> newCategories.add(entityManager.find(Category.class, categoryId)));
//            store.setRootCategories(newCategories);
//        } else {
//            store.setCatalogs(originalCatalog.getCatalogs());
//        }

        if (isOwner(securityContext, store.getOwner()) || isAdminUser(securityContext)) {
            store.setPresentationByLocale(originalCatalog.getPresentationByLocale());
            return entityManager.merge(store);
        } else {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }

    @GET
    @Path("/{storeId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Set<String> findPresentationsLocales(@PathParam("storeId") @NotNull Long storeId) {
        Store loadedStore = entityManager.find(Store.class, storeId);
        checkNotNull(loadedStore);
        return loadedStore.getPresentationByLocale().keySet();
    }

    @Path("/{storeId}/presentations/{locale}")
    @PermitAll
    public PresentationResource findPresentationByLocale(@PathParam("storedId") @NotNull Long storeId, @NotNull @PathParam("locale") String locale) {
        Store store = entityManager.find(Store.class, storeId);
        checkNotNull(store);
        Presentation presentation = store.getPresentationByLocale().get(locale);
        return presentationResource.init(store, locale, presentation);
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
