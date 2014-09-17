package org.rembx.jeeshop.user;

import org.rembx.jeeshop.role.JeeshopRoles;
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
import java.util.HashSet;
import java.util.List;

/**
 * Newsletter resource
 */

@Path("/newsletters")
@Stateless
public class Newsletters {

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
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Newsletter create(Newsletter newsletter) {
        entityManager.persist(newsletter);
        return newsletter;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{newsletterId}")
    public void delete(@PathParam("newsletterId") Long newsletterId) {
        Newsletter newsletter = entityManager.find(Newsletter.class, newsletterId);
        checkNotNull(newsletter);
        entityManager.remove(newsletter);

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Newsletter modify(Newsletter newsletter) {
        Newsletter existingNewsletter = entityManager.find(Newsletter.class, newsletter.getId());
        checkNotNull(existingNewsletter);
        return entityManager.merge(newsletter);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<Newsletter> findAll(@QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        return newsletterFinder.findAll(start, size);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
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
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Long count() {
        return newsletterFinder.countAll();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Newsletter findByName(@QueryParam("name") String name) {
        Newsletter newsletter = newsletterFinder.findByName(name);
        checkNotNull(newsletter);
        return newsletter;
    }

    private void checkNotNull(Newsletter newsletter) {
        if (newsletter == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
