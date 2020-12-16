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

import static org.rembx.jeeshop.catalog.OwnerUtils.attachOwner;
import static org.rembx.jeeshop.catalog.model.QDiscount.discount;
import static org.rembx.jeeshop.catalog.model.QSKU.sKU;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;
import static org.rembx.jeeshop.role.AuthorizationUtils.isOwner;
import static org.rembx.jeeshop.role.JeeshopRoles.*;

/**
 * @author remi
 */

@Path("/rs/skus")
@ApplicationScoped
public class SKUs implements CatalogItems<SKU> {

    private final EntityManager entityManager;
    private final CatalogItemFinder catalogItemFinder;
    private final PresentationResource presentationResource;

    SKUs(@PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager, CatalogItemFinder catalogItemFinder, PresentationResource presentationResource) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.presentationResource = presentationResource;
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public SKU create(@Context SecurityContext securityContext, SKU sku) {

        attachOwner(securityContext, sku);

        if (sku.getDiscountsIds() != null) {
            List<Discount> newDiscounts = new ArrayList<>();
            sku.getDiscountsIds().forEach(discountId -> newDiscounts.add(entityManager.find(Discount.class, discountId)));
            sku.setDiscounts(newDiscounts);
        }
        entityManager.persist(sku);
        return sku;
    }

    @DELETE
    @Transactional
    @Path("/{skuId}")
    @RolesAllowed({ADMIN, STORE_ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(@Context SecurityContext securityContext, @PathParam("skuId") Long skuId) {
        SKU sku = entityManager.find(SKU.class, skuId);
        checkNotNull(sku);

        if (isAdminUser(securityContext) || isOwner(securityContext, sku.getOwner())) {

            List<Product> productHolders = catalogItemFinder.findForeignHolder(QProduct.product, QProduct.product.childSKUs, sku);
            for (Product product : productHolders) {
                product.getChildSKUs().remove(sku);
            }

            List<Discount> discountHolders = catalogItemFinder.findForeignHolder(QDiscount.discount, QDiscount.discount.skus, sku);
            for (Discount discount : discountHolders) {
                sku.getDiscounts().remove(discount);
                discount.getSkus().remove(sku);
            }

            entityManager.remove(sku);

        } else throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public SKU modify(@Context SecurityContext securityContext, SKU sku) {

        SKU originalSKU = entityManager.find(SKU.class, sku.getId());
        checkNotNull(originalSKU);

        if (isAdminUser(securityContext) || isOwner(securityContext, originalSKU.getOwner())) {

            if (sku.getDiscountsIds() != null) {
                List<Discount> newDiscounts = new ArrayList<>();
                sku.getDiscountsIds().forEach(discountId -> newDiscounts.add(entityManager.find(Discount.class, discountId)));
                sku.setDiscounts(newDiscounts);
            } else {
                sku.setDiscounts(originalSKU.getDiscounts());
            }

            sku.setPresentationByLocale(originalSKU.getPresentationByLocale());

            return entityManager.merge(sku);

        } else {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<SKU> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size
            , @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc, @QueryParam("locale") String locale) {
        if (search != null)
            return catalogItemFinder.findBySearchCriteria(sKU, search, start, size, orderBy, isDesc, locale);
        else
            return catalogItemFinder.findAll(sKU, start, size, orderBy, isDesc, locale);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count(@QueryParam("search") String search) {
        if (search != null)
            return catalogItemFinder.countBySearchCriteria(sKU, search);
        else
            return catalogItemFinder.countAll(sKU);
    }

    @GET
    @Path("/{skuId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public SKU find(@Context SecurityContext securityContext, @PathParam("skuId") @NotNull Long skuId, @QueryParam("locale") String locale) {

        SKU sku = entityManager.find(SKU.class, skuId);
        checkNotNull(sku);

        if (isAdminUser(securityContext) || isOwner(securityContext, sku.getOwner()))
            return sku;
        else
            return catalogItemFinder.filterVisible(sku, locale);
    }

    @GET
    @Path("/{skuId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN, ADMIN_READONLY})
    public Set<String> findPresentationsLocales(@Context SecurityContext securityContext, @PathParam("skuId") @NotNull Long skuId) {
        SKU sku = entityManager.find(SKU.class, skuId);
        checkNotNull(sku);

        if (!isAdminUser(securityContext) && !isOwner(securityContext, sku.getOwner()))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        return sku.getPresentationByLocale().keySet();
    }

    @Path("/{skuId}/presentations/{locale}")
    @PermitAll
    public PresentationResource findPresentationByLocale(@PathParam("skuId") @NotNull Long skuId, @NotNull @PathParam("locale") String locale) {
        SKU sku = entityManager.find(SKU.class, skuId);
        checkNotNull(sku);
        Presentation presentation = sku.getPresentationByLocale().get(locale);
        return presentationResource.init(sku, locale, presentation);
    }

    @GET
    @Path("/{skuId}/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN, ADMIN_READONLY})
    public List<Discount> findDiscounts(@Context SecurityContext securityContext, @PathParam("skuId") @NotNull Long skuId) {

        SKU sku = entityManager.find(SKU.class, skuId);
        checkNotNull(sku);

        List<Discount> discounts = sku.getDiscounts();
        if (discounts.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(securityContext) || isOwner(securityContext, sku.getOwner()))
            return discounts;
        else
            return catalogItemFinder.findVisibleCatalogItems(discount, discounts, null);
    }

    private void checkNotNull(SKU sku) {
        if (sku == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
