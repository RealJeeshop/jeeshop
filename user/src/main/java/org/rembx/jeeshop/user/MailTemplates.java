package org.rembx.jeeshop.user;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.rest.WebApplicationException;
import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringWriter;
import java.util.List;

import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN;
import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY;

/**
 * Mail template resource
 */

@Path("/mailtemplates")
@ApplicationScoped
public class MailTemplates {

    private Mailer mailer;
    private EntityManager entityManager;
    private MailTemplateFinder mailTemplateFinder;

    MailTemplates(@PersistenceUnit(UserPersistenceUnit.NAME) EntityManager entityManager, MailTemplateFinder mailTemplateFinder, Mailer mailer) {
        this.entityManager = entityManager;
        this.mailTemplateFinder = mailTemplateFinder;
        this.mailer = mailer;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public MailTemplate create(MailTemplate mailTemplate) {
        MailTemplate existingTpl = mailTemplateFinder.findByNameAndLocale(mailTemplate.getName(), mailTemplate.getLocale());
        if (existingTpl != null) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        entityManager.persist(mailTemplate);
        return mailTemplate;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Path("/test/{recipient}")
    public void sendTestEmail(Object properties, @NotNull @QueryParam("templateName") String templateName, @NotNull @QueryParam("locale") String locale, @NotNull @PathParam("recipient") String recipient) {
        MailTemplate existingTpl = mailTemplateFinder.findByNameAndLocale(templateName, locale);

        if (existingTpl == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        try {
            sendMail(existingTpl, recipient, properties);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Transactional
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        MailTemplate mailTemplate = entityManager.find(MailTemplate.class, id);
        checkNotNull(mailTemplate);
        entityManager.remove(mailTemplate);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed(ADMIN)
    public MailTemplate modify(MailTemplate mailTemplate) {

        MailTemplate existingMailTemplate = entityManager.find(MailTemplate.class, mailTemplate.getId());
        checkNotNull(existingMailTemplate);

        MailTemplate existingTplWithSameLocaleAndName = mailTemplateFinder.findByNameAndLocale(mailTemplate.getName(), mailTemplate.getLocale());

        if (existingTplWithSameLocaleAndName != null && !existingTplWithSameLocaleAndName.getId().equals(mailTemplate.getId())) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        mailTemplate.setCreationDate(existingMailTemplate.getCreationDate());
        return entityManager.merge(mailTemplate);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public List<MailTemplate> findAll(@QueryParam("name") String name, @QueryParam("start") Integer start, @QueryParam("size") Integer size
            , @QueryParam("orderBy") String orderBy, @QueryParam("isDesc") Boolean isDesc) {
        if (name != null) {
            return mailTemplateFinder.findByName(name);
        }

        return mailTemplateFinder.findAll(start, size, orderBy, isDesc);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
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
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    public Long count() {
        return mailTemplateFinder.countAll();
    }


    private void checkNotNull(MailTemplate mailTemplate) {
        if (mailTemplate == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    private void sendMail(MailTemplate mailTemplate, String recipient, Object properties) throws Exception {
        Template mailContentTpl = new Template(mailTemplate.getName(), mailTemplate.getContent(), new Configuration(Configuration.VERSION_2_3_21));
        final StringWriter mailBody = new StringWriter();
        mailContentTpl.process(properties, mailBody);
        mailer.sendMail(mailTemplate.getSubject(), recipient, mailBody.toString());
    }

}
