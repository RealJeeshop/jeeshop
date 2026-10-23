package org.rembx.jeeshop.user

import io.quarkus.elytron.security.common.BcryptUtil
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.apache.http.auth.BasicUserPrincipal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.rembx.jeeshop.mail.Mailer
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.JeeshopRoles
import org.rembx.jeeshop.user.infrastructure.api.Users
import org.rembx.jeeshop.user.infrastructure.repository.MailTemplateRepositoryImpl
import org.rembx.jeeshop.user.infrastructure.repository.RoleRepositoryImpl
import org.rembx.jeeshop.user.infrastructure.repository.UserRepositoryImpl
import org.rembx.jeeshop.user.domain.Address
import org.rembx.jeeshop.user.domain.User
import org.rembx.jeeshop.user.infrastructure.repository.UserPersistenceUnit
import org.rembx.jeeshop.user.test.TestMailTemplate
import org.rembx.jeeshop.user.test.TestUser
import java.util.Date
import java.util.UUID

class UsersCT {
    private lateinit var service: Users
    private lateinit var testUser: TestUser
    private lateinit var testMailTemplate: TestMailTemplate
    private lateinit var entityManager: EntityManager
    private lateinit var mailerMock: Mailer
    private lateinit var sessionContextMock: SecurityContext

    companion object {
        private lateinit var entityManagerFactory: EntityManagerFactory

        @JvmStatic
        @org.junit.jupiter.api.BeforeAll
        fun beforeClass() {
            entityManagerFactory = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME)
        }
    }

    @BeforeEach
    fun setup() {
        testUser = TestUser.getInstance()
        testMailTemplate = TestMailTemplate.getInstance()
        entityManager = entityManagerFactory.createEntityManager()
        mailerMock = mock(Mailer::class.java)
        sessionContextMock = mock(SecurityContext::class.java)
        service = Users(
            entityManager,
            UserRepositoryImpl(entityManager),
            RoleRepositoryImpl(entityManager),
            CountryChecker("FRA,GBR"),
            MailTemplateRepositoryImpl(entityManager),
            mailerMock,
        )
        service.setSalt("jeeshopisgoooood")
    }

    @Test
    fun findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null, null, null, null)).isNotEmpty
    }

    @Test
    fun findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        val users = service.findAll(null, 0, 1, null, null)
        assertThat(users).isNotEmpty
        assertThat(users).containsExactly(testUser.firstUser())
    }

    @Test
    fun findAll_ByLogin_shouldReturnSearchedUser() {
        val users = service.findAll(testUser.firstUser().login, 0, 1, null, null)
        assertThat(users).containsExactly(testUser.firstUser())
    }

    @Test
    fun findAll_ByFirstName_shouldReturnSearchedUser() {
        val users = service.findAll(testUser.firstUser().firstname, 0, 1, null, null)
        assertThat(users).containsExactly(testUser.firstUser())
    }

    @Test
    fun findAll_ByLastName_shouldReturnSearchedUser() {
        val users = service.findAll(testUser.firstUser().lastname, 0, 1, null, null)
        assertThat(users).containsExactly(testUser.firstUser())
    }

    @Test
    fun find() {
        assertThat(service.find(1L)).isNotNull
    }

    @Test
    fun find_withUnknownId_ShouldThrowException() =
        assertWebException(Response.Status.NOT_FOUND) { service.find(999L) }

    @Test
    fun count() {
        assertThat(service.count(null)).isGreaterThan(0)
    }

    @Test
    fun count_withUnknownSearchCriteria() {
        assertThat(service.count("unknown")).isEqualTo(0)
    }

    @Test
    fun create_shouldThrowBadRequestExWhenUserHasAnId() {
        val user = User().apply { id = 777L }
        assertWebException(Response.Status.BAD_REQUEST) { service.create(sessionContextMock, user) }
    }

    @Test
    fun create_shouldThrowConflictExWhenUserWithGivenLoginAlreadyExists() {
        val user = User().apply { login = "test@test.com" }
        assertWebException(Response.Status.CONFLICT) { service.create(sessionContextMock, user) }
    }

    @Test
    fun create_shouldThrowBadRequestExceptionExWhenUsersCountryIsNotAvailable() {
        val user = User().apply {
            login = "toto@toto.com"
            address = Address().apply { countryIso3Code = "ZZZ" }
        }
        assertWebException(Response.Status.BAD_REQUEST) { service.create(sessionContextMock, user) }
    }

    @Test
    fun create_shouldThrowBadRequestExceptionExWhenUserAddressHasAnId() {
        val user = User().apply {
            login = "toto@toto.com"
            address = Address().apply { id = 1L }
        }
        assertWebException(Response.Status.BAD_REQUEST) { service.create(sessionContextMock, user) }
    }

    @Test
    fun create_shouldOnlyPersistUserWhenOperationIsTriggeredByAdminUser() {
        val user = User("register2@test.com", "test", "John", "Doe", "+33616161616", null, Date(), "fr_FR", null)
            .apply { gender = "M." }
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(true)
        entityManager.transaction.begin()
        service.create(sessionContextMock, user)
        entityManager.transaction.commit()
        verify(sessionContextMock).isUserInRole(JeeshopRoles.ADMIN)
        assertThat(entityManager.find(User::class.java, user.id!!)).isNotNull
        removeTestUser(user)
    }

    @Test
    fun create_shouldPersistEndUserAndRetrieveUserRegistrationMailTemplateAndSendMail() {
        val user = User("register3@test.com", "test", "John", "Doe", "+33616161616", null, Date(), "fr_FR", null)
            .apply { gender = "M." }
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false)
        entityManager.transaction.begin()
        service.create(sessionContextMock, user)
        entityManager.transaction.commit()
        verify(sessionContextMock).isUserInRole(JeeshopRoles.ADMIN)
        verify(mailerMock).sendMail(
            testMailTemplate.userRegistrationMailTemplate().subject!!,
            user.login!!,
            "<html><body>Welcome M. John Doe</body></html>",
        )
        assertThat(entityManager.find(User::class.java, user.id!!)).isNotNull
        assertThat(user.activated).isFalse
        assertThat(user.actionToken).isNotNull
        entityManager.remove(user)
    }

    @Test
    fun create_shouldJustPersistEndUserEvenWhenExceptionDuringMailSending() {
        val user = User("register1@test.com", "test", "John", "Doe", "+33616161616", null, Date(), "fr_FR", null)
            .apply {
                gender = "M."
                address = Address("7 blue street", "Nowhere", "00001", "John", "Doe", "M.", null, "FRA")
            }
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(false)
        entityManager.transaction.begin()
        service.create(sessionContextMock, user)
        entityManager.transaction.commit()
        doThrow(IllegalStateException("Test Exception")).`when`(mailerMock).sendMail(
            testMailTemplate.userRegistrationMailTemplate().subject!!,
            user.login!!,
            testMailTemplate.userRegistrationMailTemplate().content!!,
        )
        verify(sessionContextMock).isUserInRole(JeeshopRoles.ADMIN)
        verify(mailerMock).sendMail(
            testMailTemplate.userRegistrationMailTemplate().subject!!,
            user.login!!,
            "<html><body>Welcome M. John Doe</body></html>",
        )
        val persistedUser = entityManager.find(User::class.java, user.id!!)
        assertThat(persistedUser).isNotNull
        assertThat(persistedUser.address).isEqualTo(user.address)
        entityManager.remove(user)
    }

    @Test
    fun activate_shouldActivateUserAndClearActionToken() {
        val user = User("activate1@test.com", "test", "John", "Doe", "+33616161616", null, Date(), "fr_FR", null)
            .apply {
                gender = "M."
                actionToken = UUID.randomUUID()
                activated = false
            }
        entityManager.transaction.begin()
        entityManager.persist(user)
        entityManager.transaction.commit()
        service.activate(user.login!!, user.actionToken.toString())
        val modifiedUser = entityManager.find(User::class.java, user.id!!)
        assertThat(modifiedUser).isNotNull
        assertThat(modifiedUser.activated).isTrue
        assertThat(modifiedUser.actionToken).isNull()
    }

    @Test
    fun activate_shouldThrowNotFoundExWhenUserIsNotFound() =
        assertWebException(Response.Status.NOT_FOUND) { service.activate("unknown_login", UUID.randomUUID().toString()) }

    @Test
    fun sendResetPasswordMail_shouldGenerateActionTokenAndSendResetPasswordMail() {
        entityManager.transaction.begin()
        service.sendResetPasswordMail(testUser.firstUser().login!!)
        entityManager.transaction.commit()
        verify(mailerMock).sendMail(
            testMailTemplate.resetPasswordMailTemplate().subject!!,
            testUser.firstUser().login!!,
            "<html><body>Here is the link to reset your password</body></html>",
        )
        val persistedUser = entityManager.find(User::class.java, testUser.firstUser().id!!)
        assertThat(persistedUser).isNotNull
        entityManager.transaction.begin()
        persistedUser.actionToken = null
        entityManager.transaction.commit()
    }

    @Test
    fun sendResetPasswordMail_shouldThrowNotFoundEX_WhenUserIsNotFound() =
        assertWebException(Response.Status.NOT_FOUND) { service.sendResetPasswordMail("unknown_login") }

    @Test
    fun resetPassword_shouldUpdateUserPasswordWhenActionTokenMatchesUserActionToken() {
        val user = notActivatedTestUser()
        service.resetPassword(sessionContextMock, user.login!!, user.actionToken.toString(), "newPassword")
        val updatedUser = entityManager.find(User::class.java, user.id!!)
        assertThat(updatedUser).isNotNull
        assertThat(updatedUser.password).isEqualTo(BcryptUtil.bcryptHash("newPassword", 10, "jeeshopisgoooood".toByteArray()))
        assertThat(updatedUser.actionToken).isNull()
        verify(mailerMock).sendMail(
            testMailTemplate.changePasswordMailTpl().subject!!,
            user.login!!,
            testMailTemplate.changePasswordMailTpl().content!!,
        )
        removeTestUser(user)
    }

    @Test
    fun resetPassword_shouldUpdateUserPasswordForAuthenticatedUser() {
        val user = notActivatedTestUser()
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true)
        `when`(sessionContextMock.userPrincipal).thenReturn(BasicUserPrincipal(user.login!!))
        service.resetPassword(sessionContextMock, user.login!!, null, "newPassword")
        val updatedUser = entityManager.find(User::class.java, user.id!!)
        assertThat(updatedUser).isNotNull
        assertThat(updatedUser.password).isEqualTo(BcryptUtil.bcryptHash("newPassword", 10, "jeeshopisgoooood".toByteArray()))
        verify(mailerMock).sendMail(
            testMailTemplate.changePasswordMailTpl().subject!!,
            user.login!!,
            testMailTemplate.changePasswordMailTpl().content!!,
        )
        removeTestUser(user)
    }

    @Test
    fun resetPassword_shouldUpdateUserPasswordForAuthenticatedADMINUser() {
        val user = notActivatedTestUser()
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.ADMIN)).thenReturn(true)
        service.resetPassword(sessionContextMock, user.login!!, null, "newPassword")
        val updatedUser = entityManager.find(User::class.java, user.id!!)
        assertThat(updatedUser).isNotNull
        assertThat(updatedUser.password).isEqualTo(BcryptUtil.bcryptHash("newPassword", 10, "jeeshopisgoooood".toByteArray()))
        verify(mailerMock).sendMail(
            testMailTemplate.changePasswordMailTpl().subject!!,
            user.login!!,
            testMailTemplate.changePasswordMailTpl().content!!,
        )
        removeTestUser(user)
    }

    @Test
    fun resetPassword_shouldReturnNotFoundResponse_whenUserIsNotFound() =
        assertWebException(Response.Status.NOT_FOUND) { service.resetPassword(sessionContextMock, "unknown_login", null, null) }

    @Test
    fun resetPassword_shouldReturnUnauthorizedResponse_whenAuthenticatedUserDoesNotMatchLogin() {
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true)
        `when`(sessionContextMock.userPrincipal).thenReturn(BasicUserPrincipal(testUser.firstUser().login!!))
        assertWebException(Response.Status.UNAUTHORIZED) {
            service.resetPassword(sessionContextMock, "not_matching_login", null, null)
        }
    }

    @Test
    fun modifyUser_ShouldModifyUser() {
        val user = User("test2@test.com", "test", "John", "Doe", "+33616161616", null, Date(), "fr_FR", null)
            .apply { id = testUser.firstUser().id }
        service.modify(sessionContextMock, user)
    }

    @Test
    fun modifyUnknownUser_ShouldThrowNotFoundException() {
        val user = User("test3@test.com", "test", "John", "Doe", "+33616161616", null, Date(), "fr_FR", null)
            .apply { id = 9999L }
        assertWebException(Response.Status.NOT_FOUND) { service.modify(sessionContextMock, user) }
    }

    @Test
    fun modify_ShouldThrowUnauthorizedError_WhenAuthenticatedUserDoesNotMatchLogin() {
        val user = User("test2@test.com", "test", "John", "Doe", "+33616161616", null, Date(), "fr_FR", null)
        `when`(sessionContextMock.isUserInRole(JeeshopRoles.USER)).thenReturn(true)
        `when`(sessionContextMock.userPrincipal).thenReturn(BasicUserPrincipal(testUser.firstUser().login!!))
        assertWebException(Response.Status.UNAUTHORIZED) { service.modify(sessionContextMock, user) }
    }

    @Test
    fun delete_shouldRemove() {
        val user = User("test4@test.com", "test", "John", "Doe", "+33616161616", null, Date(), "fr_FR", null)
            .apply { gender = "M." }
        entityManager.transaction.begin()
        entityManager.persist(user)
        entityManager.transaction.commit()
        entityManager.transaction.begin()
        service.delete(user.id!!)
        entityManager.transaction.commit()
        assertThat(entityManager.find(User::class.java, user.id!!)).isNull()
    }

    @Test
    fun delete_NotExistingEntry_shouldThrowNotFoundEx() {
        assertWebException(Response.Status.NOT_FOUND) { service.delete(666L) }
    }

    @Test
    fun findCurrentUser_shouldReturnCurrentAuthenticatedUser() {
        `when`(sessionContextMock.userPrincipal).thenReturn(BasicUserPrincipal(testUser.firstUser().login!!))
        assertThat(service.findCurrentUser(sessionContextMock)).isEqualTo(testUser.firstUser())
    }

    private fun notActivatedTestUser(): User {
        val user = User("reset1@test.com", "password", "John", "Doe", "+33616161616", null, Date(), "fr_FR", null)
            .apply {
                gender = "M."
                actionToken = UUID.randomUUID()
                activated = false
            }
        entityManager.transaction.begin()
        entityManager.persist(user)
        entityManager.transaction.commit()
        return user
    }

    private fun removeTestUser(user: User) {
        entityManager.transaction.begin()
        entityManager.remove(user)
        entityManager.transaction.commit()
    }

    private fun assertWebException(status: Response.Status, operation: () -> Unit) {
        try {
            operation()
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(status)
            return
        }
        throw AssertionError("Expected WebApplicationException with status $status")
    }
}
