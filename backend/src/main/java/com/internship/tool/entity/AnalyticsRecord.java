package com.internship.tool.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "analytics_records")
public class AnalyticsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(
            example = "Monthly Sales Report"
    )
    private String title;
    @Schema(
            example = "Analytics report for April sales"
    )
    private String description;
    @Schema(
            example = "active"
    )
    private String status;
    private Integer score;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @Enumerated(EnumType.STRING)
private Role role;
}