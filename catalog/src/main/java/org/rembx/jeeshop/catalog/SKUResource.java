package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Discount;
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

import static org.rembx.jeeshop.catalog.model.QDiscount.discount;
import static org.rembx.jeeshop.catalog.model.QSKU.sKU;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;

/**
 * @author remi
 */

@Path("/skus")
@Stateless
public class SKUResource {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    @Resource
    private SessionContext sessionContext;

    public SKUResource() {
    }

    public SKUResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public SKU create(SKU sku){
        if (sku.getDiscountsIds() != null){
            List<Discount> newDiscounts = new ArrayList<>();
            sku.getDiscountsIds().forEach(discountId-> newDiscounts.add(entityManager.find(Discount.class, discountId)));
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
    public void delete(@PathParam("skuId") Long skuId){
        SKU catalogPersisted = entityManager.find(SKU.class,skuId);
        if (catalogPersisted != null){
            entityManager.remove(catalogPersisted);
        }else{
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public SKU modify(SKU sku){
        SKU originalSKU = entityManager.find(SKU.class, sku.getId());
        if (originalSKU == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (sku.getDiscountsIds() != null){
            List<Discount> newDiscounts = new ArrayList<>();
            sku.getDiscountsIds().forEach(discountId-> newDiscounts.add(entityManager.find(Discount.class, discountId)));
            sku.setDiscounts(newDiscounts);
        }else{
            sku.setDiscounts(originalSKU.getDiscounts());
        }

        return  entityManager.merge(sku);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<SKU> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        if (search!=null)
            return catalogItemFinder.findByNameOrId(sKU, search, start, size);
        else
            return catalogItemFinder.findAll(sKU, start, size);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Long count() {
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
    @Path("/{skuId}/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Discount> findDiscounts(@PathParam("skuId") @NotNull Long skuId) {
        SKU sku = entityManager.find(SKU.class, skuId);
        if (sku == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        List<Discount> discounts = sku.getDiscounts();
        if (discounts.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext))
            return discounts;
        else
            return catalogItemFinder.findVisibleCatalogItems(discount, discounts, null);
    }

}
