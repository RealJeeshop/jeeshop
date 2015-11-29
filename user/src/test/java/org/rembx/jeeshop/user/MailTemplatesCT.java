package org.rembx.jeeshop.user;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.user.mail.Mails;
import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.rembx.jeeshop.user.test.TestMailTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MailTemplatesCT {

    private MailTemplates service;

    private TestMailTemplate testMailTemplate;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private Mailer mailerMock;

    @BeforeClass
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME);
    }

    @Before
    public void setup() {
        testMailTemplate = TestMailTemplate.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        mailerMock = mock(Mailer.class);
        service = new MailTemplates(entityManager, new MailTemplateFinder(entityManager), mailerMock);
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null, null, null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<MailTemplate> mailTemplates = service.findAll(null, 0, 1, null, null);
        assertThat(mailTemplates).isNotEmpty();
        assertThat(mailTemplates).containsExactly(testMailTemplate.firstMailTemplate());
    }

    @Test
    public void findByName_shouldReturnMatchingMailTemplate() {
        List<MailTemplate> newsletters = service.findAll(testMailTemplate.firstMailTemplate().getName(), null, null, null, null);
        assertThat(newsletters).containsExactly(testMailTemplate.firstMailTemplate());
    }


    @Test
    public void find_unknownId_shouldThrowException() throws Exception {
        try {
            service.find(999L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void count() {
        assertThat(service.count()).isGreaterThan(0);
    }


    @Test
    public void find() throws Exception {
        assertThat(service.find(1L)).isNotNull();
    }

    @Test
    public void create_shouldPersist() {

        MailTemplate mailTemplate = new MailTemplate("TestNewsletter", "en_GB", "test content", "Test Subject");

        entityManager.getTransaction().begin();
        service.create(mailTemplate);
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(MailTemplate.class, mailTemplate.getId())).isNotNull();
        entityManager.remove(mailTemplate);
    }

    @Test
    public void create_shouldThrowConflictException_WhenThereIsAlreadyAMailTemplateWithSameLocaleAndName() {

        MailTemplate mailTemplate = new MailTemplate("Newsletter1", "fr_FR", "test content", "Test Subject");

        try {
            service.create(mailTemplate);
            fail("Should have thrown exception");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.CONFLICT);
        }

    }

    @Test
    public void modify_ShouldModify() {
        MailTemplate detachedMailTemplateToModify = new MailTemplate("TestNewsletter2", "fr_FR", "test2 content", "Test2 Subject");
        detachedMailTemplateToModify.setId(testMailTemplate.firstMailTemplate().getId());

        service.modify(detachedMailTemplateToModify);

    }


    @Test
    public void create_shouldThrowConflictException_WhenThereIsAlreadyAMailTemplateWithSameLocaleAndNameAndDifferentID() {

        MailTemplate mailTemplate = new MailTemplate(Mails.userRegistration.name(), "fr_FR", "test content", "Test Subject");
        mailTemplate.setId(testMailTemplate.firstMailTemplate().getId());

        try {
            service.modify(mailTemplate);
            fail("Should have thrown exception");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.CONFLICT);
        }

    }

    @Test
    public void modifyUnknown_ShouldThrowNotFoundException() {

        MailTemplate detachedMailTemplate = new MailTemplate();
        detachedMailTemplate.setId(9999L);
        try {
            service.modify(detachedMailTemplate);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void delete_shouldRemove() {
        entityManager.getTransaction().begin();
        MailTemplate mailTemplate = new MailTemplate("TestNewsletter3", "fr_FR", "test content 3", "Test Subject3");
        entityManager.persist(mailTemplate);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(mailTemplate.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(MailTemplate.class, mailTemplate.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx() {
        try {
            entityManager.getTransaction().begin();
            service.delete(666L);
            entityManager.getTransaction().commit();
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void sendTestEmail_shouldSendMailFromFTLAndPropertiesToRecipient() throws Exception {

        String recipient = "toto@toto.com";
        User user = new User();
        user.setGender("Miss");
        user.setFirstname("Jane");
        user.setLastname("Doe");
        service.sendTestEmail(user, Mails.userRegistration.name(), "fr_FR", recipient);

        verify(mailerMock).sendMail(testMailTemplate.userRegistrationMailTemplate().getSubject(), recipient, "<html><body>Welcome Miss Jane Doe</body></html>");
    }

    @Test
    public void sendTestEmail_shouldThrowNotFoundExWhenNoMailTemplateFoundForGivenParams() throws Exception {

        try {
            service.sendTestEmail(null, "unknown", "fr_FR", "toto@toto.com");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
            return;
        }
        fail("Should have thrown NOT_FOUND WebApplicationException");

    }


}