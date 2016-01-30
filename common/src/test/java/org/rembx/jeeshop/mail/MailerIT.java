package org.rembx.jeeshop.mail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.*;

import javax.mail.MessagingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class MailerIT {

    protected static Weld weld;
    protected static WeldContainer container;

    private GreenMail server;

    @BeforeClass
    public static void init() {
        weld = new Weld();
        container = weld.initialize();
    }

    @AfterClass
    public static void close() {
        weld.shutdown();
    }

    @Before
    public void setUp() {
        server = new GreenMail(ServerSetupTest.SMTP);
        server.start();
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void sendMail() throws Exception{
        Mailer mailer = container.instance().select(Mailer.class).get();
        try {
            mailer.sendMail("Test Subject", "test@test.com", "<h1>Hello</h1>");
        } catch (MessagingException e) {
            e.printStackTrace();
            fail();
        }

        assertThat(server.getReceivedMessages().length).isEqualTo(1);
        assertThat(server.getReceivedMessages()[0].getSubject()).isEqualTo("Test Subject");
    }

    @Test
    public void sendMail_shouldThrowConnectTimeoutEx_WhenNoSmtpServerAvailable(){
        server.stop();
        Mailer mailer = container.instance().select(Mailer.class).get();
        try {
            mailer.sendMail("Test Subject", "test@test.com", "<h1>Hello</h1>");
            fail("should have thrown ex");
        }catch (MessagingException e){
            assertThat(e.getMessage()).startsWith("Couldn't connect to host");
        }
    }

}
