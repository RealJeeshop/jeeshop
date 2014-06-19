package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.*;
import org.rembx.jeeshop.role.JeeshopRoles;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.*;

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

    public CatalogResource(){

    }

    public CatalogResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }


    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<Catalog> findAll(@QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        return catalogItemFinder.findAll(catalog, start, size);
    }

    @GET
    @Path("/{catalogId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Category> findCategories(@PathParam("catalogId") @NotNull Long catalogId, @QueryParam("locale") String locale) {

        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        if (catalog == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (catalog.getRootCategories().isEmpty()){
            return new ArrayList<>();
        }

        return catalogItemFinder.findVisibleCatalogItems(category, catalog.getRootCategories(), locale);

    }

}
