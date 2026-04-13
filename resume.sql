CREATE DATABASE IF NOT EXISTS resume_builder;
USE resume_builder;

CREATE TABLE resumes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    resume_data JSON NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uniq_user (user_name)
);

select * from resumes;