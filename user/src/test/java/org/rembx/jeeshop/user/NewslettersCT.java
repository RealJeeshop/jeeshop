package org.rembx.jeeshop.user;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.rest.WebApplicationException;
import org.rembx.jeeshop.user.model.Newsletter;
import org.rembx.jeeshop.user.mail.Newsletters;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.rembx.jeeshop.user.test.TestMails;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class NewslettersCT {

    private Newsletters resource;

    private TestMails testMails;
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeClass
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME);
    }

    @Before
    public void setup() {
        testMails = TestMails.getInstance();
        entityManager = entityManagerFactory.createEntityManager();
        resource = new Newsletters(entityManager, new NewsletterFinder(entityManager));
    }

    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(resource.findAll(null, null, null, null, null,null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<Newsletter> newsletters = resource.findAll(null, 0, 1, null, null,null);
        assertThat(newsletters).isNotEmpty();
        assertThat(newsletters).containsExactly(testMails.firstNewsletter());
    }

    @Test
    public void findByName_shouldReturnMatchingNewsletter() {
        List<Newsletter> newsletters = resource.findAll(testMails.firstNewsletter().getName(), null, null, null, null,null);
        assertThat(newsletters).containsExactly(testMails.firstNewsletter());
    }


    @Test
    public void find_unknownId_shouldThrowException() throws Exception {
        try {
            resource.find(999L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void count() {
        assertThat(resource.count()).isGreaterThan(0);
    }


    @Test
    public void find() throws Exception {
        assertThat(resource.find(1L)).isNotNull();
    }

    @Test
    public void create_shouldPersist() {

        Newsletter newsletter = new Newsletter("TestNewsletter", null, Date.valueOf(LocalDate.now()));

        entityManager.getTransaction().begin();
        resource.create(newsletter);
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Newsletter.class, newsletter.getId())).isNotNull();
        entityManager.remove(newsletter);
    }

    @Test
    public void modify_ShouldModify() {
        Newsletter detachedNewsletter = new Newsletter("TestNewsletter2", null, Date.valueOf(LocalDate.now()));

        detachedNewsletter.setId(testMails.firstNewsletter().getId());

        resource.modify(detachedNewsletter);

    }

    @Test
    public void modifyUnknown_ShouldThrowNotFoundException() {

        Newsletter unknown = new Newsletter();
        unknown.setId(9999L);
        try {
            resource.modify(unknown);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

    @Test
    public void delete_shouldRemove() {
        entityManager.getTransaction().begin();
        Newsletter newsletter = new Newsletter("TestNewsletter3", null, Date.valueOf(LocalDate.now()));
        entityManager.persist(newsletter);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        resource.delete(newsletter.getId());
        entityManager.getTransaction().commit();

        assertThat(entityManager.find(Newsletter.class, newsletter.getId())).isNull();
    }

    @Test
    public void delete_NotExistingEntry_shouldThrowNotFoundEx() {
        try {
            entityManager.getTransaction().begin();
            resource.delete(787L);
            entityManager.getTransaction().commit();
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatusInfo()).isEqualTo(Response.Status.NOT_FOUND);
        }
    }

}