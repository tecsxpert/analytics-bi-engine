package com.internship.tool.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendRecordCreatedEmail(
            String to,
            String title,
            String status,
            String description
    ) {

        try {


            Context context = new Context();

            context.setVariable("title", title);
            context.setVariable("status", status);
            context.setVariable("description", description);

            String htmlContent =
                    templateEngine.process("record-created", context);


            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Analytics Record Created");
            helper.setText(htmlContent, true);

            // 🔹 Send
            mailSender.send(message);

            System.out.println("HTML Email sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}