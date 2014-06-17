package org.rembx.jeeshop.user;

import org.rembx.jeeshop.user.model.Roles;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * Customer resource
 */

@Path("/user")
@Stateless
public class UserResource {

    @Resource
    private SessionContext sessionContext;

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private UserFinder userFinder;

    public UserResource() {
    }

    public UserResource(EntityManager entityManager, UserFinder userFinder) {
        this.entityManager = entityManager;
        this.userFinder = userFinder;
    }


    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(Roles.ADMIN)// TODO paginate
    public List<User> findAll(@QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        return userFinder.findAll(start, size);
    }

    @GET
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(Roles.ADMIN)
    public User findById(@PathParam("customerId") @NotNull Long customerId) {

        User user = entityManager.find(User.class, customerId);

        if (user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return user;
    }

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Roles.USER, Roles.ADMIN})
    public User findCurrentUser(@Context SecurityContext sec) {

        User user = userFinder.findByLogin(sec.getUserPrincipal().getName());

        if (user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return user;
    }

}
