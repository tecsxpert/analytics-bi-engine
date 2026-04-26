package com.internship.tool.repository;

import com.internship.tool.entity.AnalyticsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRecordRepository 
        extends JpaRepository<AnalyticsRecord, Long> {

    List<AnalyticsRecord> findByStatus(String status);

}
