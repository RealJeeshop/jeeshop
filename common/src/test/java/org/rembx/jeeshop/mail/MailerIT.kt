package org.rembx.jeeshop.mail

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetupTest
import jakarta.mail.MessagingException
import org.assertj.core.api.Assertions.assertThat
import org.jboss.weld.environment.se.Weld
import org.jboss.weld.environment.se.WeldContainer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Properties

class MailerIT {
    private lateinit var server: GreenMail

    @BeforeEach
    fun setUp() {
        val mailerProperties = Properties()
        val stream = requireNotNull(MailerIT::class.java.getResourceAsStream("/mailer.properties")) {
            "mailer.properties not found"
        }
        stream.use { mailerProperties.load(it) }

        server = GreenMail(ServerSetupTest.SMTP)
        server.setUser(
            "test@test.com",
            mailerProperties.getProperty("mail.auth.user"),
            mailerProperties.getProperty("mail.auth.password"),
        )
        server.start()
    }

    @AfterEach
    fun tearDown() {
        server.stop()
    }

    @Test
    fun sendMail() {
        val mailer = container.select(Mailer::class.java).get()
        mailer.sendMail("Test Subject", "test@test.com", "<h1>Hello</h1>")
        assertThat(server.receivedMessages).hasSize(1)
        assertThat(server.receivedMessages[0].subject).isEqualTo("Test Subject")
    }

    @Test
    fun sendMail_shouldThrowConnectTimeoutEx_WhenNoSmtpServerAvailable() {
        server.stop()
        val mailer = container.select(Mailer::class.java).get()
        try {
            mailer.sendMail("Test Subject", "test@test.com", "<h1>Hello</h1>")
            fail("should have thrown ex")
        } catch (e: MessagingException) {
            assertThat(e.message).startsWith("Couldn't connect to host")
        }
    }

    companion object {
        private lateinit var weld: Weld
        private lateinit var container: WeldContainer

        @BeforeAll
        @JvmStatic
        fun init() {
            weld = Weld()
            container = weld.initialize()
        }

        @AfterAll
        @JvmStatic
        fun close() {
            weld.shutdown()
        }
    }
}
