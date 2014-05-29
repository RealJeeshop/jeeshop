package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.*;

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
    public List<Category> findCategories(@PathParam("catalogId") @NotNull Long catalogId, @QueryParam("locale") String locale) {

        Catalog catalog = entityManager.find(Catalog.class, catalogId);
        if (catalog == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (catalog.getRootCategories().isEmpty()){
            return new ArrayList<>();
        }

        return catalogItemFinder.findVisibleCatalogItems(category, catalog.getRootCategories(), locale);

    }


    //------------------------------------------------------------------------------------------------------------------
    //--------------------------------------TESTS TO BE REMOVED---------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    /**
     * TODO Remove - TEMPORARY FAKE CATALOG FOR MANUAL TESTS
     */

    @GET
    @Path("/toto")
    @Produces(MediaType.APPLICATION_JSON)
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
        Category childCat5WithPresentation = new Category("childCat5", "Child category 5 with presentation", now, tomorrow, false);
        Presentation presentationUKChildCat5 = new Presentation("en_GB","Chocolat cakes", TEST_TEXT_2000_TO_REMOVE, TEST_TEXT_2000_TO_REMOVE);
        Presentation presentationUSChildCat5 = new Presentation("en_US","Chocolat cakes", TEST_TEXT_2000_TO_REMOVE, TEST_TEXT_2000_TO_REMOVE);
        childCat5WithPresentation.setPresentationByLocale(new HashMap<String, Presentation>(){{
            put (Locale.UK.toString(), presentationUKChildCat5);
            put (Locale.US.toString(), presentationUSChildCat5);
        }});

        Product product1 = new Product("product1", now, tomorrow, false);
        Product product2Expired = new Product("product2", now, yesterday, false);
        Product product3Disabled = new Product("product3", now, yesterday, true);

        SKU sku1 = new SKU("sku1", "Sku1 enabled", 10d,100, "X1213JJLB-1", now, tomorrow, false, 3);
        SKU sku2 = new SKU("sku2", "Sku2 disabled", 10d,100, "X1213JJLB-2", now, tomorrow, true, 3);
        SKU sku3 = new SKU("sku3", "Sku3 expired", 10d,100, "X1213JJLB-3",  now, yesterday, false, 3);
        SKU sku4 = new SKU("sku4", "Sku4 not available", 10d,2, "X1213JJLB-3",  now, yesterday, false, 3);

        catalog.setRootCategories(Arrays.asList(rootCat1Empty, rootCat2, rootCat3Expired));
        rootCat2.setChildCategories(Arrays.asList(childCat1Empty, childCat2, childCat3Expired, childCat4Disabled, childCat5WithPresentation));
        childCat2.setChildProducts(Arrays.asList(product1, product2Expired, product3Disabled));
        product1.setChildSKUs(Arrays.asList(sku1,sku2,sku3,sku4));
        entityManager.persist(catalog);

    }

    private final static String TEST_TEXT_2000_TO_REMOVE = "orem ipsum dolor sit amet, consectetur adipiscing elit. Mauris ultricies convallis lacinia. Nunc fringilla, odio ac volutpat lobortis, ligula nisi suscipit eros, quis porttitor neque mi id purus. Proin vitae nibh semper, fermentum sapien et, interdum urna. Morbi dictum dignissim est, vitae hendrerit dolor adipiscing sed. Phasellus ac est mi. Aenean adipiscing turpis rutrum elit ultricies, eget scelerisque nisl scelerisque. Cras quis urna vulputate est interdum congue. Pellentesque vel sollicitudin mi, sed fringilla odio. Duis quis volutpat orci. Curabitur nec vestibulum nunc. Nunc imperdiet lacus orci, ac vestibulum diam bibendum quis. Praesent convallis tempus ante, in suscipit urna facilisis at. Sed congue pretium feugiat. Nunc faucibus consectetur accumsan.Phasellus malesuada augue eget magna condimentum, ut rhoncus nisl ullamcorper. Pellentesque vitae erat facilisis, ullamcorper urna ac, eleifend massa. Phasellus elit eros, volutpat nec pellentesque et, ultrices nec erat. Cras iaculis tincidunt nulla auctor luctus. Pellentesque turpis leo, auctor sed sem eget, luctus malesuada erat. Donec sit amet posuere magna. Duis iaculis, arcu sit amet pharetra condimentum, quam dolor condimentum massa, convallis venenatis diam lorem eu sem.Nullam ornare dolor id neque pretium, quis dignissim lorem posuere. Quisque iaculis augue libero, in sagittis velit dignissim in. Nullam adipiscing, odio ut facilisis malesuada, nibh justo tincidunt ipsum, eget eleifend diam lacus ut nibh. Sed feugiat est eu elit sagittis, id sollicitudin sem faucibus. Sed sit amet libero et massa luctus ullamcorper. Sed non purus adipiscing, malesuada leo vel, tempus felis. Nulla ut ipsum laoreet diam eleifend gravida at eget nulla. Nullam ultrices convallis nisi at cursus. Nullam lacinia tincidunt eleifend. Interdum et malesuada fames ac ante ipsum primis in faucibus. Maecenas vel tincidunt metus. Nullam nibh orci, tristique sed mollis vitae, euismod ultricies augue. Aliquam tincidunt elit a massa ....";


}
