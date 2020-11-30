package org.rembx.jeeshop.catalog;


import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.catalog.model.*;
import org.rembx.jeeshop.catalog.model.Discount.ApplicableTo;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Set;

import static org.rembx.jeeshop.catalog.model.QDiscount.discount;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;
import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN;
import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY;

/**
 * @author remi
 */

@Path("/discounts")
@ApplicationScoped
public class Discounts {

    @Resource
    SecurityContext sessionContext;
    private EntityManager entityManager;
    private CatalogItemFinder catalogItemFinder;
    private PresentationResource presentationResource;
    private DiscountFinder discountFinder;

    Discounts(@PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager, CatalogItemFinder catalogItemFinder, PresentationResource presentationResource) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
        this.presentationResource = presentationResource;
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Discount create(Discount discount) {
        entityManager.persist(discount);
        return discount;
    }

    @DELETE
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Path("/{discountId}")
    public void delete(@PathParam("discountId") Long discountId) {
        Discount discount = entityManager.find(Discount.class, discountId);
        checkNotNull(discount);

        List<Product> productHolders = catalogItemFinder.findForeignHolder(QProduct.product, QProduct.product.discounts, discount);
        for (Product product : productHolders) {
            product.getDiscounts().remove(discount);
        }

        List<SKU> skuHolders = catalogItemFinder.findForeignHolder(QSKU.sKU, QSKU.sKU.discounts, discount);
        for (SKU sku : skuHolders) {
            sku.getDiscounts().remove(discount);
        }

        entityManager.remove(discount);

    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Discount modify(Discount discount) {
        Discount originalDiscount = entityManager.find(Discount.class, discount.getId());
        checkNotNull(originalDiscount);

        discount.setPresentationByLocale(originalDiscount.getPresentationByLocale());

        return entityManager.merge(discount);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<Discount> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size
            , @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc) {
        if (search != null)
            return catalogItemFinder.findBySearchCriteria(discount, search, start, size, orderBy, isDesc);
        else
            return catalogItemFinder.findAll(discount, start, size, orderBy, isDesc);
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
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Discount find(@PathParam("discountId") @NotNull Long discountId, @QueryParam("locale") String locale) {
        Discount discount = entityManager.find(Discount.class, discountId);
        if (isAdminUser(sessionContext))
            return discount;
        else
            return catalogItemFinder.filterVisible(discount, locale);
    }

    @GET
    @Path("/{discountId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Set<String> findPresentationsLocales(@PathParam("discountId") @NotNull Long discountId) {
        Discount discount = entityManager.find(Discount.class, discountId);
        checkNotNull(discount);
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
