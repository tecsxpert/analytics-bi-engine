package com.internship.tool.repository;

import com.internship.tool.entity.AnalyticsRecord;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRecordRepository extends JpaRepository<AnalyticsRecord, Long> {

    // 🔹 Basic filter
    Page<AnalyticsRecord> findByStatus(String status, Pageable pageable);

    // 🔹 Search
    @Query("SELECT a FROM AnalyticsRecord a WHERE a.title LIKE %:keyword% OR a.description LIKE %:keyword%")
    Page<AnalyticsRecord> search(@Param("keyword") String keyword, Pageable pageable);

    // 🔹 Combined filter + search
    @Query("SELECT a FROM AnalyticsRecord a WHERE a.status = :status AND (a.title LIKE %:keyword% OR a.description LIKE %:keyword%)")
    Page<AnalyticsRecord> findByStatusAndKeyword(
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    @Query("SELECT a FROM AnalyticsRecord a WHERE a.createdAt BETWEEN :start AND :end")
List<AnalyticsRecord> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end);

    // 🔹 Stats optimization
    long countByStatus(String status);
}