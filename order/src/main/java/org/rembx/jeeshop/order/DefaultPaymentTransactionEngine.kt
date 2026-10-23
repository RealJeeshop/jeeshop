package org.rembx.jeeshop.order

import freemarker.template.Configuration
import freemarker.template.Template
import io.quarkus.hibernate.orm.PersistenceUnit
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import org.rembx.jeeshop.catalog.infrastructure.repository.CatalogPersistenceUnit
import org.rembx.jeeshop.catalog.domain.SKU
import org.rembx.jeeshop.mail.Mailer
import org.rembx.jeeshop.order.domain.Mails
import org.rembx.jeeshop.order.domain.Order
import org.rembx.jeeshop.order.domain.OrderRepository
import org.rembx.jeeshop.order.domain.OrderStatus
import org.rembx.jeeshop.user.domain.MailTemplateRepository
import org.rembx.jeeshop.user.domain.MailTemplate
import org.slf4j.LoggerFactory
import java.io.StringWriter
import java.util.Date
import java.util.UUID

@ApplicationScoped
class DefaultPaymentTransactionEngine(
    private val orderRepository: OrderRepository,
    private val mailTemplateRepository: MailTemplateRepository,
    private val mailer: Mailer,
    @PersistenceUnit(CatalogPersistenceUnit.NAME) private val catalogEntityManager: EntityManager,
) : PaymentTransactionEngine {
    override fun processPayment(order: Order) {
        LOG.warn("Default implementation of PaymentTransactionEngine. Please provide a final PayamentTransactionEngine implementation")
        updateOrderWithPaymentInfo(order)
        updateSkusQuantities(order)
        order.computeOrderReference()
        orderRepository.enhanceOrder(order)
        sendOrderConfirmationMail(order)
    }

    private fun updateOrderWithPaymentInfo(order: Order) {
        order.status = OrderStatus.PAYMENT_VALIDATED
        order.paymentDate = Date()
        order.transactionId = UUID.randomUUID().toString()
    }

    protected fun updateSkusQuantities(order: Order) {
        order.items.orEmpty().forEach { item ->
            val sku = catalogEntityManager.find(SKU::class.java, item.skuId)
            sku.quantity = sku.quantity!! - item.quantity!!
        }
    }

    protected fun sendOrderConfirmationMail(order: Order) {
        val user = order.customer!!
        val mailTemplate: MailTemplate? =
            mailTemplateRepository.findByNameAndLocale(Mails.orderValidated.name, user.preferredLocale)
        if (mailTemplate == null) {
            LOG.warn("orderValidated e-mail template does not exist. Configure this missing template to allow user e-mail notification")
            return
        }
        try {
            val template = Template(
                Mails.orderValidated.name,
                mailTemplate.content,
                Configuration(Configuration.VERSION_2_3_21),
            )
            val mailBody = StringWriter()
            template.process(order, mailBody)
            mailer.sendMail(mailTemplate.subject!!, user.login!!, mailBody.toString())
        } catch (e: Exception) {
            LOG.error("Unable to send mail ${Mails.orderValidated} to user ${user.login}", e)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DefaultPaymentTransactionEngine::class.java)
    }
}
