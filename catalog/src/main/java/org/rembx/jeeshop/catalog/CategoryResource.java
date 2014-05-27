package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;

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

@Path("/category")
@Stateless
public class CategoryResource implements Serializable {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    public CategoryResource() {
    }

    public CategoryResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }

    @GET
    @Path("/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Category find(@PathParam("categoryId") @NotNull Long categoryId) {
        Category category = entityManager.find(Category.class, categoryId);
        if (category == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (!category.isVisible()){
            throw new WebApplicationException((Response.Status.FORBIDDEN));
        }

        return category;
    }

    @GET
    @Path("/{categoryId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Category> findCategories(@PathParam("categoryId") @NotNull Long categoryId) {
        Category cat = entityManager.find(Category.class, categoryId);
        if (cat == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (cat.getChildCategories().isEmpty()){
            return new ArrayList<>();
        }
        return catalogItemFinder.findVisibleCatalogItems(category, cat.getChildCategories());
    }

    @GET
    @Path("/{categoryId}/products")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> findProducts(@PathParam("categoryId") @NotNull Long categoryId) {
        Category cat = entityManager.find(Category.class, categoryId);
        if (cat == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (cat.getChildProducts().isEmpty()){
            return new ArrayList<>();
        }

        return catalogItemFinder.findVisibleCatalogItems(product, cat.getChildProducts());
    }

}
