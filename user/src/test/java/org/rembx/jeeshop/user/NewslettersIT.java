package org.rembx.jeeshop.user;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.user.model.Newsletter;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.rembx.jeeshop.user.test.TestNewsletter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

public class NewslettersIT {

    private Newsletters service;

    private TestNewsletter testNewsletter;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeClass
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME);
    }

    @Before
    public void setup() {
        testNewsletter = TestNewsletter.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        service = new Newsletters(entityManager, new NewsletterFinder(entityManager));
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Newsletter> newsletters = service.findAll( 0, 1);
        assertThat(newsletters).isNotEmpty();
        assertThat(newsletters).containsExactly(testNewsletter.firstNewsletter());
    }

    @Test
    public void findByName_shouldReturnMatchingNewsletter() {
        Newsletter newsletters = service.findByName(testNewsletter.firstNewsletter().getName());
        assertThat(newsletters).isEqualTo(testNewsletter.firstNewsletter());
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

        Newsletter newsletter = new Newsletter("TestNewsletter","test content");

        entityManager.getTransaction().begin();
        service.create(newsletter);
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Newsletter.class, newsletter.getId())).isNotNull();
        entityManager.remove(newsletter);
    }

    @Test
    public void modify_ShouldModify() {
        Newsletter detachedNewsletterToModify = new Newsletter("TestNewsletter2","test2 content");
        detachedNewsletterToModify.setId(testNewsletter.firstNewsletter().getId());

        service.modify(detachedNewsletterToModify);

    }

    @Test
    public void modifyUnknown_ShouldThrowNotFoundException() {

        Newsletter detachedNewsletter = new Newsletter();
        detachedNewsletter.setId(9999L);
        try {
            service.modify(detachedNewsletter);
            fail("should have thrown ex");
        }catch (WebApplicationException e){
            assertThat(e.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void delete_shouldRemove(){
        entityManager.getTransaction().begin();
        Newsletter newsletter = new Newsletter("TestNewsletter3","test content 3");
        entityManager.persist(newsletter);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        service.delete(newsletter.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Newsletter.class, newsletter.getId())).isNull();
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