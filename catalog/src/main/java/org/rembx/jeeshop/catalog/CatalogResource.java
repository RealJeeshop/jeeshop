package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.rembx.jeeshop.catalog.model.QCategory.category;

/**
 * @author remi
 */

@Path("/catalog")
@Stateless
public class CatalogResource implements Serializable {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    public CatalogResource(){

    }

    public CatalogResource(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }

    @GET
    @Path("/{catalogId}/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Category> findCategories(@PathParam("catalogId") Long catalogId) {

        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        if (catalog == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (catalog.getRootCategories().isEmpty()){
            return new ArrayList<>();
        }

        return catalogItemFinder.findVisibleCatalogItems(category, catalog.getRootCategories());

    }

    @GET
    @Path("/toto")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * TODO Remove
     */
    public void toto() {

        Date now = Timestamp.from(ZonedDateTime.now().toInstant());
        Date tomorrow = Timestamp.from(ZonedDateTime.now().plusDays(1).toInstant());
        Date yesterday = Timestamp.from(ZonedDateTime.now().minusDays(1).toInstant());

        Catalog catalog = new Catalog("test");

        Category rootCat1Empty = new Category("rootCat1", "Root category 1 empty", now, tomorrow, false);
        Category rootCat2 = new Category("rootCat2", "Root category 2 with child categories", now, tomorrow, false);
        Category rootCat3Expired = new Category("rootCat3", "Root category 3 expired", now, yesterday, false);


        Category childCat1Empty = new Category("childCat1", "Child category 1", now, tomorrow, false);
        Category childCat2 = new Category("childCat2", "Child category 2 with products", now, tomorrow, false);
        Category childCat3Expired = new Category("childCat3", "Child category 3 expired", now, yesterday, false);
        Category childCat4Disabled = new Category("childCat4", "Child category 4 disabled", now, tomorrow, true);

        Product product1 = new Product("product1", now, tomorrow, false);
        Product product2Expired = new Product("product2", now, yesterday, false);
        Product product3Disabled = new Product("product3", now, yesterday, true);

        SKU sku1 = new SKU("sku1", "Sku1 enabled", 10d,100, "X1213JJLB-1", now, tomorrow, false, 3);
        SKU sku2 = new SKU("sku2", "Sku2 disabled", 10d,100, "X1213JJLB-2", now, tomorrow, true, 3);
        SKU sku3 = new SKU("sku3", "Sku3 expired", 10d,100, "X1213JJLB-3",  now, yesterday, false, 3);
        SKU sku4 = new SKU("sku4", "Sku4 not available", 10d,2, "X1213JJLB-3",  now, yesterday, false, 3);

        catalog.setRootCategories(Arrays.asList(rootCat1Empty, rootCat2, rootCat3Expired));
        rootCat2.setChildCategories(Arrays.asList(childCat1Empty, childCat2, childCat3Expired, childCat4Disabled));
        childCat2.setChildProducts(Arrays.asList(product1, product2Expired, product3Disabled));
        product1.setChildSKUs(Arrays.asList(sku1,sku2,sku3,sku4));
        entityManager.persist(catalog);

    }

}
