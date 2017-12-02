/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.ejb;

import app.entity.Usuario;
import java.util.List;
import java.util.Properties;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author johncarlo
 */
@Stateless
@LocalBean
public class GmailBean {

    private final String username = "xagendamlg@gmail.com";
    private final String password = "agendamlg1234";

    public void sendMail(List<Usuario> to, String subject, String msg) {
        if (!to.isEmpty()) {
            Properties properties = new Properties();
            properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            properties.put("mail.smtp.auth", true);
            properties.put("mail.smtp.starttls.enable", true);
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                InternetAddress recipients[] = new InternetAddress[to.size()];
                for (int i = 0; i < recipients.length; i++) {
                    recipients[i] = InternetAddress.parse(to.get(i).getEmail())[0];
                }
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("no-reply@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, recipients);
                message.setSubject(subject);
                message.setText(msg);

                Transport.send(message);

                System.out.println("Mail Sent");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
