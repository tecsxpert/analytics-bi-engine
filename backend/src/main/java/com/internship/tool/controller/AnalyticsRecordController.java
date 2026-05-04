package com.internship.tool.controller;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.service.AnalyticsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsRecordController {

    @Autowired
    private AnalyticsRecordService service;

    @GetMapping("/search")
    public List<AnalyticsRecord> search(@RequestParam String q) {
        return service.search(q);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return service.getStats();
    }
@PostMapping("/create")
public AnalyticsRecord create(@RequestBody AnalyticsRecord record) {
    return service.save(record);
}
}