# Project Timeline

## Overview

This document tracks the progress of building the Learning Management System (LMS) using Spring Boot. Tasks are modularized into components for better management and visibility.

---

## Completed Tasks

### Foundation Setup

- Maven dependencies added:
  - Spring Boot Web, JPA, Security, Validation, Mail, JWT, File Upload, Lombok, MySQL Connector.
- `application.properties` configured:
  - Database connection.
  - Security disabled for development.
  - File upload and mail settings preset.

### Student Module

- **Entity Layer:** `Student.java` created with JPA annotations and Lombok.
- **Repository Layer:** `StudentRepository.java` implemented with JpaRepository and custom queries.
- **Service Layer:**
  - `StudentService.java` interface designed.
  - `StudentServiceImpl.java` implemented with DTO mapping.
- **Controller Layer:** `StudentController.java` created with 8 REST endpoints.
- **DTO Layer:** `StudentResponseDto.java`, `CreateStudentDto.java`, `UpdateStudentDto.java` created.

---

## Pending Tasks

### Module 1: User Management System

- Test Student API endpoints with Postman.
- Create Admin entity:
  - Database table, entity, repository, service, controller, DTOs.
- Create Instructor entity (similar to Admin).
- Implement basic authentication:
  - Login/logout.
  - Entity-based access control.

### Module 2: Course Management System

- Create Course entity.
- Create Lesson entity.
- Implement Enrollment system.
- Add File upload functionality.

### Module 3: Assessment System

- Develop Quiz system.
- Build Assignment system.
- Implement Grading system.

### Module 4: Advanced Features

- Attendance system.
- Notification system.
- Performance tracking.

### Module 5: Security & Polish

- Advanced authentication:
  - JWT.
  - Password encryption.
- Authorization.
- Error handling.
- API documentation.
