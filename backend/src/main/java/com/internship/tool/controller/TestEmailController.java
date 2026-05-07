package com.internship.tool.controller;

import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/test-email")
    public String testEmail() {

        emailService.sendRecordCreatedEmail(
                "test@mail.com",
                "Test Record",
                "active",
                "Testing Thymeleaf HTML email"
        );

        return "Email sent!";
    }
}