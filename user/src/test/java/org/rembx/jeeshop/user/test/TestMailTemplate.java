package org.rembx.jeeshop.user.test;

import org.rembx.jeeshop.user.mail.Mails;
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
    private static MailTemplate userRegistrationMailTpl;

    public static TestMailTemplate getInstance() {
        if (instance != null)
            return instance;

        EntityManager entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager();

        entityManager.getTransaction().begin();

        mailTemplate1 = new MailTemplate("Newsletter1", "fr_FR", "<html><body>bla bla...</body></html>", "Hello Subject");
        userRegistrationMailTpl = new MailTemplate(Mails.userRegistration.name(), "fr_FR", "<html><body>Welcome ${gender} ${firstname} ${lastname}</body></html>", "New Registration Subject");

        entityManager.persist(mailTemplate1);
        entityManager.persist(userRegistrationMailTpl);

        entityManager.getTransaction().commit();

        instance = new TestMailTemplate();
        entityManager.close();
        return instance;
    }


    public MailTemplate firstMailTemplate() {
        return mailTemplate1;
    }

    public MailTemplate userRegistrationMailTemplate(){
        return userRegistrationMailTpl;
    }

}
