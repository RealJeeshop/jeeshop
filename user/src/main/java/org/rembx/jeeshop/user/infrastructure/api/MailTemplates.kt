package org.rembx.jeeshop.user.infrastructure.api

import freemarker.template.Configuration
import freemarker.template.Template
import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.rembx.jeeshop.mail.Mailer
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY
import org.rembx.jeeshop.user.domain.MailTemplateRepository
import org.rembx.jeeshop.user.domain.MailTemplate
import org.rembx.jeeshop.user.infrastructure.repository.UserPersistenceUnit
import java.io.StringWriter

@Path("/mailtemplates")
@ApplicationScoped
class MailTemplates(
    @PersistenceUnit(UserPersistenceUnit.NAME) private val entityManager: EntityManager,
    private val mailTemplateRepository: MailTemplateRepository,
    private val mailer: Mailer,
) {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    fun create(mailTemplate: MailTemplate): MailTemplate {
        if (mailTemplateRepository.findByNameAndLocale(mailTemplate.name!!, mailTemplate.locale) != null) {
            throw WebApplicationException(Response.Status.CONFLICT)
        }
        entityManager.persist(mailTemplate)
        return mailTemplate
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Path("/test/{recipient}")
    fun sendTestEmail(
        properties: Any?,
        @NotNull @QueryParam("templateName") templateName: String,
        @NotNull @QueryParam("locale") locale: String,
        @NotNull @PathParam("recipient") recipient: String,
    ) {
        val template = mailTemplateRepository.findByNameAndLocale(templateName, locale)
            ?: throw WebApplicationException(Response.Status.NOT_FOUND)
        try {
            sendMail(template, recipient, properties)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Transactional
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long) {
        val mailTemplate = entityManager.find(MailTemplate::class.java, id)
        checkNotNull(mailTemplate)
        entityManager.remove(mailTemplate)
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed(ADMIN)
    fun modify(mailTemplate: MailTemplate): MailTemplate {
        val existing = entityManager.find(MailTemplate::class.java, mailTemplate.id)
        checkNotNull(existing)
        val duplicate = mailTemplateRepository.findByNameAndLocale(mailTemplate.name!!, mailTemplate.locale)
        if (duplicate != null && duplicate.id != mailTemplate.id) {
            throw WebApplicationException(Response.Status.CONFLICT)
        }
        mailTemplate.creationDate = existing.creationDate
        return entityManager.merge(mailTemplate)
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun findAll(
        @QueryParam("name") name: String?,
        @QueryParam("start") start: Int?,
        @QueryParam("size") size: Int?,
        @QueryParam("orderBy") orderBy: String?,
        @QueryParam("isDesc") isDesc: Boolean?,
    ): List<MailTemplate> =
        if (name != null) mailTemplateRepository.findByName(name)
        else mailTemplateRepository.findAll(start, size, orderBy, isDesc)

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun find(@PathParam("id") @NotNull id: Long): MailTemplate =
        entityManager.find(MailTemplate::class.java, id)
            ?: throw WebApplicationException(Response.Status.NOT_FOUND)

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun count(): Long = mailTemplateRepository.countAll()

    private fun checkNotNull(mailTemplate: MailTemplate?) {
        if (mailTemplate == null) throw WebApplicationException(Response.Status.NOT_FOUND)
    }

    private fun sendMail(mailTemplate: MailTemplate, recipient: String, properties: Any?) {
        val template = Template(
            mailTemplate.name!!,
            mailTemplate.content!!,
            Configuration(Configuration.VERSION_2_3_21),
        )
        val body = StringWriter()
        template.process(properties, body)
        mailer.sendMail(mailTemplate.subject!!, recipient, body.toString())
    }
}
