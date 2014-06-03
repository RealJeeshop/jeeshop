package org.rembx.jeeshop.user;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;
import org.rembx.jeeshop.user.test.TestUser;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class UserResourceIT {

        private UserResource service;

        private TestUser testUser;
        private static EntityManagerFactory entityManagerFactory;

        @BeforeClass
        public static void beforeClass(){
            entityManagerFactory = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME);
        }

        @Before
        public void setup(){
            testUser = TestUser.getInstance();
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            service = new UserResource(entityManager, new UserFinder(entityManager));
        }


    @Test
    // TODO
    public void testFindById() throws Exception {

    }

    @Test
    // TODO
    public void testFindCurrentUser() throws Exception {

    }
}