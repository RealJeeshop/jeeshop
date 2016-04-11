package org.rembx.jeeshop.user.mail;

import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.rest.WebApplicationException;
import org.rembx.jeeshop.user.NewsletterFinder;
import org.rembx.jeeshop.user.model.Newsletter;
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

import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN;
import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY;

/**
 * Newsletters resource
 */
@Path("/newsletters")
@Stateless
public class Newsletters {

    @Inject
    private Mailer mailer;

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private NewsletterFinder newsletterFinder;

    public Newsletters() {
    }

    public Newsletters(EntityManager entityManager, NewsletterFinder newsletterFinder) {
        this.entityManager = entityManager;
        this.newsletterFinder = newsletterFinder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Newsletter create(Newsletter newsletter) {
        entityManager.persist(newsletter);
        return newsletter;
    }


    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        Newsletter newsletter = entityManager.find(Newsletter.class, id);
        checkNotNull(newsletter);
        entityManager.remove(newsletter);

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Newsletter modify(Newsletter newsletter) {

        Newsletter existingNewsletter = entityManager.find(Newsletter.class, newsletter.getId());
        checkNotNull(existingNewsletter);

        return entityManager.merge(newsletter);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<Newsletter> findAll(@QueryParam("name") String name, @QueryParam("start") Integer start, @QueryParam("size") Integer size
            , @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc, @QueryParam("locale") String locale) {

        return newsletterFinder.findAll(start, size, orderBy, isDesc, name, locale);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Newsletter find(@PathParam("id") @NotNull Long id) {
        Newsletter newsletter = entityManager.find(Newsletter.class, id);
        if (newsletter == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return newsletter;
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count() {
        return newsletterFinder.countAll();
    }


    private void checkNotNull(Newsletter newsletter) {
        if (newsletter == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
