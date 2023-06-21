package org.rembx.jeeshop.catalog;

import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Presentation;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;
import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN;
import static org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN;

/**
 * Sub-resource of CatalogItem resources (Catalogs, Categories,...) dedicated to a Presentation instance.
 *
 * @author remi
 */
@ApplicationScoped
public class PresentationResource<T extends CatalogItem> {

    private Presentation presentation;
    private CatalogItem parentCatalogItem;
    private String locale;
    private EntityManager entityManager;
    private UserTransaction transaction;
    private Class<T> clazz;

    PresentationResource(@PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager, UserTransaction userTransaction) {
        this.entityManager = entityManager;
        this.transaction = userTransaction;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Presentation find() {
        checkEntityNotNull();
        return presentation;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(value = REQUIRES_NEW)
    @RolesAllowed({STORE_ADMIN, ADMIN})
    public void delete() {
        checkEntityNotNull();
        refreshCatalogItem();
        parentCatalogItem.getPresentationByLocale().remove(presentation.getLocale());
        entityManager.merge(parentCatalogItem);
        entityManager.remove(entityManager.merge(presentation));
    }

    private void refreshCatalogItem() {
        parentCatalogItem = entityManager.find(this.clazz, parentCatalogItem.getId());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed({STORE_ADMIN, ADMIN})
    public Presentation createLocalizedPresentation(Presentation presentation) {

        if (this.presentation != null) {
            // Item already exist
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        presentation.setLocale(locale);
        entityManager.persist(presentation);

        refreshCatalogItem();
        parentCatalogItem.getPresentationByLocale().put(locale, presentation);
        CatalogItem c = entityManager.merge(parentCatalogItem);

        return presentation;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(REQUIRES_NEW)
    @RolesAllowed({STORE_ADMIN, ADMIN})
    public Presentation modifyLocalizedPresentation(Presentation presentation) {

        checkEntityNotNull();

        if (this.presentation == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        entityManager.merge(presentation);

        return presentation;
    }

    private void checkEntityNotNull() {
        if (presentation == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    public PresentationResource<T> init(Class<T> clazz, CatalogItem parentCatalogItem, String locale, Presentation presentation) {
        this.clazz = clazz;
        this.presentation = presentation;
        this.locale = locale;
        this.parentCatalogItem = parentCatalogItem;
        return this;
    }
}
