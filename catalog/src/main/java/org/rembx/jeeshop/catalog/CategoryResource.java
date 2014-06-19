package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.util.CatalogItemResourceUtil;

import javax.annotation.security.PermitAll;
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

import static org.rembx.jeeshop.catalog.model.QCategory.category;
import static org.rembx.jeeshop.catalog.model.QProduct.product;

/**
 * @author remi
 */

@Path("/categories")
@Stateless
public class CategoryResource implements Serializable {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    @Inject
    private CatalogItemResourceUtil catItemResUtil;

    public CategoryResource() {
    }

    public CategoryResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder, CatalogItemResourceUtil catItemResUtil) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.catItemResUtil = catItemResUtil;
    }

    @GET
    @Path("/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Category find(@PathParam("categoryId") @NotNull Long categoryId, @QueryParam("locale") String locale) {
        Category category = entityManager.find(Category.class, categoryId);
        return catItemResUtil.find(category,locale);
    }

    @GET
    @Path("/{categoryId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Category> findChildCategories(@PathParam("categoryId") @NotNull Long categoryId, @QueryParam("locale") String locale) {
        Category cat = entityManager.find(Category.class, categoryId);
        if (cat == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (cat.getChildCategories().isEmpty()){
            return new ArrayList<>();
        }
        return catalogItemFinder.findVisibleCatalogItems(category, cat.getChildCategories(), locale);
    }

    @GET
    @Path("/{categoryId}/products")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Product> findChildProducts(@PathParam("categoryId") @NotNull Long categoryId, @QueryParam("locale") String locale) {
        Category cat = entityManager.find(Category.class, categoryId);
        if (cat == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (cat.getChildProducts().isEmpty()){
            return new ArrayList<>();
        }

        return catalogItemFinder.findVisibleCatalogItems(product, cat.getChildProducts(), locale);
    }

}
