package org.rembx.jeeshop.catalog;


import org.apache.commons.collections.CollectionUtils;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Product;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.rembx.jeeshop.catalog.model.QProduct.product;
import static org.rembx.jeeshop.catalog.model.QSKU.sKU;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;

/**
 * @author remi
 */

@Path("/products")
@Stateless
public class ProductResource implements Serializable {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    @Resource
    private SessionContext sessionContext;

    public ProductResource() {

    }

    public ProductResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Product modify(Product product){
        Product originalProduct = entityManager.find(Product.class, product.getId());
        if (originalProduct == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (CollectionUtils.isEmpty(product.getChildSKUs())){
            product.setChildSKUs(originalProduct.getChildSKUs());
        }

        if (CollectionUtils.isEmpty(product.getDiscounts())){
            product.setDiscounts(originalProduct.getDiscounts());
        }

        return  entityManager.merge(product);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<Product> findAll(@QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        return catalogItemFinder.findAll(product, start, size);
    }

    @GET
    @Path("/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Product find(@PathParam("productId") @NotNull Long productId, @QueryParam("locale") String locale) {
        Product product = entityManager.find(Product.class, productId);
        if (isAdminUser(sessionContext))
            return product;
        else
            return catalogItemFinder.filterVisible(product, locale);
    }

    @GET
    @Path("/{productId}/skus")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<SKU> findChildSKUs(@PathParam("productId") @NotNull Long productId, @QueryParam("locale") String locale) {
        Product product = entityManager.find(Product.class, productId);
        if (product == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        List<SKU> childSKUs = product.getChildSKUs();
        if (childSKUs.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext))
            return childSKUs;
        else
            return catalogItemFinder.findVisibleCatalogItems(sKU, childSKUs, locale);
    }

}
