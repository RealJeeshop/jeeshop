package org.rembx.jeeshop.order;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.quarkus.hibernate.orm.PersistenceUnit;
import org.rembx.jeeshop.catalog.model.CatalogPersistenceUnit;
import org.rembx.jeeshop.catalog.model.SKU;
import org.rembx.jeeshop.mail.Mailer;
import org.rembx.jeeshop.order.model.Order;
import org.rembx.jeeshop.order.model.OrderStatus;
import org.rembx.jeeshop.user.MailTemplateFinder;
import org.rembx.jeeshop.user.model.MailTemplate;
import org.rembx.jeeshop.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;

import static org.rembx.jeeshop.order.mail.Mails.orderValidated;

/**
 * Default implementation of PaymentTransactionEngine
 */
@RequestScoped
public class DefaultPaymentTransactionEngine implements PaymentTransactionEngine {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultPaymentTransactionEngine.class);

    private OrderFinder orderFinder;
    private MailTemplateFinder mailTemplateFinder;
    private Mailer mailer;

    private EntityManager catalogEntityManager;

    DefaultPaymentTransactionEngine( OrderFinder orderFinder, MailTemplateFinder mailTemplateFinder, Mailer mailer,
                                     @PersistenceUnit(CatalogPersistenceUnit.NAME) EntityManager catalogEntityManager) {
        this.orderFinder = orderFinder;
        this.mailTemplateFinder = mailTemplateFinder;
        this.mailer = mailer;
        this.catalogEntityManager = catalogEntityManager;
    }

    @Override
    public void processPayment(Order order) {

        LOG.warn("Default implementation of PaymentTransactionEngine. Please provide a final PayamentTransactionEngine implementation");

        updateOrderWithPaymentInfo(order);

        updateSkusQuantities(order);

        order.computeOrderReference();

        orderFinder.enhanceOrder(order);

        sendOrderConfirmationMail(order);

    }

    private void updateOrderWithPaymentInfo(Order order) {
        order.setStatus(OrderStatus.PAYMENT_VALIDATED);
        order.setPaymentDate(new Date());
        order.setTransactionId(UUID.randomUUID().toString());
    }

    protected void updateSkusQuantities(Order order) {

        order.getItems().forEach(orderItem -> {
            SKU sku = catalogEntityManager.find(SKU.class, (orderItem).getSkuId());
            sku.setQuantity(sku.getQuantity() - (orderItem).getQuantity());
        });
    }

    protected void sendOrderConfirmationMail(Order order) {

        User user = order.getUser();

        MailTemplate mailTemplate = mailTemplateFinder.findByNameAndLocale(orderValidated.name(), user.getPreferredLocale());

        if (mailTemplate == null){
            LOG.warn("orderValidated e-mail template does not exist. Configure this missing template to allow user e-mail notification");
            return;
        }

        try {
            Template mailContentTpl = new Template(orderValidated.name(), mailTemplate.getContent(), new Configuration(Configuration.VERSION_2_3_21));
            final StringWriter mailBody = new StringWriter();
            mailContentTpl.process(order, mailBody);
            mailer.sendMail(mailTemplate.getSubject(), user.getLogin(), mailBody.toString());
        } catch (Exception e) {
            LOG.error("Unable to send mail " + orderValidated + " to user " + user.getLogin(), e);
        }
    }
}
