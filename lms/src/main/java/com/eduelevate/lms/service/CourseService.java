package com.eduelevate.lms.service;

import com.eduelevate.lms.dto.course.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    
    // Course CRUD operations
    CourseResponseDto createCourse(CourseCreateDto createDto);
    CourseResponseDto getCourseById(Integer courseId);
    List<CourseResponseDto> getAllCourses();
    Page<CourseResponseDto> getAllCourses(Pageable pageable);
    CourseResponseDto updateCourse(Integer courseId, CourseUpdateDto updateDto);
    void deleteCourse(Integer courseId);
    
    // Course search and filtering
    List<CourseResponseDto> getCoursesByInstructor(Integer instructorId);
    List<CourseResponseDto> searchCoursesByTitle(String title);
    List<CourseResponseDto> getCoursesWithAvailableSpots();
    List<CourseResponseDto> getCoursesByDuration(Integer minWeeks, Integer maxWeeks);
    
    // Enrollment operations
    EnrollmentResponseDto enrollStudent(Integer courseId, Integer studentId);
    void unenrollStudent(Integer courseId, Integer studentId);
    List<EnrollmentResponseDto> getCourseEnrollments(Integer courseId);
    List<CourseResponseDto> getStudentCourses(Integer studentId);
    boolean isStudentEnrolled(Integer courseId, Integer studentId);
    
    // Course statistics
    Integer getCourseEnrollmentCount(Integer courseId);
    Integer getCourseAvailableSpots(Integer courseId);
}
