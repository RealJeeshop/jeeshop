package org.rembx.jeeshop.catalog;

import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Presentation;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN;

/**
 * Sub-resource of CatalogItem resources (Catalogs, Categories,...) dedicated to a Presentation instance.
 *
 * @author remi
 */
@RequestScoped
public class PresentationResource {

    private Presentation presentation;
    private CatalogItem parentCatalogItem;
    private String locale;
    private EntityManager entityManager;

    @Inject
    PresentationResource( @PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private PresentationResource(EntityManager entityManager, CatalogItem parentCatalogItem, String locale, Presentation presentation) {
        this.entityManager = entityManager;
        this.parentCatalogItem = parentCatalogItem;
        this.locale = locale;
        this.presentation = presentation;
    }

    static PresentationResource init(EntityManager entityManager, CatalogItem parentCatalogItem, String locale, Presentation presentation) {
        return new PresentationResource(entityManager, parentCatalogItem, locale, presentation);
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
    @RolesAllowed(ADMIN)
    public void delete() {
        checkEntityNotNull();
        parentCatalogItem.getPresentationByLocale().remove(presentation.getLocale());
        entityManager.merge(parentCatalogItem);
        entityManager.remove(entityManager.merge(presentation));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @RolesAllowed(ADMIN)
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
    @RolesAllowed(ADMIN)
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

    public static PresentationResource build(Presentation presentation, String locale, CatalogItem parentCatalogItem) {
        return new PresentationResource(null, parentCatalogItem, locale, presentation);
    }
}
