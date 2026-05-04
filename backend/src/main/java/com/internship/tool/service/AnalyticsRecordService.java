package com.internship.tool.service;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.repository.AnalyticsRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class AnalyticsRecordService {

    @Autowired
    private AnalyticsRecordRepository repository;

    // 🔹 Search
    public Page<AnalyticsRecord> search(String keyword, Pageable pageable) {
        return repository.search(keyword, pageable);
    }

    // 🔹 Save
    public AnalyticsRecord save(AnalyticsRecord record) {
        return repository.save(record);
    }

    // 🔹 Get all with pagination
    public Page<AnalyticsRecord> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // 🔹 Filter by status
    public Page<AnalyticsRecord> filterByStatus(String status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    // 🔹 Date range
    public List<AnalyticsRecord> getByDateRange(String start, String end) {
        return repository.findByDateRange(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }

    // 🔹 Stats (optimized)
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        long total = repository.count();
        long active = repository.countByStatus("active");
        long inactive = repository.countByStatus("inactive");

        stats.put("total", total);
        stats.put("active", active);
        stats.put("inactive", inactive);

        return stats;
    }

    // 🔹 Combined API
    public Page<AnalyticsRecord> getFilteredData(String status, String search, Pageable pageable) {

        if (status != null && search != null) {
            return repository.findByStatusAndKeyword(status, search, pageable);
        } else if (status != null) {
            return repository.findByStatus(status, pageable);
        } else if (search != null) {
            return repository.search(search, pageable);
        } else {
            return repository.findAll(pageable);
        }
    }
}