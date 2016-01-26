package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Presentation;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.rembx.jeeshop.catalog.model.QCatalog.catalog;
import static org.rembx.jeeshop.catalog.model.QCategory.category;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;
import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN;
import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY;

/**
 * @author remi
 */

@Path("/catalogs")
@Stateless
public class Catalogs {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject PresentationResource presentationResource;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    @Resource
    private SessionContext sessionContext;

    public Catalogs() {

    }

    public Catalogs(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<Catalog> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size
            ,@QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc) {
        if (search != null)
            return catalogItemFinder.findBySearchCriteria(catalog, search, start, size, orderBy, isDesc);
        else
            return catalogItemFinder.findAll(catalog, start, size, orderBy, isDesc);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count(@QueryParam("search") String search) {
        if (search != null)
            return catalogItemFinder.countBySearchCriteria(catalog, search);
        else
            return catalogItemFinder.countAll(catalog);
    }


    @GET
    @Path("/{catalogId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Catalog find(@PathParam("catalogId") @NotNull Long catalogId, @QueryParam("locale") String locale) {
        Catalog catalog = entityManager.find(Catalog.class, catalogId);

        if (isAdminUser(sessionContext))
            return catalog;
        else
            return catalogItemFinder.filterVisible(catalog, locale);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Catalog create(Catalog catalog) {
        if (catalog.getRootCategoriesIds() != null) {
            List<Category> newCategories = new ArrayList<>();
            catalog.getRootCategoriesIds().forEach(categoryId -> newCategories.add(entityManager.find(Category.class, categoryId)));
            catalog.setRootCategories(newCategories);
        }
        entityManager.persist(catalog);
        return catalog;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Path("/{catalogId}")
    public void delete(@PathParam("catalogId") Long catalogId) {
        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        checkNotNull(catalog);
        entityManager.remove(catalog);

    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Catalog modify(Catalog catalog) {
        Catalog originalCatalog = entityManager.find(Catalog.class, catalog.getId());
        checkNotNull(originalCatalog);

        if (catalog.getRootCategoriesIds() != null) {
            List<Category> newCategories = new ArrayList<>();
            catalog.getRootCategoriesIds().forEach(categoryId -> newCategories.add(entityManager.find(Category.class, categoryId)));
            catalog.setRootCategories(newCategories);
        } else {
            catalog.setRootCategories(originalCatalog.getRootCategories());
        }

        catalog.setPresentationByLocale(originalCatalog.getPresentationByLocale());

        return entityManager.merge(catalog);
    }

    @GET
    @Path("/{catalogId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Set<String> findPresentationsLocales(@PathParam("catalogId") @NotNull Long catalogId) {
        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        checkNotNull(catalog);
        return catalog.getPresentationByLocale().keySet();
    }

    @Path("/{catalogId}/presentations/{locale}")
    @PermitAll
    public PresentationResource findPresentationByLocale(@PathParam("catalogId") @NotNull Long catalogId, @NotNull @PathParam("locale") String locale) {
        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        checkNotNull(catalog);
        Presentation presentation = catalog.getPresentationByLocale().get(locale);
        return presentationResource.init(presentation, locale, catalog);
    }

    @GET
    @Path("/{catalogId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Category> findCategories(@PathParam("catalogId") @NotNull Long catalogId, @QueryParam("locale") String locale) {

        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        checkNotNull(catalog);

        List<Category> rootCategories = catalog.getRootCategories();
        if (rootCategories.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext)) {
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
