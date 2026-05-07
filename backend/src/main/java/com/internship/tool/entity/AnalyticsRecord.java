package com.internship.tool.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

 jd2-day12
@Data

@Data   // 🔥 THIS WAS MISSING
 main
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

jd2-day12
    @Column(name = "due_date")
    private LocalDateTime dueDate;

 main
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

jd2-day12
    @Enumerated(EnumType.STRING)
    private Role role;

    // 🔥 NEW FIELD (IMPORTANT)
    @Column(name = "email_sent")
    private boolean emailSent = false;


public Boolean getEmailSent() {
    return emailSent;
}

public void setEmailSent(Boolean emailSent) {
    this.emailSent = emailSent;
}

 main
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
 jd2-day12

    public void setId(Long id) {
    this.id = id;
}

    @Enumerated(EnumType.STRING)
private Role role;
 main
}