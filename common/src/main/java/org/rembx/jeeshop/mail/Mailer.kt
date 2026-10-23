package org.rembx.jeeshop.mail

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import org.apache.commons.lang3.StringUtils
import org.rembx.jeeshop.configuration.NamedConfiguration
import java.util.Date
import java.util.Properties

@ApplicationScoped
class Mailer {
    @Inject
    @field:NamedConfiguration("mail.smtp.host")
    private lateinit var host: String

    @Inject
    @field:NamedConfiguration("mail.smtp.port")
    private lateinit var port: String

    @Inject
    @field:NamedConfiguration("mail.auth.user")
    private lateinit var user: String

    @Inject
    @field:NamedConfiguration("mail.auth.password")
    private lateinit var password: String

    @Inject
    @field:NamedConfiguration("mail.from")
    private lateinit var sender: String

    @Inject
    @field:NamedConfiguration("mail.smtp.timeout")
    private lateinit var readTimeout: String

    @Inject
    @field:NamedConfiguration("mail.smtp.connectiontimeout")
    private lateinit var connectTimeout: String

    @Inject
    @field:NamedConfiguration("debug")
    private lateinit var debug: String

    @Throws(MessagingException::class)
    fun sendMail(subject: String, to: String, content: String) {
        val props = Properties().apply {
            put("mail.smtp.host", host)
            put("mail.smtp.port", port)
            put("mail.smtp.socketFactory.port", port)
            put("mail.smtp.socketFactory.class", "jakarta.net.ssl.SSLSocketFactory")
            put("mail.smtp.connectiontimeout", connectTimeout)
            put("mail.smtp.timeout", readTimeout)
        }

        val authenticator = if (StringUtils.isNotEmpty(user)) {
            props["mail.smtp.auth"] = "true"
            object : Authenticator() {
                override fun getPasswordAuthentication() = PasswordAuthentication(user, password)
            }
        } else {
            null
        }

        val session = Session.getInstance(props, authenticator)
        session.setDebug(java.lang.Boolean.parseBoolean(this.debug))
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(this.sender))
        message.setRecipients(Message.RecipientType.TO, arrayOf(InternetAddress(to)))
        message.setSubject(subject, "UTF-8")
        message.sentDate = Date()
        val multipart = MimeMultipart("alternative")
        val htmlPart = MimeBodyPart()
        htmlPart.setContent(content, "text/html; charset=utf-8")
        multipart.addBodyPart(htmlPart)
        message.setContent(multipart)
        Transport.send(message)
    }
}
