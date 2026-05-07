package com.internship.tool.controller;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.service.AnalyticsRecordService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsRecordController {

    private final AnalyticsRecordService service;

    @Autowired
    public AnalyticsRecordController(AnalyticsRecordService service) {
        this.service = service;
    }

    // 🔍 SEARCH → all roles
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @GetMapping("/search")
    public Page<AnalyticsRecord> search(
            @RequestParam String keyword,
            Pageable pageable) {

        return service.search(keyword, pageable);
    }

    // 📊 STATS → ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public Map<String, Object> getStats() {

        return service.getStats();
    }

    // ➕ CREATE → ADMIN & MANAGER
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public AnalyticsRecord create(@RequestBody AnalyticsRecord record) {

        return service.save(record);
    }

    // 📄 GET ALL → all roles
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @GetMapping("/all")
    public Page<AnalyticsRecord> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        return service.getAll(page, size, sortBy, sortDir);
    }

    // 🔎 FILTER → ADMIN & MANAGER
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/filter")
    public Page<AnalyticsRecord> filterByStatus(
            @RequestParam String status,
            Pageable pageable) {

        return service.filterByStatus(status, pageable);
    }

    // 📅 DATE RANGE → all roles
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @GetMapping("/date-range")
    public List<AnalyticsRecord> getByDateRange(
            @RequestParam String start,
            @RequestParam String end) {

        return service.getByDateRange(start, end);
    }

    @GetMapping("/export")
public void exportCsv(HttpServletResponse response) throws Exception {

    response.setContentType("text/csv");

    response.setHeader(
            "Content-Disposition",
            "attachment; filename=analytics.csv"
    );

    service.exportCsv(response.getWriter());
}
}