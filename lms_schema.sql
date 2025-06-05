-- LMS Database Schema - EduElevate
-- Use existing edu_elevate schema
DROP DATABASE IF EXISTS edu_elevate;

CREATE DATABASE edu_elevate;

USE edu_elevate;

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS notification;

DROP TABLE IF EXISTS attendance;

DROP TABLE IF EXISTS quiz_attempt;

DROP TABLE IF EXISTS assignment_submission;

DROP TABLE IF EXISTS quiz_question;

DROP TABLE IF EXISTS assignment;

DROP TABLE IF EXISTS quiz;

DROP TABLE IF EXISTS enrollment;

DROP TABLE IF EXISTS lesson;

DROP TABLE IF EXISTS course;

DROP TABLE IF EXISTS instructor;

DROP TABLE IF EXISTS student;

-- Create student table
CREATE TABLE
    student (
        student_id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) UNIQUE NOT NULL,
        email VARCHAR(100) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        first_name VARCHAR(50) NOT NULL,
        last_name VARCHAR(50) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Create instructor table (extends student)
CREATE TABLE
    instructor (
        instructor_id INT AUTO_INCREMENT PRIMARY KEY,
        student_id INT UNIQUE NOT NULL,
        department VARCHAR(100),
        bio TEXT,
        specialization VARCHAR(100),
        FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE
    );

-- Create course table
CREATE TABLE
    course (
        course_id INT AUTO_INCREMENT PRIMARY KEY,
        instructor_id INT NOT NULL,
        title VARCHAR(200) NOT NULL,
        description TEXT,
        duration_weeks INT,
        max_students INT DEFAULT 50,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (instructor_id) REFERENCES instructor (instructor_id) ON DELETE CASCADE
    );

-- Create lesson table
CREATE TABLE
    lesson (
        lesson_id INT AUTO_INCREMENT PRIMARY KEY,
        course_id INT NOT NULL,
        title VARCHAR(200) NOT NULL,
        description TEXT,
        lesson_order INT NOT NULL,
        otp VARCHAR(6),
        otp_expires_at TIMESTAMP NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE CASCADE
    );

-- Create enrollment table (Many-to-Many: Students to course)
CREATE TABLE
    enrollment (
        enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
        student_id INT NOT NULL,
        course_id INT NOT NULL,
        enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        status ENUM ('ACTIVE', 'COMPLETED', 'DROPPED') DEFAULT 'ACTIVE',
        FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE,
        FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE CASCADE,
        UNIQUE KEY unique_enrollment (student_id, course_id)
    );

-- Create quiz table (Course level)
CREATE TABLE
    quiz (
        quiz_id INT AUTO_INCREMENT PRIMARY KEY,
        course_id INT NOT NULL,
        title VARCHAR(200) NOT NULL,
        description TEXT,
        total_marks INT DEFAULT 0,
        time_limit_minutes INT,
        attempts_allowed INT DEFAULT 1,
        is_randomized BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE CASCADE
    );

-- Create Quiz Questions table (Question bank per course)
CREATE TABLE
    quiz_question (
        question_id INT AUTO_INCREMENT PRIMARY KEY,
        quiz_id INT NOT NULL,
        question_text TEXT NOT NULL,
        question_type ENUM ('MCQ', 'TRUE_FALSE', 'SHORT_ANSWER') NOT NULL,
        option_a VARCHAR(500),
        option_b VARCHAR(500),
        option_c VARCHAR(500),
        option_d VARCHAR(500),
        correct_answer VARCHAR(500) NOT NULL,
        marks INT DEFAULT 1,
        FOREIGN KEY (quiz_id) REFERENCES quiz (quiz_id) ON DELETE CASCADE
    );

-- Create assignment table (Lesson level)
CREATE TABLE
    assignment (
        assignment_id INT AUTO_INCREMENT PRIMARY KEY,
        lesson_id INT NOT NULL,
        title VARCHAR(200) NOT NULL,
        description TEXT,
        total_marks INT DEFAULT 100,
        due_date TIMESTAMP,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (lesson_id) REFERENCES lesson (lesson_id) ON DELETE CASCADE
    );

-- Create Assignment Submissions table
CREATE TABLE
    assignment_submission (
        submission_id INT AUTO_INCREMENT PRIMARY KEY,
        assignment_id INT NOT NULL,
        student_id INT NOT NULL,
        file_path VARCHAR(500),
        submission_text TEXT,
        submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        grade INT,
        feedback TEXT,
        graded_at TIMESTAMP NULL,
        FOREIGN KEY (assignment_id) REFERENCES assignment (assignment_id) ON DELETE CASCADE,
        FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE,
        UNIQUE KEY unique_submission (assignment_id, student_id)
    );

-- Create Quiz Attempts table
CREATE TABLE
    quiz_attempt (
        attempt_id INT AUTO_INCREMENT PRIMARY KEY,
        quiz_id INT NOT NULL,
        student_id INT NOT NULL,
        score INT DEFAULT 0,
        total_questions INT,
        started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        completed_at TIMESTAMP NULL,
        time_taken_minutes INT,
        FOREIGN KEY (quiz_id) REFERENCES quiz (quiz_id) ON DELETE CASCADE,
        FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE
    );

-- Create Attendance table
CREATE TABLE
    attendance (
        attendance_id INT AUTO_INCREMENT PRIMARY KEY,
        lesson_id INT NOT NULL,
        student_id INT NOT NULL,
        otp_entered VARCHAR(6),
        attended BOOLEAN DEFAULT FALSE,
        attended_at TIMESTAMP NULL,
        FOREIGN KEY (lesson_id) REFERENCES lesson (lesson_id) ON DELETE CASCADE,
        FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE,
        UNIQUE KEY unique_attendance (lesson_id, student_id)
    );

-- Create notification table
CREATE TABLE
    notification (
        notification_id INT AUTO_INCREMENT PRIMARY KEY,
        student_id INT NOT NULL,
        course_id INT,
        type ENUM (
            'ENROLLMENT',
            'ASSIGNMENT',
            'QUIZ',
            'GRADES',
            'GENERAL'
        ) NOT NULL,
        title VARCHAR(200) NOT NULL,
        message TEXT NOT NULL,
        is_read BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE,
        FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE SET NULL
    );

-- Create indexes for better performance
CREATE INDEX idx_student_email ON student (email);

CREATE INDEX idx_course_instructor ON course (instructor_id);

CREATE INDEX idx_lesson_course ON lesson (course_id);

CREATE INDEX idx_enrollment_user ON enrollment (student_id);

CREATE INDEX idx_enrollment_course ON enrollment (course_id);

CREATE INDEX idx_notification_user ON notification (student_id);

CREATE INDEX idx_notification_unread ON notification (student_id, is_read);

CREATE INDEX idx_attendance_lesson ON attendance (lesson_id);

CREATE INDEX idx_quiz_attempt_user ON quiz_attempt (student_id);

COMMIT;

-- Display success message
SELECT
    'LMS Database Schema Created Successfully!' AS Status;