package org.rembx.jeeshop.user.test

import jakarta.persistence.Persistence
import org.rembx.jeeshop.user.domain.Mails
import org.rembx.jeeshop.user.domain.MailTemplate
import org.rembx.jeeshop.user.infrastructure.repository.UserPersistenceUnit

class TestMailTemplate private constructor() {
    fun firstMailTemplate(): MailTemplate = mailTemplate1

    fun userRegistrationMailTemplate(): MailTemplate = userRegistrationMailTpl

    fun resetPasswordMailTemplate(): MailTemplate = ressetPasswordMailTpl

    fun changePasswordMailTpl(): MailTemplate = changePasswordMailTpl

    companion object {
        private var instance: TestMailTemplate? = null
        private lateinit var mailTemplate1: MailTemplate
        private lateinit var userRegistrationMailTpl: MailTemplate
        private lateinit var ressetPasswordMailTpl: MailTemplate
        private lateinit var changePasswordMailTpl: MailTemplate

        @JvmStatic
        fun getInstance(): TestMailTemplate {
            instance?.let { return it }
            val entityManager = Persistence.createEntityManagerFactory(UserPersistenceUnit.NAME).createEntityManager()
            entityManager.transaction.begin()

            mailTemplate1 = MailTemplate("Newsletter1", "fr_FR", "<html><body>bla bla...</body></html>", "Hello Subject")
            userRegistrationMailTpl = MailTemplate(
                Mails.userRegistration.name,
                "fr_FR",
                "<html><body>Welcome \${gender} \${firstname} \${lastname}</body></html>",
                "New Registration Subject",
            )
            ressetPasswordMailTpl = MailTemplate(
                Mails.userResetPassword.name,
                "fr_FR",
                "<html><body>Here is the link to reset your password</body></html>",
                "Reset Password Subject",
            )
            changePasswordMailTpl = MailTemplate(
                Mails.userChangePassword.name,
                "fr_FR",
                "<html><body>Hello there, your password has changed!</body></html>",
                "Change Password Subject",
            )

            entityManager.persist(mailTemplate1)
            entityManager.persist(userRegistrationMailTpl)
            entityManager.persist(ressetPasswordMailTpl)
            entityManager.persist(changePasswordMailTpl)
            entityManager.transaction.commit()

            return TestMailTemplate().also {
                instance = it
                entityManager.close()
            }
        }
    }
}
