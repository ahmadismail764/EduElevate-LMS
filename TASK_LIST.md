# EduElevate LMS - Task List

## Project Goal

Complete Module 1: User Management with Basic Authentication (JWT tokens, password hashing, Spring Security) before proceeding to Course Management features.

## ✅ COMPLETED

- [x] Removed all authentication-related code to start fresh
- [x] Created minimal SecurityConfig that allows all requests
- [x] Added PasswordEncoder bean for BCrypt password hashing
- [x] Verified all entities (Student, Admin, Instructor) have proper JPA mapping
- [x] Confirmed all repositories have findByUsername, findByEmail, existsByUsername, existsByEmail methods
- [x] All service implementations have PasswordEncoder injection and password hashing
- [x] Server successfully starts and runs on localhost:8080
- [x] First two API endpoints working:
  - ✅ GET /api/students (returns empty array)
  - ✅ POST /api/students (creates student)

## 🔄 CURRENT PRIORITY

### 1. Test All CRUD Endpoints

**Students:**

- [x] POST /api/students (create)
- [x] GET /api/students (list all)
- [ ] GET /api/students/{id} (get by ID)
- [ ] PUT /api/students/{id} (update)
- [ ] DELETE /api/students/{id} (delete)

**Admins:**

- [ ] POST /api/admins (create)
- [ ] GET /api/admins (list all)
- [ ] GET /api/admins/{id} (get by ID)
- [ ] PUT /api/admins/{id} (update)
- [ ] DELETE /api/admins/{id} (delete)

**Instructors:**

- [ ] POST /api/instructors (create)
- [ ] GET /api/instructors (list all)
- [ ] GET /api/instructors/{id} (get by ID)
- [ ] PUT /api/instructors/{id} (update)
- [ ] DELETE /api/instructors/{id} (delete)

## 📋 PENDING TASKS

### 2. Fix Error Handling

- [ ] Add proper exception classes for duplicate username/email
- [ ] Implement global exception handler to return 400 Bad Request instead of 500 Internal Server Error
- [ ] Add validation error responses

### 3. Implement JWT Authentication System

- [ ] Create JWT utility classes
- [ ] Implement authentication controllers
- [ ] Add JWT filters
- [ ] Configure Spring Security for JWT

### 4. Add Role-Based Authorization

- [ ] Students can access only their own data
- [ ] Admins can access everything
- [ ] Instructors can access their courses and students

### 5. Build Core LMS Features

- [ ] Course Management Module
- [ ] Enrollment System
- [ ] Progress Tracking

## 🐛 KNOWN ISSUES

- **Duplicate username handling**: Currently throws 500 Internal Server Error instead of proper 400 Bad Request
- **Missing validation**: Need comprehensive input validation for all DTOs
- **Non-existent ID handling**: When a request comes for an ID that doesn't exist, should return 404 Not Found instead of 500 Internal Server Error

## 📁 Project Structure

- **Entities**: Student, Admin, Instructor (all with password fields)
- **Repositories**: All with custom query methods
- **Services**: All with PasswordEncoder and business logic
- **Controllers**: REST endpoints for all entities
- **DTOs**: Create/Update/Response DTOs for all entities
- **Security**: Minimal config allowing all requests + PasswordEncoder

## 🎯 Architecture Goals

- Vertical slicing architecture
- Enterprise-grade security (SQL injection protection, IDOR prevention)
- BCrypt password hashing
- JWT token-based authentication
- Role-based authorization
