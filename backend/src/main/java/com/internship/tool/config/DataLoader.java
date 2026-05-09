package com.internship.tool.config;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.entity.Role;
import com.internship.tool.repository.AnalyticsRecordRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class DataLoader {

    @Autowired
    private AnalyticsRecordRepository repository;

    private final Random random =
            new Random();

    @PostConstruct
    public void seedData() {

        if (repository.count() > 0) {
            return;
        }

        List<String> statuses =
                List.of(
                        "active",
                        "inactive",
                        "pending"
                );

        for (int i = 1; i <= 30; i++) {

            AnalyticsRecord record =
                    new AnalyticsRecord();

            record.setTitle(
                    "Analytics Report " + i
            );

            record.setDescription(
                    "Demo analytics description " + i
            );

            record.setStatus(
                    statuses.get(
                            random.nextInt(
                                    statuses.size()
                            )
                    )
            );

            record.setScore(
                    random.nextInt(101)
            );

            record.setRole(Role.VIEWER);

            repository.save(record);
        }

        System.out.println(
                "Seeded 30 demo analytics records"
        );
    }
}