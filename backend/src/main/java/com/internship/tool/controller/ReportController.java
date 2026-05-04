package com.internship.tool.controller;

import com.internship.tool.dto.ReportRequest;
import com.internship.tool.entity.Report;
import com.internship.tool.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Report>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        return ResponseEntity.ok(
                reportService.getAllPaginated(
                        PageRequest.of(page, size, Sort.by(sortBy))
                )
        );
    }

    @PostMapping("/create")
    public ResponseEntity<Report> create(@Valid @RequestBody ReportRequest request) {
        Report created = reportService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}