package com.internship.tool.service;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.repository.AnalyticsRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsRecordService {

    @Autowired
    private AnalyticsRecordRepository repository;


    @Cacheable(value = "analytics_search", key = "#keyword")
    public List<AnalyticsRecord> search(String keyword) {
        System.out.println("Fetching search results from DB...");
        return repository.search(keyword);
    }


    @Cacheable("analytics_stats")
    public Map<String, Object> getStats() {
        System.out.println("Fetching stats from DB...");

        Map<String, Object> stats = new HashMap<>();

        long total = repository.count();
        long active = repository.findByStatus("active").size();
        long inactive = repository.findByStatus("inactive").size();

        stats.put("total", total);
        stats.put("active", active);
        stats.put("inactive", inactive);

        return stats;
    }

    @CacheEvict(value = {"analytics_search", "analytics_stats"}, allEntries = true)
    public AnalyticsRecord save(AnalyticsRecord record) {
        return repository.save(record);
    }
}