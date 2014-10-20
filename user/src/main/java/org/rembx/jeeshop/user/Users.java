package org.rembx.jeeshop.user;

import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.rembx.jeeshop.user.model.Role;
import org.rembx.jeeshop.user.model.RoleName;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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
import java.util.UUID;

/**
 * Customer resource
 */

@Path("/users")
@Stateless
public class Users {

    @PersistenceContext(unitName = UserPersistenceUnit.NAME)
    private EntityManager entityManager;

    @Inject
    private UserFinder userFinder;

    @Inject
    private RoleFinder roleFinder;

    @Inject
    private Mailer mailer;

    public Users() {
    }

    public Users(EntityManager entityManager, UserFinder userFinder, RoleFinder roleFinder) {
        this.entityManager = entityManager;
        this.userFinder = userFinder;
        this.roleFinder = roleFinder;
    }

    @POST
    @Path("/public")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public User subscribe(User user) {
        if (user.getId() != null){
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        User userByLogin = userFinder.findByLogin(user.getLogin());

        if (userByLogin != null){
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        entityManager.persist(user);
        Role userRole = roleFinder.findByName(RoleName.user);
        user.setRoles(Sets.newHashSet(userRole));
        user.setPassword(Hashing.sha256().hashString(user.getPassword()).toString());
        user.setActionToken(UUID.randomUUID());
        user.setActivated(false);

        // TODO Mail sending

        return user;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public User create(User user) {
        entityManager.persist(user);
        Role userRole = roleFinder.findByName(RoleName.user);
        user.setRoles(Sets.newHashSet(userRole));
        user.setPassword(Hashing.sha256().hashString(user.getPassword()).toString());
        return user;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{userId}")
    public void delete(@PathParam("userId") Long userId) {
        User catalog = entityManager.find(User.class, userId);
        checkNotNull(catalog);
        entityManager.remove(catalog);

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public User modify(User user) {
        User existingUser = entityManager.find(User.class, user.getId());
        checkNotNull(existingUser);
        user.setPassword(existingUser.getPassword());
        user.setRoles(existingUser.getRoles());
        return entityManager.merge(user);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public List<User> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size) {
        if (search != null)
            return userFinder.findBySearchCriteria(search, start, size);
        else
            return userFinder.findAll(start, size);
    }

    @GET
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public User find(@PathParam("customerId") @NotNull Long customerId) {
        User user = entityManager.find(User.class, customerId);
        if (user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return user;
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    public Long count(@QueryParam("search") String search) {
        if (search != null)
            return userFinder.countBySearchCriteria(search);
        else
            return userFinder.countAll();
    }

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({JeeshopRoles.USER, JeeshopRoles.ADMIN})
    public User findCurrentUser(@Context SecurityContext sec) {

        User user = userFinder.findByLogin(sec.getUserPrincipal().getName());

        if (user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return user;
    }

    private void checkNotNull(User originalUser) {
        if (originalUser == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
