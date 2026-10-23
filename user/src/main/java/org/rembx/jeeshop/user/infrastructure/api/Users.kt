package org.rembx.jeeshop.user.infrastructure.api

import com.google.common.collect.Sets
import freemarker.template.Configuration
import freemarker.template.Template
import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.HEAD
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.rembx.jeeshop.mail.Mailer
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.USER
import org.rembx.jeeshop.user.domain.Mails
import org.rembx.jeeshop.user.domain.MailTemplateRepository
import org.rembx.jeeshop.user.domain.RoleRepository
import org.rembx.jeeshop.user.domain.UserRepository
import org.rembx.jeeshop.user.CountryChecker
import org.rembx.jeeshop.user.domain.MailTemplate
import org.rembx.jeeshop.user.domain.Role
import org.rembx.jeeshop.user.domain.RoleName
import org.rembx.jeeshop.user.domain.User
import org.rembx.jeeshop.user.infrastructure.repository.UserPersistenceUnit
import org.slf4j.LoggerFactory
import java.io.StringWriter
import java.util.UUID

@Path("/users")
@ApplicationScoped
class Users(
    @PersistenceUnit(UserPersistenceUnit.NAME) private val entityManager: EntityManager,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val countryChecker: CountryChecker,
    private val mailTemplateRepository: MailTemplateRepository,
    private val mailer: Mailer,
) {
    @ConfigProperty(name = "security.salt")
    private lateinit var salt: String

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    fun create(@Context securityContext: SecurityContext, @NotNull user: User): User {
        if (user.id != null) throw WebApplicationException(Response.Status.BAD_REQUEST)
        if (userRepository.findByLogin(user.login) != null) throw WebApplicationException(Response.Status.CONFLICT)

        user.address?.let { address ->
            if (address.id != null) throw WebApplicationException(Response.Status.BAD_REQUEST)
            if (!countryChecker.isAvailable(address.countryIso3Code)) {
                LOG.error("Country {} is not available", address.countryIso3Code)
                throw WebApplicationException(Response.Status.BAD_REQUEST)
            }
        }

        entityManager.persist(user)
        val role: Role = roleRepository.findByName(RoleName.user)!!
        user.roles = Sets.newHashSet(role)
        user.password = BcryptUtil.bcryptHash(user.password!!, 10, salt.toByteArray())
        if (!securityContext.isUserInRole(ADMIN)) {
            user.activated = false
            generateActionTokenAndSendMail(user, Mails.userRegistration)
        }
        return user
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userLogin}")
    @PermitAll
    fun activate(@NotNull @PathParam("userLogin") userLogin: String, @NotNull token: String) {
        val user = userRepository.findByLogin(userLogin)
        if (user != null && user.actionToken != null && user.actionToken == UUID.fromString(token)) {
            user.activated = true
            user.actionToken = null
        } else {
            throw WebApplicationException(Response.Status.NOT_FOUND)
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userLogin}/password")
    @PermitAll
    fun sendResetPasswordMail(@NotNull @PathParam("userLogin") userLogin: String) {
        val user = userRepository.findByLogin(userLogin)
            ?: throw WebApplicationException(Response.Status.NOT_FOUND)
        generateActionTokenAndSendMail(user, Mails.userResetPassword)
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{userLogin}/password")
    @PermitAll
    fun resetPassword(
        @Context securityContext: SecurityContext,
        @NotNull @PathParam("userLogin") userLogin: String,
        @QueryParam("token") token: String?,
        @NotNull newPassword: String?,
    ) {
        val user = when {
            securityContext.isUserInRole(ADMIN) -> userRepository.findByLogin(userLogin)
            securityContext.isUserInRole(USER) -> {
                val currentUser = userRepository.findByLogin(securityContext.userPrincipal.name)!!
                if (userLogin != currentUser.login) {
                    throw WebApplicationException(Response.Status.UNAUTHORIZED)
                }
                currentUser
            }
            else -> {
                val targetUser = userRepository.findByLogin(userLogin)
                if (targetUser == null || targetUser.actionToken != UUID.fromString(token)) {
                    throw WebApplicationException(Response.Status.NOT_FOUND)
                }
                targetUser.actionToken = null
                targetUser
            }
        }!!
        user.password = BcryptUtil.bcryptHash(newPassword!!, 10, salt.toByteArray())
        user.activated = true
        sendMail(user, Mails.userChangePassword)
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Path("/{userId}")
    fun delete(@NotNull @PathParam("userId") userId: Long) {
        val user = entityManager.find(User::class.java, userId)
        checkNotNull(user)
        entityManager.remove(user)
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RolesAllowed(ADMIN, USER)
    fun modify(@Context securityContext: SecurityContext, @NotNull user: User): User {
        var existingUser: User? = null
        if (securityContext.isUserInRole(USER) && !securityContext.isUserInRole(ADMIN)) {
            val authenticatedUser = userRepository.findByLogin(securityContext.userPrincipal.name)
                ?: throw WebApplicationException(Response.Status.NOT_FOUND)
            existingUser = authenticatedUser
            if (authenticatedUser.id != user.id || authenticatedUser.login != user.login) {
                throw WebApplicationException(Response.Status.UNAUTHORIZED)
            }
            user.activated = authenticatedUser.activated
            user.disabled = authenticatedUser.disabled
            user.actionToken = authenticatedUser.actionToken
        }
        if (existingUser == null) existingUser = entityManager.find(User::class.java, user.id)
        val persistedUser = existingUser ?: throw WebApplicationException(Response.Status.NOT_FOUND)
        user.password = persistedUser.password
        user.creationDate = persistedUser.creationDate
        user.roles = persistedUser.roles
        return entityManager.merge(user)
    }

    @HEAD
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    fun authenticate(): Boolean {
        println(BcryptUtil.bcryptHash("jeeshop"))
        return true
    }

    @POST
    @Path("/administrators")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN, ADMIN_READONLY)
    fun authenticateAdminUser(@Context context: SecurityContext): User? =
        userRepository.findByLogin(context.userPrincipal.name)

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun findAll(
        @QueryParam("search") search: String?,
        @QueryParam("start") start: Int?,
        @QueryParam("size") size: Int?,
        @QueryParam("orderBy") orderBy: String?,
        @QueryParam("isDesc") isDesc: Boolean?,
    ): List<User> =
        if (search != null) userRepository.findBySearchCriteria(search, start, size, orderBy, isDesc)
        else userRepository.findAll(start, size, orderBy, isDesc)

    @GET
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun find(@PathParam("customerId") @NotNull customerId: Long): User =
        entityManager.find(User::class.java, customerId)
            ?: throw WebApplicationException(Response.Status.NOT_FOUND)

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    fun count(@QueryParam("search") search: String?): Long =
        if (search != null) userRepository.countBySearchCriteria(search) else userRepository.countAll()

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, ADMIN_READONLY, USER)
    fun findCurrentUser(@Context securityContext: SecurityContext): User =
        userRepository.findByLogin(securityContext.userPrincipal.name)
            ?: throw WebApplicationException(Response.Status.NOT_FOUND)

    private fun checkNotNull(user: User?) {
        if (user == null) throw WebApplicationException(Response.Status.NOT_FOUND)
    }

    private fun generateActionTokenAndSendMail(user: User, mailType: Mails): User {
        user.actionToken = UUID.randomUUID()
        sendMail(user, mailType)
        return user
    }

    private fun sendMail(user: User, mailType: Mails) {
        val mailTemplate: MailTemplate? =
            mailTemplateRepository.findByNameAndLocale(mailType.name, user.preferredLocale)
        if (mailTemplate == null) {
            LOG.debug("Mail template {} is not configured.", mailType)
            return
        }
        try {
            val template = Template(
                mailType.name,
                mailTemplate.content,
                Configuration(Configuration.VERSION_2_3_21),
            )
            val body = StringWriter()
            template.process(user, body)
            mailer.sendMail(mailTemplate.subject!!, user.login!!, body.toString())
        } catch (e: Exception) {
            LOG.error("Unable to send mail {} to user {}", mailType, user.login, e)
        }
    }

    fun setSalt(salt: String) {
        this.salt = salt
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(Users::class.java)
    }
}
