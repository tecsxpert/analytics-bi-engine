package com.internship.tool.service;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.entity.Role;
import com.internship.tool.repository.AnalyticsRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class AnalyticsRecordService {

    @Autowired
    private AnalyticsRecordRepository repository;


    @Cacheable(value = "analytics_search", key = "#keyword")
    public Page<AnalyticsRecord> search(String keyword, Pageable pageable) {
        System.out.println("Fetching search results from DB...");
        return repository.search(keyword, pageable);
    }


    @CacheEvict(value = {"analytics_search", "analytics_stats"}, allEntries = true)
    public AnalyticsRecord save(AnalyticsRecord record) {

        if (record.getRole() == null) {
            record.setRole(Role.VIEWER);
        }

        return repository.save(record);
    }


    public Page<AnalyticsRecord> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }


    public Page<AnalyticsRecord> filterByStatus(String status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }


    public List<AnalyticsRecord> getByDateRange(String start, String end) {
        return repository.findByDateRange(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }


    @Cacheable("analytics_stats")
    public Map<String, Object> getStats() {
        System.out.println("Fetching stats from DB...");

        Map<String, Object> stats = new HashMap<>();

        long total = repository.count();
        long active = repository.countByStatus("active");
        long inactive = repository.countByStatus("inactive");

        stats.put("total", total);
        stats.put("active", active);
        stats.put("inactive", inactive);

        return stats;
    }

    // 🔹 Combined filter
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