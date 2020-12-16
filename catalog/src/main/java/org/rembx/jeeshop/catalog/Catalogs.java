package org.rembx.jeeshop.catalog;

import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Presentation;
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
import static org.rembx.jeeshop.catalog.model.QCategory.category;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;
import static org.rembx.jeeshop.role.AuthorizationUtils.isOwner;
import static org.rembx.jeeshop.role.JeeshopRoles.*;

@Path("/rs/catalogs")
@ApplicationScoped
public class Catalogs implements CatalogItemService<Catalog> {

    private final EntityManager entityManager;
    private final CatalogItemFinder catalogItemFinder;
    private final PresentationResource presentationResource;

    Catalogs(@PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager, CatalogItemFinder catalogItemFinder, PresentationResource presentationResource) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.presentationResource = presentationResource;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<Catalog> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start,
                                 @QueryParam("size") Integer size, @QueryParam("orderBy") String orderBy,
                                 @QueryParam("isDesc") Boolean isDesc, @QueryParam("locale") String locale) {

        return search != null
                ? catalogItemFinder.findBySearchCriteria(catalog, search, start, size, orderBy, isDesc, locale)
                : catalogItemFinder.findAll(catalog, start, size, orderBy, isDesc, locale);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count(@QueryParam("search") String search) {
        return search != null
                ? catalogItemFinder.countBySearchCriteria(catalog, search)
                : catalogItemFinder.countAll(catalog);
    }

    @GET
    @Path("/{catalogId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Catalog find(@Context SecurityContext securityContext, @PathParam("catalogId") @NotNull Long catalogId, @QueryParam("locale") String locale) {
        Catalog catalog = entityManager.find(Catalog.class, catalogId);

        if (isAdminUser(securityContext) || isOwner(securityContext, catalog.getOwner()))
            return catalog;
        else
            return catalogItemFinder.filterVisible(catalog, locale);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public Catalog create(@Context SecurityContext securityContext, Catalog catalog) {

        attachOwner(securityContext, catalog);

        if (catalog.getRootCategoriesIds() != null) {
            List<Category> newCategories = new ArrayList<>();
            catalog.getRootCategoriesIds().forEach(categoryId -> newCategories.add(entityManager.find(Category.class, categoryId)));
            catalog.setRootCategories(newCategories);
        }

        entityManager.persist(catalog);
        return catalog;
    }

    @DELETE
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    @Path("/{catalogId}")
    public void delete(@Context SecurityContext securityContext, @PathParam("catalogId") Long catalogId) {
        Catalog loadedCatalog = entityManager.find(Catalog.class, catalogId);
        checkNotNull(loadedCatalog);

        if (isAdminUser(securityContext) || isOwner(securityContext, loadedCatalog.getOwner()))
            entityManager.remove(loadedCatalog);
        else
            throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public Catalog modify(@Context SecurityContext securityContext, Catalog catalogToModify) {

        Catalog originalCatalog = entityManager.find(Catalog.class, catalogToModify.getId());
        checkNotNull(originalCatalog);

        if (!isAdminUser(securityContext) && !isOwner(securityContext, originalCatalog.getOwner()))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        if (catalogToModify.getRootCategoriesIds() != null) {
            List<Category> newCategories = new ArrayList<>();
            catalogToModify.getRootCategoriesIds().forEach(categoryId -> newCategories.add(entityManager.find(Category.class, categoryId)));
            catalogToModify.setRootCategories(newCategories);
        } else {
            catalogToModify.setRootCategories(originalCatalog.getRootCategories());
        }

        catalogToModify.setPresentationByLocale(originalCatalog.getPresentationByLocale());

        return entityManager.merge(catalogToModify);
    }

    @GET
    @Path("/{catalogId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN, ADMIN_READONLY})
    public Set<String> findPresentationsLocales(@Context SecurityContext securityContext, @PathParam("catalogId") @NotNull Long catalogId) {
        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        checkNotNull(catalog);

        if (!isAdminUser(securityContext) && !isOwner(securityContext, catalog.getOwner()))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        return catalog.getPresentationByLocale().keySet();
    }

    @Path("/{catalogId}/presentations/{locale}")
    @PermitAll
    public PresentationResource findPresentationByLocale(@PathParam("catalogId") @NotNull Long catalogId, @NotNull @PathParam("locale") String locale) {
        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        checkNotNull(catalog);
        Presentation presentation = catalog.getPresentationByLocale().get(locale);
        return presentationResource.init(catalog, locale, presentation);
    }

    @GET
    @Path("/{catalogId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Category> findCategories(@Context SecurityContext securityContext,
                                         @PathParam("catalogId") @NotNull Long catalogId,
                                         @QueryParam("locale") String locale) {

        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        checkNotNull(catalog);

        List<Category> rootCategories = catalog.getRootCategories();
        if (rootCategories.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(securityContext) || isOwner(securityContext, catalog.getOwner())) {
            return rootCategories;
        } else {
            return catalogItemFinder.findVisibleCatalogItems(category, rootCategories, locale);
        }

    }

    private void checkNotNull(Catalog originalCatalog) {
        if (originalCatalog == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
