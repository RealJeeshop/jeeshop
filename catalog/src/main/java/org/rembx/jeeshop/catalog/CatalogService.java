package org.rembx.jeeshop.catalog;


import com.mysema.query.jpa.impl.JPAQuery;
import org.rembx.jeeshop.catalog.model.Catalog;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
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

        return new JPAQuery(entityManager)
                .from(category).where(
                        category.disabled.isFalse(),
                        category.endDate.after(new Date()),
                        category.in(catalog.getRootCategories()))
                .list(category);
    }

}
