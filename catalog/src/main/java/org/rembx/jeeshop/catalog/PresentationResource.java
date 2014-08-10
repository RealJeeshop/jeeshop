package org.rembx.jeeshop.catalog;

import org.rembx.jeeshop.catalog.model.CatalogItem;
import org.rembx.jeeshop.catalog.model.Presentation;
import org.rembx.jeeshop.role.JeeshopRoles;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * @Author remi
 */
public class PresentationResource {

    private Presentation existingPresentation;
    private CatalogItem parentCatalogItem;
    private String locale;

    private EntityManager entityManager;

    public PresentationResource(Presentation presentation, String locale, EntityManager entityManager, CatalogItem parentCatalogItem){
        this.entityManager = entityManager;
        this.existingPresentation = presentation;
        this.parentCatalogItem = parentCatalogItem;
        this.locale = locale;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Presentation find(){
        checkEntityNotNull();
        return existingPresentation;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(){
        checkEntityNotNull();
        parentCatalogItem.getPresentationByLocale().remove(existingPresentation.getLocale());
        entityManager.remove(existingPresentation);
    };

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Presentation createLocalizedPresentation(Presentation presentation){

        if (existingPresentation != null) {
            // Item already exist
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        entityManager.persist(presentation);

        parentCatalogItem.getPresentationByLocale().put(locale,presentation);

        return presentation;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Presentation modifyLocalizedPresentation(Presentation presentation, @PathParam("skuId") @NotNull Long skuId, @NotNull @PathParam("locale") String locale){
        checkEntityNotNull();

        if (existingPresentation == null) {
            // Item does not exist
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        entityManager.merge(presentation);

        parentCatalogItem.getPresentationByLocale().put(locale,presentation);

        return presentation;
    }

    private void checkEntityNotNull() {
        if (existingPresentation == null){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
