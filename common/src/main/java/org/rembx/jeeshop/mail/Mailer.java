package org.rembx.jeeshop.mail;

import org.apache.commons.lang.StringUtils;
import org.rembx.jeeshop.configuration.NamedConfiguration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

/**
 */
@ApplicationScoped
public class Mailer {

    @Inject
    @NamedConfiguration("mail.smtp.host")
    private String host;

    @Inject
    @NamedConfiguration("mail.smtp.port")
    private String port;

    @Inject
    @NamedConfiguration("mail.auth.user")
    private String user;

    @Inject
    @NamedConfiguration("mail.auth.password")
    private String password;

    @Inject
    @NamedConfiguration("mail.from")
    private String sender;

    @Inject
    @NamedConfiguration("mail.smtp.timeout")
    private String readTimeout;

    @Inject
    @NamedConfiguration("mail.smtp.connectiontimeout")
    private String connectTimeout;

    @Inject
    @NamedConfiguration("debug")
    private String debug;

    public void sendMail(String subject, String to, String content) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.connectiontimeout",connectTimeout);
        props.put("mail.smtp.timeout",readTimeout);

        Authenticator authenticator = null;
        if (StringUtils.isNotEmpty(user)) {
            props.put("mail.smtp.auth", "true");
            authenticator = new Authenticator() {
                private PasswordAuthentication pa = new PasswordAuthentication(user, password);

                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return pa;
                }
            };
        }

        Session session = Session.getInstance(props, authenticator);
        session.setDebug(Boolean.parseBoolean(debug));

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(sender));
        InternetAddress[] address = {new InternetAddress(to)};
        message.setRecipients(Message.RecipientType.TO, address);
        message.setSubject(subject,"UTF-8");
        message.setSentDate(new Date());

        Multipart multipart = new MimeMultipart("alternative");
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(content, "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        message.setContent(multipart);

        Transport.send(message);

    }

}
