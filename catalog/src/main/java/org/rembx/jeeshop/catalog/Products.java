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

import static org.rembx.jeeshop.catalog.model.QDiscount.discount;
import static org.rembx.jeeshop.catalog.model.QProduct.product;
import static org.rembx.jeeshop.catalog.model.QSKU.sKU;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;
import static org.rembx.jeeshop.role.AuthorizationUtils.isOwner;
import static org.rembx.jeeshop.role.JeeshopRoles.*;

@Path("/products")
@ApplicationScoped
public class Products implements CatalogItemService<Product> {

    private EntityManager entityManager;
    private CatalogItemFinder catalogItemFinder;
    private PresentationResource<Product> presentationResource;

    Products(@PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager, CatalogItemFinder catalogItemFinder, PresentationResource<Product> presentationResource) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.presentationResource = presentationResource;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public Product create(@Context SecurityContext securityContext, Product product) {

        attachOwner(securityContext, product);

        if (product.getChildSKUsIds() != null) {
            List<SKU> newSkus = new ArrayList<>();
            product.getChildSKUsIds().forEach(skuId -> newSkus.add(entityManager.find(SKU.class, skuId)));
            product.setChildSKUs(newSkus);
        }
        if (product.getDiscountsIds() != null) {
            List<Discount> newDiscounts = new ArrayList<>();
            product.getDiscountsIds().forEach(discountId -> newDiscounts.add(entityManager.find(Discount.class, discountId)));
            product.setDiscounts(newDiscounts);
        }
        entityManager.persist(product);
        return product;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed({ADMIN, STORE_ADMIN})
    @Path("/{productId}")
    public void delete(@Context SecurityContext securityContext, @PathParam("productId") Long productId) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);

        if (isAdminUser(securityContext) || isOwner(securityContext, product.getOwner())) {
            List<Category> categoryHolders = catalogItemFinder.findForeignHolder(QCategory.category, QCategory.category.childProducts, product);
            for (Category category : categoryHolders) {
                category.getChildProducts().remove(product);
            }

            List<Discount> discountHolders = catalogItemFinder.findForeignHolder(QDiscount.discount, QDiscount.discount.products, product);
            for (Discount discount : discountHolders) {
                product.getDiscounts().remove(discount);
                discount.getProducts().remove(product);
            }

            entityManager.remove(product);

        } else {

            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public Product modify(@Context SecurityContext securityContext, Product product) {
        Product originalProduct = entityManager.find(Product.class, product.getId());
        checkNotNull(originalProduct);

        if (isAdminUser(securityContext) || isOwner(securityContext, originalProduct.getOwner())) {

            if (product.getChildSKUsIds() != null) {
                List<SKU> newSkus = new ArrayList<>();
                product.getChildSKUsIds().forEach(skuId -> newSkus.add(entityManager.find(SKU.class, skuId)));
                product.setChildSKUs(newSkus);
            } else {
                product.setChildSKUs(originalProduct.getChildSKUs());
            }

            if (product.getDiscountsIds() != null) {
                List<Discount> newDiscounts = new ArrayList<>();
                product.getDiscountsIds().forEach(discountId -> newDiscounts.add(entityManager.find(Discount.class, discountId)));
                product.setDiscounts(newDiscounts);
            } else {
                product.setDiscounts(originalProduct.getDiscounts());
            }

            product.setPresentationByLocale(originalProduct.getPresentationByLocale());

            return entityManager.merge(product);

        } else {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    @Path("/{productId}/discounts")
    public Product attachDiscounts(@PathParam("productId") Long productId, List<Long> discountsIds) {

        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);

        List<Discount> newDiscounts = new ArrayList<>();
        discountsIds.forEach(discountId -> newDiscounts.add(entityManager.find(Discount.class, discountId)));

        if (product.getDiscounts() != null) {
            product.getDiscounts().addAll(newDiscounts);
        } else {
            product.setDiscounts(newDiscounts);
        }

        return entityManager.merge(product);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<Product> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size
            , @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc, @QueryParam("locale") String locale) {
        if (search != null)
            return catalogItemFinder.findBySearchCriteria(product, search, start, size, orderBy, isDesc, locale);
        else
            return catalogItemFinder.findAll(product, start, size, orderBy, isDesc, locale);
    }

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({STORE_ADMIN})
    public List<Product> findManagedProducts(@Context SecurityContext context) {
        return catalogItemFinder.findByOwner(product, context.getUserPrincipal().getName());
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count(@QueryParam("search") String search) {
        if (search != null)
            return catalogItemFinder.countBySearchCriteria(product, search);
        else
            return catalogItemFinder.countAll(product);
    }

    @GET
    @Path("/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Product find(@Context SecurityContext sessionContext, @PathParam("productId") @NotNull Long productId, @QueryParam("locale") String locale) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);
        if (isAdminUser(sessionContext) || isOwner(sessionContext, product.getOwner()))
            return product;
        else
            return catalogItemFinder.filterVisible(product, locale);
    }

    @GET
    @Path("/{productId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN, ADMIN_READONLY})
    public Set<String> findPresentationsLocales(@Context SecurityContext securityContext, @PathParam("productId") @NotNull Long productId) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);

        if (!isAdminUser(securityContext) && !isOwner(securityContext, product.getOwner()))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        return product.getPresentationByLocale().keySet();
    }

    @Path("/{productId}/presentations/{locale}")
    @PermitAll
    public PresentationResource<Product> findPresentationByLocale(@PathParam("productId") @NotNull Long productId, @NotNull @PathParam("locale") String locale) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);
        Presentation presentation = product.getPresentationByLocale().get(locale);
        return presentationResource.init(Product.class, product, locale, presentation);
    }

    @GET
    @Path("/{productId}/skus")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<SKU> findChildSKUs(@Context SecurityContext sessionContext, @PathParam("productId") @NotNull Long productId, @QueryParam("locale") String locale) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);
        List<SKU> childSKUs = product.getChildSKUs();
        if (childSKUs.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext) || isOwner(sessionContext, product.getOwner()))
            return childSKUs;
        else
            return catalogItemFinder.findVisibleCatalogItems(sKU, childSKUs, locale);
    }

    @GET
    @Path("/{productId}/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN, ADMIN_READONLY})
    public List<Discount> findDiscounts(@Context SecurityContext sessionContext, @PathParam("productId") @NotNull Long productId) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);
        List<Discount> discounts = product.getDiscounts();
        if (discounts.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext) || isOwner(sessionContext, product.getOwner()))
            return discounts;
        else
            return catalogItemFinder.findVisibleCatalogItems(discount, discounts, null);
    }


    private void checkNotNull(Product product) {
        if (product == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
