package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.*;
import org.rembx.jeeshop.catalog.util.CatalogItemResourceUtil;
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
import java.util.ArrayList;
import java.util.List;

import static org.rembx.jeeshop.catalog.model.QCategory.category;
import static org.rembx.jeeshop.catalog.model.QDiscount.discount;
import static org.rembx.jeeshop.catalog.model.QSKU.sKU;

/**
 * @author remi
 */

@Path("/skus")
@Stateless
public class SKUResource implements Serializable {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private CatalogItemResourceUtil catItemResUtil;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    public SKUResource() {
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<SKU> findAll(@QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        return catalogItemFinder.findAll(sKU, start, size);
    }

    public SKUResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder, CatalogItemResourceUtil catItemResUtil) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.catItemResUtil = catItemResUtil;
    }

    @GET
    @Path("/{skuId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public SKU find(@PathParam("skuId") @NotNull Long skuId, @QueryParam("locale") String locale) {
        SKU sku = entityManager.find(SKU.class, skuId);
        return catItemResUtil.find(sku, locale);
    }

    @GET
    @Path("/{skuId}/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Discount> findDiscounts(@PathParam("skuId") @NotNull Long skuId) {
        SKU sku = entityManager.find(SKU.class, skuId);
        if (sku == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (sku.getDiscounts().isEmpty()) {
            return new ArrayList<>();
        }

        return catalogItemFinder.findVisibleCatalogItems(discount, sku.getDiscounts(), null);
    }

}
