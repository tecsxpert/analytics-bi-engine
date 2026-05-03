package com.internship.tool.service;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.repository.AnalyticsRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsRecordService {

    @Autowired
    private AnalyticsRecordRepository repository;

    public List<AnalyticsRecord> search(String keyword) {
        return repository.search(keyword);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        long total = repository.count();
        long active = repository.findByStatus("active").size();
        long inactive = repository.findByStatus("inactive").size();

        stats.put("total", total);
        stats.put("active", active);
        stats.put("inactive", inactive);

        return stats;
    }

    public AnalyticsRecord save(AnalyticsRecord record) {
    return repository.save(record);
}
}