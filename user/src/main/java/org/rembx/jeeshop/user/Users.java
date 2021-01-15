package org.rembx.jeeshop.user;

import com.google.common.collect.Sets;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.rest.WebApplicationException;
import org.rembx.jeeshop.user.mail.Mails;
import org.rembx.jeeshop.user.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

import static org.rembx.jeeshop.role.JeeshopRoles.*;

/**
 * Customer resource
 */

@Path("/users")
@ApplicationScoped
public class Users {

    private final static Logger LOG = LoggerFactory.getLogger(Users.class);

    private EntityManager entityManager;
    private UserFinder userFinder;
    private RoleFinder roleFinder;
    private CountryChecker countryChecker;
    private Mailer mailer;
    private MailTemplateFinder mailTemplateFinder;

    Users(@PersistenceUnit(UserPersistenceUnit.NAME) EntityManager entityManager, UserFinder userFinder, RoleFinder roleFinder, CountryChecker countryChecker,
                 MailTemplateFinder mailTemplateFinder, Mailer mailer) {
        this.entityManager = entityManager;
        this.userFinder = userFinder;
        this.roleFinder = roleFinder;
        this.countryChecker = countryChecker;
        this.mailTemplateFinder = mailTemplateFinder;
        this.mailer = mailer;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public User create(@Context SecurityContext securityContext, @NotNull User user) {

        if (user.getId() != null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        User userByLogin = userFinder.findByLogin(user.getLogin());

        if (userByLogin != null) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        final Address userAddress = user.getAddress();

        if (userAddress != null) {
            if (userAddress.getId() != null) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            if (!countryChecker.isAvailable(userAddress.getCountryIso3Code())) {
                LOG.error("Country {} is not available", userAddress.getCountryIso3Code());
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
        }

        entityManager.persist(user);
        Role userRole = roleFinder.findByName(RoleName.user);
        user.setRoles(Sets.newHashSet(userRole));

        user.setPassword(BcryptUtil.bcryptHash(user.getPassword()));

          if (!securityContext.isUserInRole(ADMIN)) {

            user.setActivated(false);
            generateActionTokenAndSendMail(user, Mails.userRegistration);
        }

        return user;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userLogin}")
    @PermitAll
    public void activate(@NotNull @PathParam("userLogin") String userLogin, @NotNull String token) {
        User user = userFinder.findByLogin(userLogin);
        if (user != null && user.getActionToken() != null && user.getActionToken().equals(UUID.fromString(token))) {
            user.setActivated(true);
            user.setActionToken(null);
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userLogin}/password")
    @PermitAll
    public void sendResetPasswordMail(@NotNull @PathParam("userLogin") String userLogin) {
        User user = userFinder.findByLogin(userLogin);
        if (user != null) {
            generateActionTokenAndSendMail(user, Mails.userResetPassword);
        } else {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userLogin}/password")
    @PermitAll
    public void resetPassword(@Context SecurityContext securityContext, @NotNull @PathParam("userLogin") String userLogin, @QueryParam("token") String token,
                              @NotNull String newPassword) {

        User user;
        if (securityContext.isUserInRole(ADMIN)) {

            user = userFinder.findByLogin(userLogin);

        } else if (securityContext.isUserInRole(USER)) {

            user = userFinder.findByLogin(securityContext.getUserPrincipal().getName());
            if (!userLogin.equals(user.getLogin())) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        } else {
            user = userFinder.findByLogin(userLogin);

            if (user == null || !user.getActionToken().equals(UUID.fromString(token))) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            user.setActionToken(null);
        }

        user.setPassword(BcryptUtil.bcryptHash(newPassword));
        user.setActivated(true);
        sendMail(user, Mails.userChangePassword);


    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Path("/{userId}")
    public void delete(@NotNull @PathParam("userId") Long userId) {
        User catalog = entityManager.find(User.class, userId);
        checkNotNull(catalog);
        entityManager.remove(catalog);

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed({ADMIN, USER})
    public User modify(@Context SecurityContext securityContext, @NotNull User user) {

        User existingUser = null;
        if (securityContext.isUserInRole(USER) && !securityContext.isUserInRole(ADMIN)) {
            existingUser = userFinder.findByLogin(securityContext.getUserPrincipal().getName());

            if (!existingUser.getId().equals(user.getId()) || !existingUser.getLogin().equals(user.getLogin())) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }

            user.setActivated(existingUser.getActivated());
            user.setDisabled(existingUser.getDisabled());
            user.setActionToken(existingUser.getActionToken());

        }

        if (existingUser == null) {
            existingUser = entityManager.find(User.class, user.getId());
        }
        checkNotNull(existingUser);
        user.setPassword(existingUser.getPassword());
        user.setCreationDate(existingUser.getCreationDate());
        user.setRoles(existingUser.getRoles());
        return entityManager.merge(user);
    }


    @HEAD
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Boolean authenticate() {

        System.out.println(BcryptUtil.bcryptHash("jeeshop"));
        return true;
    }

    @POST
    @Path("/administrators")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, STORE_ADMIN, ADMIN_READONLY})
    public User authenticateAdminUser(@Context SecurityContext context) {
        return userFinder.findByLogin(context.getUserPrincipal().getName());
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<User> findAll(@QueryParam("search") String search, @QueryParam("start") Integer start, @QueryParam("size") Integer size
            , @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc) {
        if (search != null)
            return userFinder.findBySearchCriteria(search, start, size, orderBy, isDesc);
        else
            return userFinder.findAll(start, size, orderBy, isDesc);
    }

    @GET
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
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
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count(@QueryParam("search") String search) {
        if (search != null)
            return userFinder.countBySearchCriteria(search);
        else
            return userFinder.countAll();
    }

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY, USER})
    public User findCurrentUser(@Context SecurityContext securityContext) {

        User user = userFinder.findByLogin(securityContext.getUserPrincipal().getName());
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

    private User generateActionTokenAndSendMail(User user, Mails mailType) {

        user.setActionToken(UUID.randomUUID());

        sendMail(user, mailType);

        return user;
    }

    private void sendMail(User user, Mails mailType) {
        MailTemplate mailTemplate = mailTemplateFinder.findByNameAndLocale(mailType.name(), user.getPreferredLocale());

        if (mailTemplate == null) {
            LOG.debug("Mail template " + mailType + " is not configured.");
            return;
        }

        try {
            Template mailContentTpl = new Template(mailType.name(), mailTemplate.getContent(), new Configuration(Configuration.VERSION_2_3_21));
            final StringWriter mailBody = new StringWriter();
            mailContentTpl.process(user, mailBody);
            mailer.sendMail(mailTemplate.getSubject(), user.getLogin(), mailBody.toString());
        } catch (Exception e) {
            LOG.error("Unable to send mail " + mailType + " to user " + user.getLogin(), e);
        }

        return;
    }

}
