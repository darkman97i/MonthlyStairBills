package com.monthlystairbills.email;

import com.monthlystairbills.model.BillConfig;
import com.monthlystairbills.model.EmailConfig;
import com.monthlystairbills.model.Recipient;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.nio.file.Path;
import java.util.Properties;

public class EmailSender {

    private final EmailConfig emailConfig;
    private final Session session;

    public EmailSender(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
        this.session = buildSession();
    }

    public void send(Path pdfPath, int billNumber, BillConfig config, Recipient recipient) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailConfig.getUsername()));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getEmail()));
        message.setSubject(buildSubject(billNumber, config, recipient), "UTF-8");

        // Body part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(buildBody(billNumber, config, recipient), "UTF-8");

        // Attachment part
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setDataHandler(new DataHandler(new FileDataSource(pdfPath.toFile())));
        attachmentPart.setFileName(pdfPath.getFileName().toString());

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);
        message.setContent(multipart);

        Transport.send(message);
    }

    private String buildSubject(int billNumber, BillConfig config, Recipient recipient) {
        return applyPlaceholders(emailConfig.getSubjectTemplate(), billNumber, config, recipient);
    }

    private String buildBody(int billNumber, BillConfig config, Recipient recipient) {
        String raw = applyPlaceholders(emailConfig.getBodyTemplate(), billNumber, config, recipient);
        // Convert literal \n written in the properties file into real line breaks
        return raw.replace("\\n", "\n");
    }

    private String applyPlaceholders(String template, int billNumber, BillConfig config, Recipient recipient) {
        return template
                .replace("{name}",      recipient.getName())
                .replace("{number}",    String.valueOf(billNumber))
                .replace("{year}",      String.valueOf(config.getYear()))
                .replace("{concept}",   recipient.getConcept())
                .replace("{amount}",    String.valueOf(recipient.getQuantityInEuros()))
                .replace("{community}", config.getCommunityName());
    }

    private Session buildSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", emailConfig.getHost());
        props.put("mail.smtp.port", String.valueOf(emailConfig.getPort()));
        props.put("mail.smtp.auth", String.valueOf(emailConfig.isAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(emailConfig.isStarttls()));

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
            }
        });
    }
}
