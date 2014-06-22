package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.util.CatalogItemResourceUtil;
import org.rembx.jeeshop.role.JeeshopRoles;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.rembx.jeeshop.catalog.model.QCatalog.catalog;
import static org.rembx.jeeshop.catalog.model.QCategory.category;

/**
 * @author remi
 */

@Path("/catalogs")
@Stateless
public class CatalogResource implements Serializable {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    @Inject
    private CatalogItemResourceUtil catItemResUtil;

    @Resource
    private SessionContext sessionContext;

    public CatalogResource(){

    }

    public CatalogResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder, CatalogItemResourceUtil catalogItemResourceUtil) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.catItemResUtil = catalogItemResourceUtil;
    }


    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<Catalog> findAll(@QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        return catalogItemFinder.findAll(catalog, start, size);
    }

    @GET
    @Path("/{catalogId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({JeeshopRoles.ADMIN, JeeshopRoles.USER})
    public Catalog find(@PathParam("catalogId") @NotNull Long catalogId, @QueryParam("locale") String locale) {
        Catalog catalog = entityManager.find(Catalog.class, catalogId);

        return catItemResUtil.find(catalog, locale);
    }

    @GET
    @Path("/{catalogId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll()
    public List<Category> findCategories(@PathParam("catalogId") @NotNull Long catalogId, @QueryParam("locale") String locale) {

        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        if (catalog == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        List<Category> rootCategories = catalog.getRootCategories();
        if (rootCategories.isEmpty()){
            return new ArrayList<>();
        }

        if (sessionContext != null && sessionContext.getCallerPrincipal().getName().equals(JeeshopRoles.ADMIN)){
            return rootCategories;
        }else{
            return catalogItemFinder.findVisibleCatalogItems(category, rootCategories, locale);
        }

    }

}
