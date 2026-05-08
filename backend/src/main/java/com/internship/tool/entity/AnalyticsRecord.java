package com.internship.tool.entity;

import lombok.Data;
import jakarta.persistence.*;
 jd2-day14

import java.time.LocalDateTime;

@Data
@Entity
@Table(
    name = "analytics_records",
    indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_title", columnList = "title")
    }
)

import java.time.LocalDateTime;

@Data   // 🔥 THIS WAS MISSING
@Entity
@Table(name = "analytics_records")
 main
public class AnalyticsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;
    private Integer score;

 jd2-day14
    @Column(name = "due_date")
    private LocalDateTime dueDate;

 main
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

 jd2-day14
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "email_sent")
    private boolean emailSent = false;

    public boolean getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }


    // 🔥 AUTO SET BEFORE INSERT
 main
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

 jd2-day14

    // 🔥 AUTO UPDATE BEFORE UPDATE
 main
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
 jd2-day14

    public void setId(Long id) {
        this.id = id;
    }

    @Enumerated(EnumType.STRING)
private Role role;
 main
}