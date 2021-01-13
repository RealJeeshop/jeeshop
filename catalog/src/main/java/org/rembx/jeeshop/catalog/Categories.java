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

import static org.rembx.jeeshop.catalog.model.QCategory.category;
import static org.rembx.jeeshop.catalog.model.QProduct.product;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;
import static org.rembx.jeeshop.role.AuthorizationUtils.isOwner;
import static org.rembx.jeeshop.role.JeeshopRoles.*;

/**
 * @author remi
 */

@Path("/rs/categories")
@ApplicationScoped
public class Categories implements CatalogItemService<Category> {

    private final EntityManager entityManager;
    private final CatalogItemFinder catalogItemFinder;
    private final PresentationResource presentationResource;

    Categories(@PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager, CatalogItemFinder catalogItemFinder, PresentationResource presentationResource) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.presentationResource = presentationResource;
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public Category create(@Context SecurityContext securityContext, Category category) {

        attachOwner(securityContext, category);

        if (category.getChildCategories() != null) {
            List<Category> newCategories = new ArrayList<>();
            category.getChildCategoriesIds().forEach(categoryId -> newCategories.add(entityManager.find(Category.class, categoryId)));
            category.setChildCategories(newCategories);
        }
        if (category.getChildProductsIds() != null) {
            List<Product> newProducts = new ArrayList<>();
            category.getChildProductsIds().forEach(productId -> newProducts.add(entityManager.find(Product.class, productId)));
            category.setChildProducts(newProducts);
        }

        entityManager.persist(category);
        return category;
    }

    @DELETE
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    @Path("/{categoryId}")
    public void delete(@Context SecurityContext securityContext, @PathParam("categoryId") Long categoryId) {
        Category category = entityManager.find(Category.class, categoryId);
        checkNotNull(category);

        if (!isOwner(securityContext, category.getOwner()) && !isAdminUser(securityContext))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        else {

            List<Category> categoryHolders = catalogItemFinder.findForeignHolder(QCategory.category, QCategory.category.childCategories, category);
            for (Category categoryHolder : categoryHolders) {
                categoryHolder.getChildCategories().remove(category);
            }

            List<Catalog> catalogHolders = catalogItemFinder.findForeignHolder(QCatalog.catalog, QCatalog.catalog.rootCategories, category);
            for (Catalog catalogHolder : catalogHolders) {
                catalogHolder.getRootCategories().remove(category);
            }

            entityManager.remove(category);
        }
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public Category modify(@Context SecurityContext securityContext, Category category) {
        Category originalCategory = entityManager.find(Category.class, category.getId());
        checkNotNull(originalCategory);

        if (isOwner(securityContext, originalCategory.getOwner()) || isAdminUser(securityContext)) {

            if (category.getChildCategoriesIds() != null) {
                List<Category> newCategories = new ArrayList<>();
                category.getChildCategoriesIds().forEach(categoryId -> newCategories.add(entityManager.find(Category.class, categoryId)));
                category.setChildCategories(newCategories);
            } else {
                category.setChildCategories(originalCategory.getChildCategories());
            }

            if (category.getChildProductsIds() != null) {
                List<Product> newProducts = new ArrayList<>();
                category.getChildProductsIds().forEach(productId -> newProducts.add(entityManager.find(Product.class, productId)));
                category.setChildProducts(newProducts);
            } else {
                category.setChildProducts(originalCategory.getChildProducts());
            }

            category.setPresentationByLocale(originalCategory.getPresentationByLocale());

            return entityManager.merge(category);

        } else {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<Category> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size
            , @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc, @QueryParam("locale") String locale) {
        if (search != null)
            return catalogItemFinder.findBySearchCriteria(category, search, start, size, orderBy, isDesc, locale);
        else
            return catalogItemFinder.findAll(category, start, size, orderBy, isDesc, locale);
    }


    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count(@QueryParam("search") String search) {
        if (search != null)
            return catalogItemFinder.countBySearchCriteria(category, search);
        else
            return catalogItemFinder.countAll(category);
    }

    @GET
    @Path("/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Category find(@Context SecurityContext securityContext, @PathParam("categoryId") @NotNull Long categoryId, @QueryParam("locale") String locale) {

        Category category = entityManager.find(Category.class, categoryId);
        checkNotNull(category);

        return !isAdminUser(securityContext) && !isOwner(securityContext, category.getOwner())
                ? catalogItemFinder.filterVisible(category, locale)
                : category;
    }

    @GET
    @Path("/{categoryId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN, ADMIN_READONLY})
    public Set<String> findPresentationsLocales(@Context SecurityContext securityContext, @PathParam("categoryId") @NotNull Long categoryId) {
        Category category = entityManager.find(Category.class, categoryId);
        checkNotNull(category);

        if (!isAdminUser(securityContext) && !isOwner(securityContext, category.getOwner()))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        return category.getPresentationByLocale().keySet();
    }

    @Path("/{categoryId}/presentations/{locale}")
    @PermitAll
    public PresentationResource findPresentationByLocale(@PathParam("categoryId") @NotNull Long categoryId, @NotNull @PathParam("locale") String locale) {
        Category category = entityManager.find(Category.class, categoryId);
        checkNotNull(category);
        Presentation presentation = category.getPresentationByLocale().get(locale);
        return presentationResource.init(category, locale, presentation);
    }

    @GET
    @Path("/{categoryId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Category> findChildCategories(@Context SecurityContext securityContext, @PathParam("categoryId") @NotNull Long categoryId, @QueryParam("locale") String locale) {
        Category cat = entityManager.find(Category.class, categoryId);
        checkNotNull(cat);
        List<Category> childCategories = cat.getChildCategories();
        if (childCategories.isEmpty()) {
            return new ArrayList<>();
        }

        return !isAdminUser(securityContext) && !isOwner(securityContext, cat.getOwner())
                ? catalogItemFinder.findVisibleCatalogItems(category, childCategories, locale)
                : childCategories;
    }


    @GET
    @Path("/{categoryId}/products")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Product> findChildProducts(@Context SecurityContext securityContext, @PathParam("categoryId") @NotNull Long categoryId, @QueryParam("locale") String locale) {

        Category cat = entityManager.find(Category.class, categoryId);
        checkNotNull(cat);

        List<Product> childProducts = cat.getChildProducts();
        if (childProducts.isEmpty()) {
            return new ArrayList<>();
        }

        return !isOwner(securityContext, cat.getOwner()) && isAdminUser(securityContext)
                ? catalogItemFinder.findVisibleCatalogItems(product, childProducts, locale)
                : childProducts;
    }

    private void checkNotNull(Category cat) {
        if (cat == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
