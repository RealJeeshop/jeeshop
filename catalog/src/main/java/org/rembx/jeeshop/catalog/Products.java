package org.rembx.jeeshop.catalog;


import org.rembx.jeeshop.catalog.model.*;
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
import java.util.*;

import static org.rembx.jeeshop.catalog.model.QDiscount.discount;
import static org.rembx.jeeshop.catalog.model.QProduct.product;
import static org.rembx.jeeshop.catalog.model.QSKU.sKU;
import static org.rembx.jeeshop.role.AuthorizationUtils.isAdminUser;

/**
 * @author remi
 */

@Path("/products")
@Stateless
public class Products {

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    PresentationResource presentationResource;

    @Inject
    private CatalogItemFinder catalogItemFinder;

    @Resource
    private SessionContext sessionContext;

    public Products() {

    }

    public Products(EntityManager entityManager, CatalogItemFinder catalogItemFinder) {
        this.entityManager = entityManager;
        this.catalogItemFinder = catalogItemFinder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Product create(Product product) {
        if (product.getChildSKUsIds() != null) {
            List<SKU> newSkus = new ArrayList<>();
            product.getChildSKUsIds().forEach(skuId -> newSkus.add(entityManager.find(SKU.class, skuId)));
            product.setChildSKUs(newSkus);
        }
        if (product.getDiscountsIds() != null) {
            List<Discount> newDiscounts = new ArrayList<>();
            product.getDiscountsIds().forEach(discountId -> newDiscounts.add(entityManager.find(Discount.class, discountId)));
            product.setDiscounts(newDiscounts);
        }
        entityManager.persist(product);
        return product;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{productId}")
    public void delete(@PathParam("productId") Long productId) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);
        entityManager.remove(product);

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Product modify(Product product) {
        Product originalProduct = entityManager.find(Product.class, product.getId());
        checkNotNull(originalProduct);

        if (product.getChildSKUsIds() != null) {
            List<SKU> newSkus = new ArrayList<>();
            product.getChildSKUsIds().forEach(skuId -> newSkus.add(entityManager.find(SKU.class, skuId)));
            product.setChildSKUs(newSkus);
        } else {
            product.setChildSKUs(originalProduct.getChildSKUs());
        }

        if (product.getDiscountsIds() != null) {
            List<Discount> newDiscounts = new ArrayList<>();
            product.getDiscountsIds().forEach(discountId -> newDiscounts.add(entityManager.find(Discount.class, discountId)));
            product.setDiscounts(newDiscounts);
        } else {
            product.setDiscounts(originalProduct.getDiscounts());
        }

        return entityManager.merge(product);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<Product> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        if (search != null)
            return catalogItemFinder.findBySearchCriteria(product, search, start, size);
        else
            return catalogItemFinder.findAll(product, start, size);
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Long count(@QueryParam("search") String search) {
        if (search != null)
            return catalogItemFinder.countBySearchCriteria(product, search);
        else
            return catalogItemFinder.countAll(product);
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
    @Path("/{productId}/presentationslocales")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Set<String> findPresentationsLocales(@PathParam("productId") @NotNull Long productId) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);
        return product.getPresentationByLocale().keySet();
    }

    @Path("/{productId}/presentations/{locale}")
    @RolesAllowed(JeeshopRoles.ADMIN)
    public PresentationResource findPresentationByLocale(@PathParam("productId") @NotNull Long productId, @NotNull @PathParam("locale") String locale) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);
        Presentation presentation = product.getPresentationByLocale().get(locale);
        return presentationResource.init(presentation, locale, product);
    }


    @GET
    @Path("/{productId}/skus")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<SKU> findChildSKUs(@PathParam("productId") @NotNull Long productId, @QueryParam("locale") String locale) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);
        List<SKU> childSKUs = product.getChildSKUs();
        if (childSKUs.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext))
            return childSKUs;
        else
            return catalogItemFinder.findVisibleCatalogItems(sKU, childSKUs, locale);
    }

    @GET
    @Path("/{productId}/discounts")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<Discount> findDiscounts(@PathParam("productId") @NotNull Long productId) {
        Product product = entityManager.find(Product.class, productId);
        checkNotNull(product);
        List<Discount> discounts = product.getDiscounts();
        if (discounts.isEmpty()) {
            return new ArrayList<>();
        }

        if (isAdminUser(sessionContext))
            return discounts;
        else
            return catalogItemFinder.findVisibleCatalogItems(discount, discounts, null);
    }


    private void checkNotNull(Product product) {
        if (product == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
