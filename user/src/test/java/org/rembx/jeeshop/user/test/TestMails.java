package org.rembx.jeeshop.user.test;

import org.rembx.jeeshop.user.mail.Mails;
import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.Newsletter;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * MailTemplate test utility
 */
public class TestMails {

    private static TestMails instance;

    private static MailTemplate mailTemplate1;
    private static MailTemplate userRegistrationMailTpl;
    private static MailTemplate ressetPasswordMailTpl;
    private static MailTemplate changePasswordMailTpl;

    private static Newsletter newsletter1;

    public static TestMails getInstance() {
        if (instance != null)
            return instance;

        EntityManager entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager();

        entityManager.getTransaction().begin();

        mailTemplate1 = new MailTemplate("MailTemplate1", "fr_FR", "<html><body>bla bla...</body></html>", "Hello Subject");
        newsletter1 = new Newsletter("Newsletter1", mailTemplate1, Timestamp.valueOf(LocalDateTime.now().plusYears(1)));
        userRegistrationMailTpl = new MailTemplate(Mails.userRegistration.name(), "fr_FR", "<html><body>Welcome ${gender} ${firstname} ${lastname}</body></html>", "New Registration Subject");
        ressetPasswordMailTpl = new MailTemplate(Mails.userResetPassword.name(), "fr_FR", "<html><body>Here is the link to reset your password</body></html>", "Reset Password Subject");
        changePasswordMailTpl = new MailTemplate(Mails.userChangePassword.name(), "fr_FR", "<html><body>Hello there, your password has changed!</body></html>", "Change Password Subject");

        entityManager.persist(mailTemplate1);
        entityManager.persist(newsletter1);
        entityManager.persist(userRegistrationMailTpl);
        entityManager.persist(ressetPasswordMailTpl);
        entityManager.persist(changePasswordMailTpl);


        entityManager.getTransaction().commit();

        instance = new TestMails();
        entityManager.close();

        return instance;
    }


    public MailTemplate firstMailTemplate() {
        return mailTemplate1;
    }

    public Newsletter firstNewsletter() {
        return newsletter1;
    }

    public MailTemplate userRegistrationMailTemplate(){
        return userRegistrationMailTpl;
    }

    public MailTemplate resetPasswordMailTemplate(){
        return ressetPasswordMailTpl;
    }

    public MailTemplate changePasswordMailTpl(){
        return changePasswordMailTpl;
    }
}
