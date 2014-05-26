package org.rembx.jeeshop.catalog;


import com.mysema.query.jpa.impl.JPAQuery;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.rembx.jeeshop.catalog.model.QCategory.category;

/**
 * @author remi
 */

@Path("/catalog")
@Stateless
public class CatalogService implements Serializable {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    public CatalogService(){

    }

    public CatalogService(EntityManager entityManager) {
        this.entityManager = entityManager;
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
            return null;
        }

        return new JPAQuery(entityManager)
                .from(category).where(
                        category.disabled.isFalse(),
                        category.endDate.after(new Date()),
                        category.in(catalog.getRootCategories()))
                .list(category);
    }

    @GET
    @Path("/toto")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * TODO Remove
     */
    public void toto() {

        Date now = Timestamp.from(ZonedDateTime.now().toInstant());
        Date Tomorrow = Timestamp.from(ZonedDateTime.now().plusDays(1).toInstant());
        Date yesterday = Timestamp.from(ZonedDateTime.now().minusDays(1).toInstant());

        Catalog catalog = new Catalog("test");

        Category rootCat1Empty = new Category("rootCat1", "Root category 1 empty", now, Tomorrow, false);
        Category rootCat2 = new Category("rootCat2", "Root category 2 with child categories", now, Tomorrow, false);
        Category rootCat3Expired = new Category("rootCat3", "Root category 3 expired", now, yesterday, false);

        Category childCat1Empty = new Category("childCat1", "Child category 1", now, Tomorrow, false);
        Category childCat2 = new Category("childCat2", "Child category 2 with products", now, Tomorrow, false);
        Category childCat3Expired = new Category("childCat3", "Child category 3 expired", now, yesterday, false);
        Category childCat4Disabled = new Category("childCat4", "Child category 4 disabled", now, Tomorrow, true);


        Product product1 = new Product("product1", now, Tomorrow, false);
        Product product2Expired = new Product("product2", now, yesterday, false);
        Product product3Disabled = new Product("product3", now, yesterday, true);

        catalog.setRootCategories(Arrays.asList(rootCat1Empty, rootCat2, rootCat3Expired));
        rootCat2.setChildCategories(Arrays.asList(childCat1Empty, childCat2, childCat3Expired, childCat4Disabled));
        childCat2.setChildProducts(Arrays.asList(product1, product2Expired, product3Disabled));

        entityManager.persist(catalog);

    }

}
