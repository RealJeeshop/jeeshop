package org.rembx.jeeshop.order.test;

import org.rembx.jeeshop.order.mail.Mails;
import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.UserPersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * MailTemplate test utility
 */
public class TestMailTemplate {

    private static TestMailTemplate instance;

    private static MailTemplate orderConfirmationTpl;


    public static TestMailTemplate getInstance() {
        if (instance != null)
            return instance;

        EntityManager entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager();

        entityManager.getTransaction().begin();

        orderConfirmationTpl = new MailTemplate(Mails.OrderConfirmation.name(), "fr_FR", "<html><body>Hello ${gender} ${firstname} ${lastname}. Your order has been registered...</body></html>", "Order Confirmation");

        entityManager.persist(orderConfirmationTpl);

        entityManager.getTransaction().commit();

        instance = new TestMailTemplate();
        entityManager.close();
        return instance;
    }

    public MailTemplate orderConfirmationMailTemplate(){
        return orderConfirmationTpl;
    }
}
