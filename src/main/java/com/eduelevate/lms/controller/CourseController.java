package com.eduelevate.lms.controller;

import com.eduelevate.lms.dto.course.*;
import com.eduelevate.lms.security.SecurityUtils;
import com.eduelevate.lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    
    private final CourseService courseService;    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponseDto> createCourse(@Valid @RequestBody CourseCreateDto createDto) {
        log.info("Creating course: {}", createDto.getTitle());
        
        // Instructors can only create courses for themselves
        Integer currentUserId = SecurityUtils.getCurrentUserId();
        if (!currentUserId.equals(createDto.getInstructorId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CourseResponseDto response = courseService.createCourse(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
      @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponseDto> getCourse(@PathVariable Integer courseId) {
        CourseResponseDto response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(response);
    }
      @GetMapping
    public ResponseEntity<?> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "true") boolean paginated) {
        
        if (paginated) {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<CourseResponseDto> coursePage = courseService.getAllCourses(pageable);
            return ResponseEntity.ok(coursePage);
        } else {
            List<CourseResponseDto> courses = courseService.getAllCourses();
            return ResponseEntity.ok(courses);
        }
    }
      @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('INSTRUCTOR') and @courseServiceImpl.getCourseById(#courseId).instructorId == authentication.principal.userId)")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Integer courseId,
            @Valid @RequestBody CourseUpdateDto updateDto) {
        log.info("Updating course: {}", courseId);
        CourseResponseDto response = courseService.updateCourse(courseId, updateDto);
        return ResponseEntity.ok(response);
    }
      @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer courseId) {
        log.info("Deleting course: {}", courseId);
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
      @GetMapping("/search")
    public ResponseEntity<List<CourseResponseDto>> searchCourses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer instructorId,
            @RequestParam(required = false) Integer minWeeks,
            @RequestParam(required = false) Integer maxWeeks,
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        
        List<CourseResponseDto> courses;
        
        if (title != null && !title.trim().isEmpty()) {
            courses = courseService.searchCoursesByTitle(title.trim());
        } else if (instructorId != null) {
            courses = courseService.getCoursesByInstructor(instructorId);
        } else if (minWeeks != null && maxWeeks != null) {
            courses = courseService.getCoursesByDuration(minWeeks, maxWeeks);
        } else if (availableOnly) {
            courses = courseService.getCoursesWithAvailableSpots();
        } else {
            courses = courseService.getAllCourses();
        }
        
        return ResponseEntity.ok(courses);
    }
      @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<CourseResponseDto>> getCoursesByInstructor(@PathVariable Integer instructorId) {
        List<CourseResponseDto> courses = courseService.getCoursesByInstructor(instructorId);
        return ResponseEntity.ok(courses);
    }
      // Enrollment endpoints
    @PostMapping("/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<EnrollmentResponseDto> enrollInCourse(
            @PathVariable Integer courseId,
            @RequestParam(required = false) Integer studentId) {
        
        // If student role, use their own ID
        if (SecurityUtils.hasRole("STUDENT") && studentId == null) {
            studentId = SecurityUtils.getCurrentUserId();
        } else if (SecurityUtils.hasRole("STUDENT") && !SecurityUtils.getCurrentUserId().equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        if (studentId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Enrolling student {} in course {}", studentId, courseId);
        EnrollmentResponseDto response = courseService.enrollStudent(courseId, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
      @DeleteMapping("/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> unenrollFromCourse(
            @PathVariable Integer courseId,
            @RequestParam(required = false) Integer studentId) {
        
        // If student role, use their own ID
        if (SecurityUtils.hasRole("STUDENT") && studentId == null) {
            studentId = SecurityUtils.getCurrentUserId();
        } else if (SecurityUtils.hasRole("STUDENT") && !SecurityUtils.getCurrentUserId().equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        if (studentId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Unenrolling student {} from course {}", studentId, courseId);
        courseService.unenrollStudent(courseId, studentId);
        return ResponseEntity.noContent().build();
    }
      @GetMapping("/{courseId}/enrollments")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('INSTRUCTOR') and @courseServiceImpl.getCourseById(#courseId).instructorId == authentication.principal.userId)")
    public ResponseEntity<List<EnrollmentResponseDto>> getCourseEnrollments(@PathVariable Integer courseId) {
        List<EnrollmentResponseDto> enrollments = courseService.getCourseEnrollments(courseId);
        return ResponseEntity.ok(enrollments);
    }
      @GetMapping("/{courseId}/stats")
    public ResponseEntity<Map<String, Object>> getCourseStats(@PathVariable Integer courseId) {
        Integer enrollmentCount = courseService.getCourseEnrollmentCount(courseId);
        Integer availableSpots = courseService.getCourseAvailableSpots(courseId);
        
        Map<String, Object> stats = Map.of(
            "courseId", courseId,
            "currentEnrollments", enrollmentCount,
            "availableSpots", availableSpots,
            "isFull", availableSpots == 0
        );
        
        return ResponseEntity.ok(stats);
    }
      // Student-specific endpoints
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('STUDENT') and #studentId == authentication.principal.userId)")
    public ResponseEntity<List<CourseResponseDto>> getStudentCourses(@PathVariable Integer studentId) {
        List<CourseResponseDto> courses = courseService.getStudentCourses(studentId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}/students/{studentId}/enrolled")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or (hasRole('STUDENT') and #studentId == authentication.principal.userId)")
    public ResponseEntity<Map<String, Object>> checkStudentEnrollment(
            @PathVariable Integer courseId, 
            @PathVariable Integer studentId) {
        boolean isEnrolled = courseService.isStudentEnrolled(courseId, studentId);
        Map<String, Object> response = Map.of(
            "courseId", courseId,
            "studentId", studentId,
            "enrolled", isEnrolled
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}/enrollment-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or (hasRole('STUDENT') and #studentId == authentication.principal.userId)")
    public ResponseEntity<Map<String, Object>> checkStudentEnrollmentByQuery(
            @PathVariable Integer courseId, 
            @RequestParam Integer studentId) {
        boolean isEnrolled = courseService.isStudentEnrolled(courseId, studentId);
        Map<String, Object> response = Map.of(
            "courseId", courseId,
            "studentId", studentId,
            "enrolled", isEnrolled
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{courseId}/students/{studentId}/unenroll")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> unenrollStudentFromCourse(
            @PathVariable Integer courseId,
            @PathVariable Integer studentId) {
        
        // If student role, ensure they can only unenroll themselves
        if (SecurityUtils.hasRole("STUDENT") && !SecurityUtils.getCurrentUserId().equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        log.info("Unenrolling student {} from course {}", studentId, courseId);
        courseService.unenrollStudent(courseId, studentId);
        return ResponseEntity.noContent().build();
    }
}
