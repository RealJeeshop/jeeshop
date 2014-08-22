package org.rembx.jeeshop.catalog;

import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.Presentation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Sub-resource of Catalogs, Categories, Products and Skus resources.
 * @Author remi
 */

public class PresentationResource {

    private Presentation existingPresentation;
    private CatalogItem parentCatalogItem;
    private String locale;

    @PersistenceContext(unitName = CatalogPersistenceUnit.NAME)
    private EntityManager entityManager;

    public PresentationResource() {
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Presentation find(){
        checkEntityNotNull();
        return existingPresentation;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void delete(){
        checkEntityNotNull();
        parentCatalogItem.getPresentationByLocale().remove(existingPresentation.getLocale());
        entityManager.merge(parentCatalogItem);
        entityManager.remove(entityManager.merge(existingPresentation));
    };

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public Presentation createLocalizedPresentation(Presentation presentation){

        if (existingPresentation != null) {
            // Item already exist
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        entityManager.persist(presentation);

        parentCatalogItem.getPresentationByLocale().put(locale,presentation);
        entityManager.merge(parentCatalogItem);

        return presentation;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public Presentation modifyLocalizedPresentation(Presentation presentation){
        checkEntityNotNull();

        if (existingPresentation == null) {
            // Item does not exist
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        entityManager.merge(presentation);

        return presentation;
    }

    private void checkEntityNotNull() {
        if (existingPresentation == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    public PresentationResource init(Presentation presentation, String locale, CatalogItem parentCatalogItem){
        this.existingPresentation = presentation;
        this.locale = locale;
        this.parentCatalogItem = parentCatalogItem;
        return this;
    }
}
