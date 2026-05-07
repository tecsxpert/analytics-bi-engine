CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(255),
    old_data TEXT,
    new_data TEXT,
    created_at TIMESTAMP
);