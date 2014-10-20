package org.rembx.jeeshop.user;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.rembx.jeeshop.user.test.TestMailTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

public class MailTemplatesIT {

    private MailTemplates service;

    private TestMailTemplate testMailTemplate;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeClass
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME);
    }

    @Before
    public void setup() {
        testMailTemplate = TestMailTemplate.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        service = new MailTemplates(entityManager, new MailTemplateFinder(entityManager));
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<MailTemplate> mailTemplates = service.findAll( 0, 1);
        assertThat(mailTemplates).isNotEmpty();
        assertThat(mailTemplates).containsExactly(testMailTemplate.firstMailTemplate());
    }

    @Test
    public void findByName_shouldReturnMatchingMailTemplate() {
        MailTemplate newsletters = service.findByName(testMailTemplate.firstMailTemplate().getName());
        assertThat(newsletters).isEqualTo(testMailTemplate.firstMailTemplate());
    }


    @Test
    public void find_unknownId_shouldThrowException() throws Exception {
        try {
            service.find(999L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void count(){
        assertThat(service.count()).isGreaterThan(0);
    }


    @Test
    public void find() throws Exception {
        assertThat(service.find(1L)).isNotNull();
    }

    @Test
    public void create_shouldPersist(){

        MailTemplate mailTemplate = new MailTemplate("TestNewsletter","en_GB","test content","Test Subject");

        entityManager.getTransaction().begin();
        service.create(mailTemplate);
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(MailTemplate.class, mailTemplate.getId())).isNotNull();
        entityManager.remove(mailTemplate);
    }

    @Test
    public void modify_ShouldModify() {
        MailTemplate detachedMailTemplateToModify = new MailTemplate("TestNewsletter2","fr_FR", "test2 content", "Test2 Subject");
        detachedMailTemplateToModify.setId(testMailTemplate.firstMailTemplate().getId());

        service.modify(detachedMailTemplateToModify);

    }

    @Test
    public void modifyUnknown_ShouldThrowNotFoundException() {

        MailTemplate detachedMailTemplate = new MailTemplate();
        detachedMailTemplate.setId(9999L);
        try {
            service.modify(detachedMailTemplate);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void delete_shouldRemove(){
        entityManager.getTransaction().begin();
        MailTemplate mailTemplate = new MailTemplate("TestNewsletter3","fr_FR","test content 3","Test Subject3");
        entityManager.persist(mailTemplate);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(mailTemplate.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(MailTemplate.class, mailTemplate.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx(){
        try {
            entityManager.getTransaction().begin();
            service.delete(666L);
            entityManager.getTransaction().commit();
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        }
    }

}