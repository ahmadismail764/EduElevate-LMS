-- LMS Database Schema - EduElevate
-- Use existing edu_elevate schema
DROP DATABASE IF EXISTS edu_elevate;

CREATE DATABASE edu_elevate;

USE edu_elevate;

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS notifications;

DROP TABLE IF EXISTS attendance;

DROP TABLE IF EXISTS quiz_attempts;

DROP TABLE IF EXISTS assignment_submissions;

DROP TABLE IF EXISTS quiz_questions;

DROP TABLE IF EXISTS assignments;

DROP TABLE IF EXISTS quizzes;

DROP TABLE IF EXISTS enrollments;

DROP TABLE IF EXISTS lessons;

DROP TABLE IF EXISTS courses;

DROP TABLE IF EXISTS instructors;

DROP TABLE IF EXISTS users;

-- Create Users table
CREATE TABLE
    users (
        user_id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) UNIQUE NOT NULL,
        email VARCHAR(100) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        first_name VARCHAR(50) NOT NULL,
        last_name VARCHAR(50) NOT NULL,
        role ENUM ('ADMIN', 'INSTRUCTOR', 'STUDENT') NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Create Instructors table (extends users)
CREATE TABLE
    instructors (
        instructor_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT UNIQUE NOT NULL,
        department VARCHAR(100),
        bio TEXT,
        specialization VARCHAR(100),
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
    );

-- Create Courses table
CREATE TABLE
    courses (
        course_id INT AUTO_INCREMENT PRIMARY KEY,
        instructor_id INT NOT NULL,
        title VARCHAR(200) NOT NULL,
        description TEXT,
        duration_weeks INT,
        max_students INT DEFAULT 50,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (instructor_id) REFERENCES instructors (instructor_id) ON DELETE CASCADE
    );

-- Create Lessons table
CREATE TABLE
    lessons (
        lesson_id INT AUTO_INCREMENT PRIMARY KEY,
        course_id INT NOT NULL,
        title VARCHAR(200) NOT NULL,
        description TEXT,
        lesson_order INT NOT NULL,
        otp VARCHAR(6),
        otp_expires_at TIMESTAMP NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (course_id) REFERENCES courses (course_id) ON DELETE CASCADE
    );

-- Create Enrollments table (Many-to-Many: Students to Courses)
CREATE TABLE
    enrollments (
        enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        course_id INT NOT NULL,
        enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        status ENUM ('ACTIVE', 'COMPLETED', 'DROPPED') DEFAULT 'ACTIVE',
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
        FOREIGN KEY (course_id) REFERENCES courses (course_id) ON DELETE CASCADE,
        UNIQUE KEY unique_enrollment (user_id, course_id)
    );

-- Create Quizzes table (Course level)
CREATE TABLE
    quizzes (
        quiz_id INT AUTO_INCREMENT PRIMARY KEY,
        course_id INT NOT NULL,
        title VARCHAR(200) NOT NULL,
        description TEXT,
        total_marks INT DEFAULT 0,
        time_limit_minutes INT,
        attempts_allowed INT DEFAULT 1,
        is_randomized BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (course_id) REFERENCES courses (course_id) ON DELETE CASCADE
    );

-- Create Quiz Questions table (Question bank per course)
CREATE TABLE
    quiz_questions (
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
        FOREIGN KEY (quiz_id) REFERENCES quizzes (quiz_id) ON DELETE CASCADE
    );

-- Create Assignments table (Lesson level)
CREATE TABLE
    assignments (
        assignment_id INT AUTO_INCREMENT PRIMARY KEY,
        lesson_id INT NOT NULL,
        title VARCHAR(200) NOT NULL,
        description TEXT,
        total_marks INT DEFAULT 100,
        due_date TIMESTAMP,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (lesson_id) REFERENCES lessons (lesson_id) ON DELETE CASCADE
    );

-- Create Assignment Submissions table
CREATE TABLE
    assignment_submissions (
        submission_id INT AUTO_INCREMENT PRIMARY KEY,
        assignment_id INT NOT NULL,
        user_id INT NOT NULL,
        file_path VARCHAR(500),
        submission_text TEXT,
        submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        grade INT,
        feedback TEXT,
        graded_at TIMESTAMP NULL,
        FOREIGN KEY (assignment_id) REFERENCES assignments (assignment_id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
        UNIQUE KEY unique_submission (assignment_id, user_id)
    );

-- Create Quiz Attempts table
CREATE TABLE
    quiz_attempts (
        attempt_id INT AUTO_INCREMENT PRIMARY KEY,
        quiz_id INT NOT NULL,
        user_id INT NOT NULL,
        score INT DEFAULT 0,
        total_questions INT,
        started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        completed_at TIMESTAMP NULL,
        time_taken_minutes INT,
        FOREIGN KEY (quiz_id) REFERENCES quizzes (quiz_id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
    );

-- Create Attendance table
CREATE TABLE
    attendance (
        attendance_id INT AUTO_INCREMENT PRIMARY KEY,
        lesson_id INT NOT NULL,
        user_id INT NOT NULL,
        otp_entered VARCHAR(6),
        attended BOOLEAN DEFAULT FALSE,
        attended_at TIMESTAMP NULL,
        FOREIGN KEY (lesson_id) REFERENCES lessons (lesson_id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
        UNIQUE KEY unique_attendance (lesson_id, user_id)
    );

-- Create Notifications table
CREATE TABLE
    notifications (
        notification_id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
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
        FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
        FOREIGN KEY (course_id) REFERENCES courses (course_id) ON DELETE SET NULL
    );

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users (email);

CREATE INDEX idx_users_role ON users (role);

CREATE INDEX idx_courses_instructor ON courses (instructor_id);

CREATE INDEX idx_lessons_course ON lessons (course_id);

CREATE INDEX idx_enrollments_user ON enrollments (user_id);

CREATE INDEX idx_enrollments_course ON enrollments (course_id);

CREATE INDEX idx_notifications_user ON notifications (user_id);

CREATE INDEX idx_notifications_unread ON notifications (user_id, is_read);

CREATE INDEX idx_attendance_lesson ON attendance (lesson_id);

CREATE INDEX idx_quiz_attempts_user ON quiz_attempts (user_id);

COMMIT;

-- Display success message
SELECT
    'LMS Database Schema Created Successfully!' AS Status;