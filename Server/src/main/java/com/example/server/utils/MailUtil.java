package com.example.server.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendEmail(
            String to, String subject, String body) {
        new Thread(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromMail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }).start();
    }

    public String resetPassMailTemplate(String recipientName, String confirmationCode) {
        return "Dear " + recipientName + ",\n\n" +
                "We are sending you the confirmation code to complete the password reset process for your account. This code helps " +
                "secure your account and ensures that only you have access.\n\n" +
                "Your confirmation code is: " + confirmationCode + "\n\n" +
                "Please use this code within 10 minutes to complete the password reset process for your account.\n\n" +
                "If you did not request this code, please contact us immediately so we can assist you.\n\n" +
                "Thank you.\n\n" +
                "Sincerely,\n" +
                "E-learning UTC" + "\n" +
                "elearningutc2025@gmail.com";
    }
}
