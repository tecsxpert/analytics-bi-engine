package com.internship.tool.scheduler;

import com.internship.tool.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OverdueScheduler {

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 60000)
    public void sendOverdueReminder() {

        System.out.println("Checking overdue records...");

        // TEMP demo email
        emailService.sendRecordCreatedEmail(
                "test@mail.com",
                "Overdue Analytics Record",
                "OVERDUE",
                "This analytics task is overdue and requires attention."
        );

        System.out.println("Overdue reminder email sent!");
    }
}