package com.internship.tool.controller;

import com.internship.tool.entity.AnalyticsRecord;
import com.internship.tool.service.AnalyticsRecordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsRecordController {

    @Autowired
    private AnalyticsRecordService service;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @Operation(
            summary = "Search analytics records",
            description = "Search records using keyword"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully"
    )
    @GetMapping("/search")
    public Page<AnalyticsRecord> search(
            @RequestParam String keyword,
            Pageable pageable) {
        return service.search(keyword, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get analytics statistics",
            description = "Returns analytics dashboard statistics"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Statistics fetched successfully"
    )
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return service.getStats();
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(
            summary = "Create analytics record",
            description = "Creates a new analytics record"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Record created successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
    )
    @PostMapping("/create")
    public AnalyticsRecord create(@RequestBody AnalyticsRecord record) {
        return service.save(record);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @Operation(
            summary = "Get all analytics records",
            description = "Returns paginated analytics records"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Records fetched successfully"
    )
    @GetMapping("/all")
    public Page<AnalyticsRecord> getAll(Pageable pageable) {
        return service.getAll(pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(
            summary = "Filter analytics records",
            description = "Filter records by status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Filter applied successfully"
    )
    @GetMapping("/filter")
    public Page<AnalyticsRecord> filterByStatus(
            @RequestParam String status,
            Pageable pageable) {
        return service.filterByStatus(status, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VIEWER')")
    @Operation(
            summary = "Get records by date range",
            description = "Returns analytics records between start and end dates"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Records fetched successfully"
    )
    @GetMapping("/date-range")
    public List<AnalyticsRecord> getByDateRange(
            @RequestParam String start,
            @RequestParam String end) {
        return service.getByDateRange(start, end);
    }
}