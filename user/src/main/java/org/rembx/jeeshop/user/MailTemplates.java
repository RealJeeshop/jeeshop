package org.rembx.jeeshop.user;

import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Mail template resource
 */

@Path("/mailtemplates")
@Stateless
public class MailTemplates {

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private MailTemplateFinder mailTemplateFinder;


    public MailTemplates() {
    }

    public MailTemplates(EntityManager entityManager, MailTemplateFinder mailTemplateFinder) {
        this.entityManager = entityManager;
        this.mailTemplateFinder = mailTemplateFinder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public MailTemplate create(MailTemplate mailTemplate) {
        entityManager.persist(mailTemplate);
        return mailTemplate;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        MailTemplate mailTemplate = entityManager.find(MailTemplate.class, id);
        checkNotNull(mailTemplate);
        entityManager.remove(mailTemplate);

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public MailTemplate modify(MailTemplate mailTemplate) {
        MailTemplate existingMailTemplate = entityManager.find(MailTemplate.class, mailTemplate.getId());
        checkNotNull(existingMailTemplate);
        return entityManager.merge(mailTemplate);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<MailTemplate> findAll(@QueryParam("name") String name, @QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        if (name!=null){
            return mailTemplateFinder.findByName(name);
        }

        return mailTemplateFinder.findAll(start, size);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public MailTemplate find(@PathParam("id") @NotNull Long id) {
        MailTemplate mailTemplate = entityManager.find(MailTemplate.class, id);
        if (mailTemplate == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return mailTemplate;
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Long count() {
        return mailTemplateFinder.countAll();
    }


    private void checkNotNull(MailTemplate mailTemplate) {
        if (mailTemplate == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
