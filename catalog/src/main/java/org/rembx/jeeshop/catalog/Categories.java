package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.rembx.jeeshop.catalog.model.QCategory.category;
import static org.rembx.jeeshop.catalog.model.QProduct.product;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;

/**
 * @author remi
 */

@Path("/categories")
@Stateless
public class Categories {

    @Inject
    PresentationResource presentationResource;

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    @Resource
    private SessionContext sessionContext;

    public Categories() {
    }

    public Categories(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Category create(Category category) {
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{categoryId}")
    public void delete(@PathParam("categoryId") Long categoryId) {
        Category category = entityManager.find(Category.class, categoryId);
        checkNotNull(category);

        List<Category> categoryHolders = catalogItemFinder.findForeignHolder(QCategory.category, QCategory.category.childCategories, category);
        for (Category categoryHolder : categoryHolders){
            categoryHolder.getChildCategories().remove(category);
        }

        List<Catalog> catalogHolders = catalogItemFinder.findForeignHolder(QCatalog.catalog, QCatalog.catalog.rootCategories, category);
        for (Catalog catalogHolder : catalogHolders){
            catalogHolder.getRootCategories().remove(category);
        }

        entityManager.remove(category);

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Category modify(Category category) {
        Category originalCategory = entityManager.find(Category.class, category.getId());
        checkNotNull(originalCategory);

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

        return entityManager.merge(category);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<Category> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        if (search != null)
            return catalogItemFinder.findBySearchCriteria(category, search, start, size);
        else
            return catalogItemFinder.findAll(category, start, size);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
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
    public Category find(@PathParam("categoryId") @NotNull Long categoryId, @QueryParam("locale") String locale) {
        Category category = entityManager.find(Category.class, categoryId);
        if (isAdminUser(sessionContext))
            return category;
        else
            return catalogItemFinder.filterVisible(category, locale);
    }

    @GET
    @Path("/{categoryId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Set<String> findPresentationsLocales(@PathParam("categoryId") @NotNull Long categoryId) {
        Category category = entityManager.find(Category.class, categoryId);
        checkNotNull(category);
        return category.getPresentationByLocale().keySet();
    }

    @Path("/{categoryId}/presentations/{locale}")
    @RolesAllowed(JeeshopRoles.ADMIN)
    public PresentationResource findPresentationByLocale(@PathParam("categoryId") @NotNull Long categoryId, @NotNull @PathParam("locale") String locale) {
        Category category = entityManager.find(Category.class, categoryId);
        checkNotNull(category);
        Presentation presentation = category.getPresentationByLocale().get(locale);
        return presentationResource.init(presentation, locale, category);
    }

    @GET
    @Path("/{categoryId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Category> findChildCategories(@PathParam("categoryId") @NotNull Long categoryId, @QueryParam("locale") String locale) {
        Category cat = entityManager.find(Category.class, categoryId);
        checkNotNull(cat);
        List<Category> childCategories = cat.getChildCategories();
        if (childCategories.isEmpty()) {
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
        checkNotNull(cat);

        List<Product> childProducts = cat.getChildProducts();

        if (childProducts.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext))
            return childProducts;
        else
            return catalogItemFinder.findVisibleCatalogItems(product, childProducts, locale);
    }

    private void checkNotNull(Category cat) {
        if (cat == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
