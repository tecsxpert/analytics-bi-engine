package com.internship.tool.service;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.entity.Role;
import com.internship.tool.repository.AnalyticsRecordRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnalyticsRecordServiceTest {

    @Mock
    private AnalyticsRecordRepository repository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AnalyticsRecordService service;

    private AnalyticsRecord record;

    @BeforeEach
    void setup() {

        record = new AnalyticsRecord();

        record.setTitle("Test Record");
        record.setDescription("Test Description");
        record.setStatus("active");
        record.setRole(Role.VIEWER);
    }
    @Test
    void testSaveRecord() {

        when(repository.save(record))
                .thenReturn(record);

        AnalyticsRecord saved =
                service.save(record);

        assertNotNull(saved);

        assertEquals(
                "Test Record",
                saved.getTitle()
        );

        verify(repository, times(1))
                .save(record);
    }
    @Test
    void testGetAllRecords() {

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<AnalyticsRecord> page =
                new PageImpl<>(
                        List.of(record)
                );

        when(repository.findAll(pageable))
                .thenReturn(page);

        Page<AnalyticsRecord> result =
                service.getAll(pageable);

        assertEquals(1, result.getTotalElements());
    }
    @Test
    void testFilterByStatus() {

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<AnalyticsRecord> page =
                new PageImpl<>(
                        List.of(record)
                );

        when(repository.findByStatus(
                "active",
                pageable
        )).thenReturn(page);

        Page<AnalyticsRecord> result =
                service.filterByStatus(
                        "active",
                        pageable
                );

        assertEquals(
                1,
                result.getTotalElements()
        );
    }
    @Test
    void testSearchRecords() {

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<AnalyticsRecord> page =
                new PageImpl<>(
                        List.of(record)
                );

        when(repository.search(
                "Test",
                pageable
        )).thenReturn(page);

        Page<AnalyticsRecord> result =
                service.search(
                        "Test",
                        pageable
                );

        assertEquals(
                1,
                result.getTotalElements()
        );
    }
    @Test
    void testGetStats() {

        when(repository.count())
                .thenReturn(10L);

        when(repository.countByStatus("active"))
                .thenReturn(7L);

        when(repository.countByStatus("inactive"))
                .thenReturn(3L);

        Map<String, Object> stats =
                service.getStats();

        assertEquals(10L, stats.get("total"));

        assertEquals(7L, stats.get("active"));

        assertEquals(3L, stats.get("inactive"));
    }
    @Test
    void testSaveAssignsDefaultRole() {

        record.setRole(null);

        when(repository.save(record))
                .thenReturn(record);

        service.save(record);

        assertEquals(
                Role.VIEWER,
                record.getRole()
        );
    }
    @Test
    void testGetFilteredDataWithStatus() {

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<AnalyticsRecord> page =
                new PageImpl<>(
                        List.of(record)
                );

        when(repository.findByStatus(
                "active",
                pageable
        )).thenReturn(page);

        Page<AnalyticsRecord> result =
                service.getFilteredData(
                        "active",
                        null,
                        pageable
                );

        assertEquals(
                1,
                result.getTotalElements()
        );
    }@Test
    void testGetFilteredDataWithSearch() {

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<AnalyticsRecord> page =
                new PageImpl<>(
                        List.of(record)
                );

        when(repository.search(
                "Test",
                pageable
        )).thenReturn(page);

        Page<AnalyticsRecord> result =
                service.getFilteredData(
                        null,
                        "Test",
                        pageable
                );

        assertEquals(
                1,
                result.getTotalElements()
        );
    }
    @Test
    void testSaveThrowsException() {

        when(repository.save(record))
                .thenThrow(
                        new RuntimeException("DB Error")
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.save(record)
                );

        assertEquals(
                "DB Error",
                exception.getMessage()
        );
    }
    @Test
    void testSearchReturnsEmpty() {

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<AnalyticsRecord> emptyPage =
                Page.empty();

        when(repository.search(
                "Unknown",
                pageable
        )).thenReturn(emptyPage);

        Page<AnalyticsRecord> result =
                service.search(
                        "Unknown",
                        pageable
                );

        assertEquals(
                0,
                result.getTotalElements()
        );
    }
}