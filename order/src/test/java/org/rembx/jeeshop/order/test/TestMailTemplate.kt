package org.rembx.jeeshop.order.test

import jakarta.persistence.Persistence
import org.rembx.jeeshop.order.domain.Mails
import org.rembx.jeeshop.user.domain.MailTemplate
import org.rembx.jeeshop.user.infrastructure.repository.UserPersistenceUnit

class TestMailTemplate private constructor() {
    fun orderConfirmationMailTemplate(): MailTemplate = orderConfirmationTpl

    companion object {
        private var instance: TestMailTemplate? = null
        private lateinit var orderConfirmationTpl: MailTemplate

        @JvmStatic
        fun getInstance(): TestMailTemplate {
            instance?.let { return it }
            val entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager()
            entityManager.transaction.begin()

            orderConfirmationTpl = MailTemplate(
                Mails.orderValidated.name,
                "fr_FR",
                "<html><body>Hello \${gender} \${firstname} \${lastname}. Your order has been registered...</body></html>",
                "Order Confirmation",
            )
            entityManager.persist(orderConfirmationTpl)
            entityManager.transaction.commit()

            return TestMailTemplate().also {
                instance = it
                entityManager.close()
            }
        }
    }
}
