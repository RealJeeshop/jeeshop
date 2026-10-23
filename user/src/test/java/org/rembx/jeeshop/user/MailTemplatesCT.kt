package org.rembx.jeeshop.user

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import jakarta.ws.rs.core.Response
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.rembx.jeeshop.mail.Mailer
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.user.domain.Mails
import org.rembx.jeeshop.user.infrastructure.api.MailTemplates
import org.rembx.jeeshop.user.infrastructure.repository.MailTemplateRepositoryImpl
import org.rembx.jeeshop.user.domain.MailTemplate
import org.rembx.jeeshop.user.domain.User
import org.rembx.jeeshop.user.infrastructure.repository.UserPersistenceUnit
import org.rembx.jeeshop.user.test.TestMailTemplate

class MailTemplatesCT {
    private lateinit var service: MailTemplates
    private lateinit var testMailTemplate: TestMailTemplate
    private lateinit var entityManager: EntityManager
    private lateinit var mailerMock: Mailer

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
        testMailTemplate = TestMailTemplate.getInstance()
        entityManager = entityManagerFactory.createEntityManager()
        mailerMock = mock(Mailer::class.java)
        service = MailTemplates(entityManager, MailTemplateRepositoryImpl(entityManager), mailerMock)
    }

    @Test
    fun findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null, null, null, null)).isNotEmpty
    }

    @Test
    fun findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        val mailTemplates = service.findAll(null, 0, 1, null, null)
        assertThat(mailTemplates).isNotEmpty
        assertThat(mailTemplates).containsExactly(testMailTemplate.firstMailTemplate())
    }

    @Test
    fun findByName_shouldReturnMatchingMailTemplate() {
        val mailTemplates = service.findAll(testMailTemplate.firstMailTemplate().name, null, null, null, null)
        assertThat(mailTemplates).containsExactly(testMailTemplate.firstMailTemplate())
    }

    @Test
    fun find_unknownId_shouldThrowException() {
        try {
            service.find(999L)
            fail<Unit>("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun count() {
        assertThat(service.count()).isGreaterThan(0)
    }

    @Test
    fun find() {
        assertThat(service.find(1L)).isNotNull
    }

    @Test
    fun create_shouldPersist() {
        val mailTemplate = MailTemplate("TestNewsletter", "en_GB", "test content", "Test Subject")
        entityManager.transaction.begin()
        service.create(mailTemplate)
        entityManager.transaction.commit()
        assertThat(entityManager.find(MailTemplate::class.java, mailTemplate.id!!)).isNotNull
        entityManager.remove(mailTemplate)
    }

    @Test
    fun create_shouldThrowConflictException_WhenThereIsAlreadyAMailTemplateWithSameLocaleAndName() {
        val mailTemplate = MailTemplate("Newsletter1", "fr_FR", "test content", "Test Subject")
        try {
            service.create(mailTemplate)
            fail<Unit>("Should have thrown exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.CONFLICT)
        }
    }

    @Test
    fun modify_ShouldModify() {
        val detachedMailTemplate = MailTemplate("TestNewsletter2", "fr_FR", "test2 content", "Test2 Subject")
        detachedMailTemplate.id = testMailTemplate.firstMailTemplate().id
        service.modify(detachedMailTemplate)
    }

    @Test
    fun create_shouldThrowConflictException_WhenThereIsAlreadyAMailTemplateWithSameLocaleAndNameAndDifferentID() {
        val mailTemplate = MailTemplate(Mails.userRegistration.name, "fr_FR", "test content", "Test Subject")
        mailTemplate.id = testMailTemplate.firstMailTemplate().id
        try {
            service.modify(mailTemplate)
            fail<Unit>("Should have thrown exception")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.CONFLICT)
        }
    }

    @Test
    fun modifyUnknown_ShouldThrowNotFoundException() {
        val detachedMailTemplate = MailTemplate().apply { id = 9999L }
        try {
            service.modify(detachedMailTemplate)
            fail<Unit>("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun delete_shouldRemove() {
        entityManager.transaction.begin()
        val mailTemplate = MailTemplate("TestNewsletter3", "fr_FR", "test content 3", "Test Subject3")
        entityManager.persist(mailTemplate)
        entityManager.transaction.commit()
        entityManager.transaction.begin()
        service.delete(mailTemplate.id!!)
        entityManager.transaction.commit()
        assertThat(entityManager.find(MailTemplate::class.java, mailTemplate.id!!)).isNull()
    }

    @Test
    fun delete_NotExistingEntry_shouldThrowNotFoundEx() {
        try {
            entityManager.transaction.begin()
            service.delete(666L)
            entityManager.transaction.commit()
            fail<Unit>("should have thrown ex")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
        }
    }

    @Test
    fun sendTestEmail_shouldSendMailFromFTLAndPropertiesToRecipient() {
        val recipient = "toto@toto.com"
        val user = User().apply {
            gender = "Miss"
            firstname = "Jane"
            lastname = "Doe"
        }
        service.sendTestEmail(user, Mails.userRegistration.name, "fr_FR", recipient)
        verify(mailerMock).sendMail(
            testMailTemplate.userRegistrationMailTemplate().subject!!,
            recipient,
            "<html><body>Welcome Miss Jane Doe</body></html>",
        )
    }

    @Test
    fun sendTestEmail_shouldThrowNotFoundExWhenNoMailTemplateFoundForGivenParams() {
        try {
            service.sendTestEmail(null, "unknown", "fr_FR", "toto@toto.com")
        } catch (e: WebApplicationException) {
            assertThat(e.response.statusInfo).isEqualTo(Response.Status.NOT_FOUND)
            return
        }
        fail<Unit>("Should have thrown NOT_FOUND WebApplicationException")
    }
}
