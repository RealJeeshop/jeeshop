package org.rembx.jeeshop.catalog;


import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.catalog.model.*;
import org.rembx.jeeshop.catalog.model.Discount.ApplicableTo;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Set;

import static org.rembx.jeeshop.catalog.OwnerUtils.attachOwner;
import static org.rembx.jeeshop.catalog.model.QDiscount.discount;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;
import static org.rembx.jeeshop.role.AuthorizationUtils.isOwner;
import static org.rembx.jeeshop.role.JeeshopRoles.*;

/**
 * @author remi
 */

@Path("/rs/discounts")
@ApplicationScoped
public class Discounts implements CatalogItemService<Discount> {

    private final EntityManager entityManager;
    private final CatalogItemFinder catalogItemFinder;
    private final PresentationResource presentationResource;
    private DiscountFinder discountFinder;

    Discounts(@PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager, CatalogItemFinder catalogItemFinder, PresentationResource presentationResource, DiscountFinder discountFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.presentationResource = presentationResource;
        this.discountFinder = discountFinder;
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public Discount create(@Context SecurityContext securityContext, Discount discount) {
        attachOwner(securityContext, discount);
        entityManager.persist(discount);
        return discount;
    }

    @DELETE
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    @Path("/{discountId}")
    public void delete(@Context SecurityContext securityContext, @PathParam("discountId") Long discountId) {
        Discount discount = entityManager.find(Discount.class, discountId);
        checkNotNull(discount);

        if (isAdminUser(securityContext) || isOwner(securityContext, discount.getOwner())) {

            List<Product> productHolders = catalogItemFinder.findForeignHolder(QProduct.product, QProduct.product.discounts, discount);
            for (Product product : productHolders) {
                product.getDiscounts().remove(discount);
            }

            List<SKU> skuHolders = catalogItemFinder.findForeignHolder(QSKU.sKU, QSKU.sKU.discounts, discount);
            for (SKU sku : skuHolders) {
                sku.getDiscounts().remove(discount);
            }

            entityManager.remove(discount);

        } else throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN})
    public Discount modify(@Context SecurityContext securityContext, Discount discount) {
        Discount originalDiscount = entityManager.find(Discount.class, discount.getId());
        checkNotNull(originalDiscount);

        if (isAdminUser(securityContext) || isOwner(securityContext, discount.getOwner())) {

            discount.setPresentationByLocale(originalDiscount.getPresentationByLocale());
            return entityManager.merge(discount);

        } else throw new WebApplicationException(Response.Status.FORBIDDEN);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<Discount> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size
            , @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc, @QueryParam("locale") String locale) {
        if (search != null)
            return catalogItemFinder.findBySearchCriteria(discount, search, start, size, orderBy, isDesc, locale);
        else
            return catalogItemFinder.findAll(discount, start, size, orderBy, isDesc, locale);
    }


    @GET
    @Path("/visible")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<Discount> findVisible(@NotNull @QueryParam("applicableTo") ApplicableTo applicableTo, @QueryParam("locale") String locale) {
        return discountFinder.findVisibleDiscounts(applicableTo, locale);
    }


    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count(@QueryParam("search") String search) {
        if (search != null)
            return catalogItemFinder.countBySearchCriteria(discount, search);
        else
            return catalogItemFinder.countAll(discount);
    }

    @GET
    @Path("/{discountId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN, ADMIN_READONLY})
    public Discount find(@Context SecurityContext securityContext, @PathParam("discountId") @NotNull Long discountId, @QueryParam("locale") String locale) {
        Discount discount = entityManager.find(Discount.class, discountId);
        checkNotNull(discount);

        if (isAdminUser(securityContext) || isOwner(securityContext, discount.getOwner()))
            return discount;
        else
            return catalogItemFinder.filterVisible(discount, locale);
    }

    @GET
    @Path("/{discountId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN, ADMIN_READONLY})
    public Set<String> findPresentationsLocales(@Context SecurityContext securityContext, @PathParam("discountId") @NotNull Long discountId) {

        Discount discount = entityManager.find(Discount.class, discountId);
        checkNotNull(discount);

        if (!isAdminUser(securityContext) && !isOwner(securityContext, discount.getOwner()))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        return discount.getPresentationByLocale().keySet();
    }

    @Path("/{discountId}/presentations/{locale}")
    @PermitAll
    public PresentationResource findPresentationByLocale(@PathParam("discountId") @NotNull Long discountId, @NotNull @PathParam("locale") String locale) {
        Discount discount = entityManager.find(Discount.class, discountId);
        checkNotNull(discount);
        Presentation presentation = discount.getPresentationByLocale().get(locale);
        return presentationResource.init(discount, locale, presentation);
    }

    private void checkNotNull(Discount discount) {
        if (discount == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
