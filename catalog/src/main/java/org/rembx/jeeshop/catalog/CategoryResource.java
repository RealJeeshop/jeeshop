package org.rembx.jeeshop.catalog;


import com.mysema.query.jpa.impl.JPAQuery;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Category;
import org.rembx.jeeshop.catalog.model.Product;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.rembx.jeeshop.catalog.model.QCategory.category;
import static org.rembx.jeeshop.catalog.model.QProduct.product;
import static org.rembx.jeeshop.util.DateUtil.dateToLocalDateTime;

/**
 * @author remi
 */

@Path("/category")
@Stateless
public class CategoryResource implements Serializable {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    public CategoryResource() {
    }

    public CategoryResource(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @GET
    @Path("/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Category find(@PathParam("categoryId") @NotNull Long categoryId) {
        Category category = entityManager.find(Category.class, categoryId);
        if (category == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (category.isDisabled() || dateToLocalDateTime(category.getEndDate()).isBefore(LocalDateTime.now()) ){
            throw new WebApplicationException((Response.Status.FORBIDDEN));
        }
        return category;
    }

    @GET
    @Path("/categories/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Category> findCategories(@PathParam("categoryId") @NotNull Long categoryId) {
        Category cat = entityManager.find(Category.class, categoryId);
        if (cat == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (cat.getChildCategories().isEmpty()){
            return null;
        }
        return new JPAQuery(entityManager)
                .from(category).where(
                        category.disabled.isFalse(),
                        category.endDate.after(new Date()),
                        category.in(cat.getChildCategories()))
                .list(category);
    }

    @GET
    @Path("/products/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> findProducts(@PathParam("categoryId") @NotNull Long categoryId) {
        Category cat = entityManager.find(Category.class, categoryId);
        if (cat == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if (cat.getChildProducts().isEmpty()){
            return null;
        }

        return new JPAQuery(entityManager)
                .from(product).where(
                        product.disabled.isFalse(),
                        product.endDate.after(new Date()),
                        product.in(cat.getChildProducts()))
                .list(product);
    }

}
