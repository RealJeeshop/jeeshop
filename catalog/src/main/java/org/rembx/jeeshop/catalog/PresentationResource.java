package org.rembx.jeeshop.catalog;

import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Presentation;
import org.rembx.jeeshop.role.JeeshopRoles;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Sub-resource of CatalogItem resources (Catalogs, Categories,...) dedicated to a Presentation instance.
 * @Author remi
 */

public class PresentationResource {

    private Presentation presentation;
    private CatalogItem parentCatalogItem;
    private String locale;

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    public PresentationResource() {
    }

    public PresentationResource(EntityManager entityManager, CatalogItem parentCatalogItem, String locale, Presentation presentation) {
        this.entityManager = entityManager;
        this.parentCatalogItem = parentCatalogItem;
        this.locale = locale;
        this.presentation = presentation;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Presentation find() {
        checkEntityNotNull();
        return presentation;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public void delete() {
        checkEntityNotNull();
        parentCatalogItem.getPresentationByLocale().remove(presentation.getLocale());
        entityManager.merge(parentCatalogItem);
        entityManager.remove(entityManager.merge(presentation));
    }

    ;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Presentation createLocalizedPresentation(Presentation presentation) {

        if (this.presentation != null) {
            // Item already exist
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        presentation.setLocale(locale);
        entityManager.persist(presentation);

        parentCatalogItem.getPresentationByLocale().put(locale, presentation);
        entityManager.merge(parentCatalogItem);

        return presentation;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Presentation modifyLocalizedPresentation(Presentation presentation) {
        checkEntityNotNull();

        if (this.presentation == null) {
            // Item does not exist
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

    public PresentationResource init(Presentation presentation, String locale, CatalogItem parentCatalogItem) {
        this.presentation = presentation;
        this.locale = locale;
        this.parentCatalogItem = parentCatalogItem;
        return this;
    }
}
