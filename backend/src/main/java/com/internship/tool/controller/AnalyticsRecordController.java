package com.internship.tool.controller;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.service.AnalyticsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsRecordController {

    @Autowired
    private AnalyticsRecordService service;

    @GetMapping("/search")
    public Page<AnalyticsRecord> search(
            @RequestParam String keyword,
            Pageable pageable) {
        return service.search(keyword, pageable);
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return service.getStats();
    }

    @PostMapping("/create")
    public AnalyticsRecord create(@RequestBody AnalyticsRecord record) {
        return service.save(record);
    }

    @GetMapping("/all")
    public Page<AnalyticsRecord> getAll(Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("/filter")
    public Page<AnalyticsRecord> filterByStatus(
            @RequestParam String status,
            Pageable pageable) {
        return service.filterByStatus(status, pageable);
    }

    @GetMapping("/date-range")
    public List<AnalyticsRecord> getByDateRange(
            @RequestParam String start,
            @RequestParam String end) {
        return service.getByDateRange(start, end);
    }
}