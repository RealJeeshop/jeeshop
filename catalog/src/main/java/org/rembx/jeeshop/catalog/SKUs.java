package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Discount;
import org.rembx.jeeshop.catalog.model.Presentation;
import org.rembx.jeeshop.catalog.model.SKU;
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

import static org.rembx.jeeshop.catalog.model.QDiscount.discount;
import static org.rembx.jeeshop.catalog.model.QSKU.sKU;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;

/**
 * @author remi
 */

@Path("/skus")
@Stateless
public class SKUs {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject PresentationResource presentationResource;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    @Resource
    private SessionContext sessionContext;

    public SKUs() {
    }

    public SKUs(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public SKU create(SKU sku) {
        if (sku.getDiscountsIds() != null) {
            List<Discount> newDiscounts = new ArrayList<>();
            sku.getDiscountsIds().forEach(discountId -> newDiscounts.add(entityManager.find(Discount.class, discountId)));
            sku.setDiscounts(newDiscounts);
        }
        entityManager.persist(sku);
        return sku;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{skuId}")
    public void delete(@PathParam("skuId") Long skuId) {
        SKU sku = entityManager.find(SKU.class, skuId);
        checkNotNull(sku);
        entityManager.remove(sku);

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public SKU modify(SKU sku) {
        SKU originalSKU = entityManager.find(SKU.class, sku.getId());
        checkNotNull(originalSKU);

        if (sku.getDiscountsIds() != null) {
            List<Discount> newDiscounts = new ArrayList<>();
            sku.getDiscountsIds().forEach(discountId -> newDiscounts.add(entityManager.find(Discount.class, discountId)));
            sku.setDiscounts(newDiscounts);
        } else {
            sku.setDiscounts(originalSKU.getDiscounts());
        }

        return entityManager.merge(sku);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<SKU> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        if (search != null)
            return catalogItemFinder.findBySearchCriteria(sKU, search, start, size);
        else
            return catalogItemFinder.findAll(sKU, start, size);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
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
    public SKU find(@PathParam("skuId") @NotNull Long skuId, @QueryParam("locale") String locale) {
        SKU sku = entityManager.find(SKU.class, skuId);
        if (isAdminUser(sessionContext))
            return sku;
        else
            return catalogItemFinder.filterVisible(sku, locale);
    }

    @GET
    @Path("/{skuId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Set<String> findPresentationsLocales(@PathParam("skuId") @NotNull Long skuId) {
        SKU sku = entityManager.find(SKU.class, skuId);
        checkNotNull(sku);
        return sku.getPresentationByLocale().keySet();
    }

    @Path("/{skuId}/presentations/{locale}")
    @RolesAllowed(JeeshopRoles.ADMIN)
    public PresentationResource findPresentationByLocale(@PathParam("skuId") @NotNull Long skuId, @NotNull @PathParam("locale") String locale) {
        SKU sku = entityManager.find(SKU.class, skuId);
        checkNotNull(sku);
        Presentation presentation = sku.getPresentationByLocale().get(locale);
        return presentationResource.init(presentation, locale, sku);
    }

    @GET
    @Path("/{skuId}/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Discount> findDiscounts(@PathParam("skuId") @NotNull Long skuId) {
        SKU sku = entityManager.find(SKU.class, skuId);
        checkNotNull(sku);
        List<Discount> discounts = sku.getDiscounts();
        if (discounts.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext))
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
