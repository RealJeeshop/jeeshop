package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.catalog.util.CatalogItemResourceUtil;

import javax.annotation.security.PermitAll;
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

import static org.rembx.jeeshop.catalog.model.QSKU.sKU;

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

    @Inject
    private CatalogItemResourceUtil catItemResUtil;

    public ProductResource() {

    }

    public ProductResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder, CatalogItemResourceUtil catItemResUtil) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.catItemResUtil = catItemResUtil;
    }

    @GET
    @Path("/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Product find(@PathParam("productId") @NotNull Long productId, @QueryParam("locale") String locale) {
        Product product = entityManager.find(Product.class, productId);
        return catItemResUtil.find(product,locale);
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
        if (product.getChildSKUs().isEmpty()) {
            return new ArrayList<>();
        }

        return catalogItemFinder.findVisibleCatalogItems(sKU, product.getChildSKUs(), locale);
    }

}
