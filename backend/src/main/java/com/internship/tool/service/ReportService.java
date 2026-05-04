package com.internship.tool.service;

import com.internship.tool.entity.Report;
import com.internship.tool.exception.BadRequestException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.ReportRepository;
import com.internship.tool.dto.ReportRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report getById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Report not found with id: " + id));
    }

    public Report create(ReportRequest request) {

        Report report = new Report();

        report.setTitle(request.getTitle());
        report.setDescription(request.getDescription());
        report.setStatus(request.getStatus());
        report.setScore(request.getScore());

        return reportRepository.save(report);
    }

    private void validate(Report report) {

        if (report.getTitle() == null || report.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Title is required");
        }

        if (report.getStatus() == null || report.getStatus().trim().isEmpty()) {
            throw new BadRequestException("Status is required");
        }

        if (report.getScore() != null &&
                (report.getScore() < 0 || report.getScore() > 100)) {
            throw new BadRequestException("Score must be between 0 and 100");
        }
    }
    public Page<Report> getAllPaginated(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }
}