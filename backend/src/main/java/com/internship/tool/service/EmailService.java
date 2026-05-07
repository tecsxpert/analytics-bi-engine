package com.internship.tool.service;

import com.internship.tool.entity.AnalyticsRecord;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendOverdueEmail(AnalyticsRecord record) {
        // 🔥 TEMP (simulate email)
        System.out.println("📧 EMAIL SENT: Overdue -> " + record.getTitle());
    }
}