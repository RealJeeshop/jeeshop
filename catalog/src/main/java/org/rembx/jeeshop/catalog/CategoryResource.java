package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;
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

import static org.rembx.jeeshop.catalog.model.QCategory.category;
import static org.rembx.jeeshop.catalog.model.QProduct.product;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;

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

    @Resource
    private SessionContext sessionContext;

    public CategoryResource() {
    }

    public CategoryResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<Category> findAll(@QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        return catalogItemFinder.findAll(category, start, size);
    }

    @GET
    @Path("/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Category find(@PathParam("categoryId") @NotNull Long categoryId, @QueryParam("locale") String locale) {
        Category category = entityManager.find(Category.class, categoryId);
        if (isAdminUser(sessionContext))
            return category;
        else
            return catalogItemFinder.filterVisible(category, locale);
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
        List<Category> childCategories = cat.getChildCategories();
        if (childCategories.isEmpty()){
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext))
            return childCategories;
        else
            return catalogItemFinder.findVisibleCatalogItems(category, childCategories, locale);
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

        List<Product> childProducts = cat.getChildProducts();

        if (childProducts.isEmpty()){
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext))
            return childProducts;
        else
            return catalogItemFinder.findVisibleCatalogItems(product, childProducts, locale);
    }

}
