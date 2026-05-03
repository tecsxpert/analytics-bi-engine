package com.internship.tool.repository;

import com.internship.tool.entity.AnalyticsRecord;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRecordRepository extends JpaRepository<AnalyticsRecord, Long> {

    List<AnalyticsRecord> findByStatus(String status);

    @Query("SELECT a FROM AnalyticsRecord a WHERE a.title LIKE %:keyword% OR a.description LIKE %:keyword%")
    List<AnalyticsRecord> search(@Param("keyword") String keyword);
}