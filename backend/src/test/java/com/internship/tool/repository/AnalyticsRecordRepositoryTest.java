package com.internship.tool.repository;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.entity.Role;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AnalyticsRecordRepositoryTest {

    @Autowired
    private AnalyticsRecordRepository repository;

    @Test
    void testSaveAndFindByStatus() {

        AnalyticsRecord record =
                new AnalyticsRecord();

        record.setTitle("Repo Test");

        record.setDescription("Testing repository");

        record.setStatus("active");

        record.setRole(Role.VIEWER);

        repository.save(record);

        Page<AnalyticsRecord> result =
                repository.findByStatus(
                        "active",
                        PageRequest.of(0, 5)
                );

        assertFalse(result.getContent().isEmpty());

        assertEquals(
                "Repo Test",
                result.getContent().get(0).getTitle()
        );
    }
}