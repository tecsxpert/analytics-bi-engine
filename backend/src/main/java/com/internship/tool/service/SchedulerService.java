package com.internship.tool.service;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.repository.AnalyticsRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerService {

    @Autowired
    private AnalyticsRecordRepository repository;

    @Autowired
    private EmailService emailService;

    // 🔴 DAILY OVERDUE CHECK
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkOverdueItems() {
        System.out.println("===== OVERDUE CHECK =====");

        LocalDateTime now = LocalDateTime.now();

        List<AnalyticsRecord> records = repository.findByDueDateBefore(now);

        for (AnalyticsRecord record : records) {

            if (Boolean.FALSE.equals(record.getEmailSent())) {

                emailService.sendOverdueEmail(record);

                record.setEmailSent(true);
                repository.save(record);
            }
        }
    }

    // 🟡 DAILY UPCOMING CHECK (7 days ahead)
    @Scheduled(cron = "0 0 10 * * ?")
    public void upcomingDeadlines() {
        System.out.println("===== UPCOMING CHECK =====");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);

        List<AnalyticsRecord> records =
                repository.findByDueDateBetween(now, nextWeek);

        for (AnalyticsRecord record : records) {
            System.out.println("📅 Upcoming: " + record.getTitle());
        }
    }

    // 📊 WEEKLY SUMMARY
    @Scheduled(cron = "0 0 9 ? * MON")
    public void weeklySummary() {
        System.out.println("===== WEEKLY SUMMARY =====");

        long total = repository.count();
        System.out.println("📊 Total records = " + total);
    }
}