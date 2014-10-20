package org.rembx.jeeshop.user.test;

import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * MailTemplate test utility
 */
public class TestMailTemplate {

    private static TestMailTemplate instance;

    private static MailTemplate mailTemplate1;

    public static TestMailTemplate getInstance() {
        if (instance != null)
            return instance;

        EntityManager entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager();

        entityManager.getTransaction().begin();

        mailTemplate1 = new MailTemplate("Newsletter1", "<html><body>bla bla...</body></html>");

        entityManager.persist(mailTemplate1);

        entityManager.getTransaction().commit();

        instance = new TestMailTemplate();
        entityManager.close();
        return instance;
    }


    public MailTemplate firstMailTemplate() {
        return mailTemplate1;
    }

}
