package org.rembx.jeeshop.mail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class MailerIT {

    protected static Weld weld;
    protected static WeldContainer container;

    private GreenMail server;

    @BeforeAll
    public static void init() {
        weld = new Weld();
        container = weld.initialize();
    }

    @AfterAll
    public static void close() {
        weld.shutdown();
    }

    @BeforeEach
    public void setUp() throws IOException {
        Properties mailerProperties = new Properties();
        try (InputStream propertiesStream = MailerIT.class.getResourceAsStream("/mailer.properties")) {
            mailerProperties.load(propertiesStream);
        }

        server = new GreenMail(ServerSetupTest.SMTP);
        server.setUser("test@test.com", mailerProperties.getProperty("mail.auth.user"),
                mailerProperties.getProperty("mail.auth.password"));
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void sendMail() throws Exception{
        Mailer mailer = container.select(Mailer.class).get();
        try {
            mailer.sendMail("Test Subject", "test@test.com", "<h1>Hello</h1>");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertThat(server.getReceivedMessages().length).isEqualTo(1);
        assertThat(server.getReceivedMessages()[0].getSubject()).isEqualTo("Test Subject");
    }

    @Test
    public void sendMail_shouldThrowConnectTimeoutEx_WhenNoSmtpServerAvailable(){
        server.stop();
        Mailer mailer = container.select(Mailer.class).get();
        try {
            mailer.sendMail("Test Subject", "test@test.com", "<h1>Hello</h1>");
            fail("should have thrown ex");
        }catch (MessagingException e){
            assertThat(e.getMessage()).startsWith("Couldn't connect to host");
        }
    }

}
