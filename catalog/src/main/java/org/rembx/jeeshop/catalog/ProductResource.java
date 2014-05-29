package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Product;
import org.rembx.jeeshop.catalog.model.SKU;

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

@Path("/product")
@Stateless
public class ProductResource implements Serializable {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    public ProductResource() {

    }

    public ProductResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }

    @GET
    @Path("/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product find(@PathParam("productId") @NotNull Long productId, @QueryParam("locale") String locale) {
        Product product = entityManager.find(Product.class, productId);
        if (product == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (!product.isVisible()){
            throw new WebApplicationException((Response.Status.FORBIDDEN));
        }

        product.setLocalizedPresentation(locale);

        return product;
    }

    @GET
    @Path("/{productId}/skus")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SKU> findSKUs(@PathParam("productId") @NotNull Long productId, @QueryParam("locale") String locale) {
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
