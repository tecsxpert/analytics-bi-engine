package com.internship.tool.service;

import com.internship.tool.entity.AnalyticsRecord;
jd2-day13
import com.internship.tool.entity.Role;

import com.internship.tool.entity.Role;  // ✅ IMPORTANT
 main
import com.internship.tool.repository.AnalyticsRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

 jd2-day13
import java.io.PrintWriter;
main
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.data.domain.Page;
 jd2-day13
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Pageable;
main

@Service
public class AnalyticsRecordService {

    @Autowired
    private AnalyticsRecordRepository repository;

    // 🔹 Search
    public Page<AnalyticsRecord> search(String keyword, Pageable pageable) {
        return repository.search(keyword, pageable);
    }

 jd2-day13
    // 🔹 Save
    public AnalyticsRecord save(AnalyticsRecord record) {

        if (record.getRole() == null) {
            record.setRole(Role.VIEWER);

    // 🔹 Save (FIXED)
    public AnalyticsRecord save(AnalyticsRecord record) {

        if (record.getRole() == null) {
            record.setRole(Role.VIEWER); // default role
 main
        }

        return repository.save(record);
    }

 jd2-day13
    // 🔹 Get All
    public Page<AnalyticsRecord> getAll(
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);


    // 🔹 Get all
    public Page<AnalyticsRecord> getAll(Pageable pageable) {
 main
        return repository.findAll(pageable);
    }

    // 🔹 Filter
    public Page<AnalyticsRecord> filterByStatus(String status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

 jd2-day13
    // 🔹 Date Range
 main
    public List<AnalyticsRecord> getByDateRange(String start, String end) {
        return repository.findByDateRange(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }

    // 🔹 Stats
    public Map<String, Object> getStats() {
 jd2-day13
main
        Map<String, Object> stats = new HashMap<>();

        long total = repository.count();
        long active = repository.countByStatus("active");
        long inactive = repository.countByStatus("inactive");

        stats.put("total", total);
        stats.put("active", active);
        stats.put("inactive", inactive);

        return stats;
    }

 jd2-day13
    // 🔹 Combined Filter + Search
    public Page<AnalyticsRecord> getFilteredData(
            String status,
            String search,
            Pageable pageable
    ) {

        if (status != null && search != null) {
            return repository.findByStatusAndKeyword(status, search, pageable);

        } else if (status != null) {
            return repository.findByStatus(status, pageable);

        } else if (search != null) {
            return repository.search(search, pageable);

    // 🔹 Combined
    public Page<AnalyticsRecord> getFilteredData(String status, String search, Pageable pageable) {

        if (status != null && search != null) {
            return repository.findByStatusAndKeyword(status, search, pageable);
        } else if (status != null) {
            return repository.findByStatus(status, pageable);
        } else if (search != null) {
            return repository.search(search, pageable);
main
        } else {
            return repository.findAll(pageable);
        }
    }
 jd2-day13

    // 🔹 Export CSV
    public void exportCsv(PrintWriter writer) {

        List<AnalyticsRecord> records = repository.findAll();

        writer.println("ID,Title,Description,Status,Score");

        for (AnalyticsRecord record : records) {

            writer.println(
                    record.getId() + "," +
                    record.getTitle() + "," +
                    record.getDescription() + "," +
                    record.getStatus() + "," +
                    record.getScore()
            );
        }
    }
 main
}