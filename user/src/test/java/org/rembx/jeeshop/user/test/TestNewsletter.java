package org.rembx.jeeshop.user.test;

import org.rembx.jeeshop.user.model.Newsletter;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * Newsletter test utility
 */
public class TestNewsletter {

    private static TestNewsletter instance;

    private static Newsletter newsletter1;

    public static TestNewsletter getInstance() {
        if (instance != null)
            return instance;

        EntityManager entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager();

        entityManager.getTransaction().begin();

        newsletter1 = new Newsletter("Newsletter1", "<html><body>bla bla...</body></html>");

        entityManager.persist(newsletter1);

        entityManager.getTransaction().commit();

        instance = new TestNewsletter();
        entityManager.close();
        return instance;
    }


    public Newsletter firstNewsletter() {
        return newsletter1;
    }

}
