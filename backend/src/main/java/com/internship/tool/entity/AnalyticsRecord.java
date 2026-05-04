package com.internship.tool.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data   // 🔥 THIS WAS MISSING
@Entity
@Table(name = "analytics_records")
public class AnalyticsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;
    private Integer score;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 🔥 AUTO SET BEFORE INSERT
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // 🔥 AUTO UPDATE BEFORE UPDATE
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}