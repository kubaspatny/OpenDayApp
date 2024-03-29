package cz.kubaspatny.opendayapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Author: Kuba Spatny
 * Web: kubaspatny.cz
 * E-mail: kuba.spatny@gmail.com
 * Date: 16/11/2014
 * Time: 22:52
 * Copyright 2014 Jakub Spatny
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Email Service used for asynchronous sending of emails.
 */

@Service("emailService")
public class EmailService {

    @Value("${email.username}") private String email_username;
    @Value("${email.password}") private String email_password;
    @Value("${email.host}") private String email_host;

    /**
     * Sends email with user login credentials.
     * @param username  credentials login
     * @param email     email address to send the email to
     * @param password  credentials password
     */
    @Async
    public void sendCredentials(String username, String email, String password) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", email_host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email_username, email_password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email_username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Registrace Open Days");
            message.setContent("<h1>Váš email byl registrován do aplikace Open Days</h1>" +
                    "<p>Přihlásit se můžete s na adrese dod.felk.cvut.cz.</p>" +
                    "<p><b>Uživatelské jméno: </b>" + username + "</p>" +
                    "<p><b>Heslo: </b>" + password + "</p>" +
                    "<br><br><p>Heslo si můžete změnit po přihlášení!</p>", "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends recovery email
     * @param email email address
     * @param token recovery token
     * @param userId user's id
     */
    @Async
    public void sendForgotEmail(String email, String token, Long userId){

        String recoveryLink = "http://dod.felk.cvut.cz/recover.xhtml?token=" + token + "&id=" + userId;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", email_host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email_username, email_password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email_username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Obnovení hesla");
            message.setContent("<h1>Vypadá to, že jste zapomněli heslo..</h1>" +
                    "<p>Pro obnovení hesla klikněte <a href=\"" + recoveryLink + "\">" + "zde</a>." +
                    "<br><br><p>V případě, že jste nezažádali o obnovení hesla, tento email ignorujte.</p>", "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

}
