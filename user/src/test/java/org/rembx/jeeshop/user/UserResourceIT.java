package org.rembx.jeeshop.user;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.user.model.User;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.rembx.jeeshop.user.test.TestUser;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

public class UserResourceIT {

    private UserResource service;

    private TestUser testUser;
    private static EntityManagerFactory entityManagerFactory;

    @BeforeClass
    public static void beforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME);
    }

    @Before
    public void setup() {
        testUser = TestUser.getInstance();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        service = new UserResource(entityManager, new UserFinder(entityManager));
    }


    @Test
    public void findAll_shouldReturnNoneEmptyList() {
        assertThat(service.findAll(null, null)).isNotEmpty();
    }

    @Test
    public void findAll_withPagination_shouldReturnNoneEmptyListPaginated() {
        List<User> users = service.findAll(0, 1);
        assertThat(users).isNotEmpty();
        assertThat(users).containsExactly(testUser.firstUser());
    }

    @Test
    public void findById_ShouldThrowException() throws Exception {
        try {
            service.findById(999L);
            fail("should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void findById() throws Exception {
        assertThat(service.findById(1L)).isNotNull();
    }

    @Test
    // TODO
    public void testFindCurrentUser() throws Exception {
    }
}